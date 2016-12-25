package hu.webarticum.chm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ComplexHistory implements History {

	private int capacity;

	private boolean gcOnInsert;
	
	private Node rootNode = new Node(null, null);
	
	private Node previousNode = rootNode;
	
	private final List<Listener> listeners = new ArrayList<>(1);
	
	public ComplexHistory() {
		this(-1, false);
	}

	public ComplexHistory(int capacity) {
		this(capacity, true);
	}

	public ComplexHistory(int capacity, boolean gcOnInsert) {
		this.capacity = capacity;
		this.gcOnInsert = gcOnInsert;
	}
	
	@Override
	public boolean executeAsNext(Command command) {
		if (!command.execute()) {
			return false;
		}
		
		previousNode = previousNode.branch(command);
		onChanged(Listener.OperationType.INSERT);
		return true;
	}

	@Override
	public boolean hasNextCommand() {
		return (previousNode.selectedChild != null);
	}
	
	@Override
	public Command getNextCommand() {
		if (previousNode.selectedChild == null) {
			return null;
		}
		
		return previousNode.selectedChild.command;
	}
	
	@Override
	public boolean hasPreviousCommand() {
		return (previousNode != rootNode);
	}

	@Override
	public Command getPreviousCommand() {
		return previousNode.command;
	}

	@Override
	public boolean executeNext() {
		if (previousNode.selectedChild == null) {
			return false;
		}
		
		if (!previousNode.selectedChild.command.execute()) {
			return false;
		}
		
		previousNode = previousNode.selectedChild;
		onChanged(Listener.OperationType.REDO);
		return true;
	}

	@Override
	public boolean rollBackPrevious() {
		if (previousNode == rootNode) {
			return false;
		}
		
		if (!previousNode.command.rollBack()) {
			return false;
		}
		
		previousNode = previousNode.parent;
		onChanged(Listener.OperationType.UNDO);
		return true;
	}
	
	@Override
	public boolean contains(Command command) {
		return (lookUp(command) != null);
	}

	@Override
	public boolean moveBefore(Command command) {
		return moveTo(command, true);
	}
	
	@Override
	public boolean moveAfter(Command command) {
		return moveTo(command, false);
	}
	
	private boolean moveTo(Command command, boolean before) {
		if (previousNode.command == command) {
			return true;
		}
		
		Node commandNode = lookUp(command);
		
		if (commandNode == null) {
			return false;
		}
		
		List<Node> commandPath = commandNode.getPath();
		
		if (commandPath.get(0) != rootNode) {
			return false;
		}
		
		List<Node> currentPath = previousNode.getPath();
		
		int commandPathSize = commandPath.size();
		int currentPathSize = currentPath.size();
		int commonSize = Math.min(commandPathSize, currentPathSize);
		
		int branchPosition = 0;
		
		for (int i = 0; i < commonSize; i++) {
			if (commandPath.get(i) != currentPath.get(i)) {
				break;
			}
			branchPosition = i + 1;
		}
		
		for (int i = currentPathSize - 1; i >= branchPosition; i--) {
			Node currentPathNode = currentPath.get(i);
			if (!currentPathNode.command.rollBack()) {
				previousNode = currentPathNode;
				return false;
			}
		}
		
		previousNode = currentPath.get(branchPosition - 1);
		
		if (before && commandPathSize == branchPosition) {
			if (!command.rollBack()) {
				return false;
			}
			previousNode = commandNode.parent;
		} else {
			int targetPosition = before ? commandPathSize - 1 : commandPathSize;
			for (int i = branchPosition; i < targetPosition; i++) {
				Node commandPathNode = commandPath.get(i);
				if (!commandPathNode.command.execute()) {
					return false;
				}
				commandPathNode.parent.select(commandPathNode);
				previousNode = commandPathNode;
			}
		}
		
		onChanged(Listener.OperationType.MOVE);
		return true;
	}
	
	@Override
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	@Override
	public boolean removeListener(Listener listener) {
		return listeners.remove(listener);
	}

	public void setCapacity(int capacity) {
		setCapacity(capacity, false);
	}

	public void setCapacity(int capacity, boolean forceGc) {
		this.capacity = capacity;
		if (forceGc || gcOnInsert) {
			gc();
		}
	}
	
	public void setGcOnInsert(boolean gcOnInsert) {
		this.gcOnInsert = gcOnInsert;
	}
	
	public void gc() {
		if (capacity >= 0) {
			List<Node> aboveNodes = new ArrayList<Node>();
			Node parentNode = previousNode;
			while (parentNode != null) {
				aboveNodes.add(parentNode);
				parentNode = parentNode.parent;
			}

			List<Node> belowNodes = new ArrayList<Node>();
			Node childNode = previousNode;
			while (childNode.selectedChild != null) {
				childNode = childNode.selectedChild;
				belowNodes.add(childNode);
			}
			
			boolean firstIteration = true;
			int remainingCapacity = (capacity == Integer.MAX_VALUE) ? capacity : capacity + 1;
			
			while (true) {
				int aboveCount = aboveNodes.size();
				int belowCount = belowNodes.size();
				
				int levelCount = aboveCount + belowCount;
				
				if (levelCount == 0) {
					break;
				}
				
				if (levelCount >= remainingCapacity) {
					List<Node> nodesToRipOut = new ArrayList<>();
					
					int keepBelowCount = (remainingCapacity + 1) / 2 - 1;
					if (keepBelowCount > belowCount) {
						keepBelowCount = belowCount;
					}
					
					int keepAboveCount = remainingCapacity - keepBelowCount;
					if (keepAboveCount > aboveCount) {
						keepAboveCount = aboveCount;
						keepBelowCount = remainingCapacity - keepAboveCount;
					}
					
					for (int i = 0; i < keepBelowCount; i++) {
						Node belowNode = belowNodes.get(i);
						List<Node> reorderedChildren = gcGetChildrenReordered(belowNode);
						for (Node belowChildNode: reorderedChildren) {
							if (!firstIteration || belowChildNode != belowNode.selectedChild) {
								nodesToRipOut.add(belowChildNode);
							}
						}
					}
					for (int i = keepBelowCount; i < belowCount; i++) {
						Node belowNode = belowNodes.get(i);
						nodesToRipOut.add(belowNode);
						if (firstIteration) {
							break;
						}
					}

					for (int i = 0; i < keepAboveCount; i++) {
						Node aboveNode = aboveNodes.get(i);
						List<Node> reorderedChildren = gcGetChildrenReordered(aboveNode);
						for (Node aboveChildNode: reorderedChildren) {
							if (!firstIteration || aboveChildNode != aboveNode.selectedChild) {
								nodesToRipOut.add(aboveChildNode);
							}
						}
					}
					if (firstIteration) {
						if (keepAboveCount < aboveCount) {
							Node lastKeepingAboveNode = aboveNodes.get(keepAboveCount - 1);
							nodesToRipOut.add(lastKeepingAboveNode.parent);
							rootNode = lastKeepingAboveNode;
							rootNode.children.clear();
							if (rootNode.selectedChild != null) {
								rootNode.children.add(rootNode.selectedChild);
							}
							rootNode.command = null;
						}
					} else {
						for (int i = keepAboveCount; i < aboveCount; i++) {
							Node aboveNode = aboveNodes.get(i);
							nodesToRipOut.add(aboveNode);
						}
					}
					
					for (Node nodeToRipOut: nodesToRipOut) {
						nodeToRipOut.ripOut();
					}
					
					break;
				}
				
				List<Node> newAboveNodes = new ArrayList<>();
				for (Node aboveNode: aboveNodes) {
					List<Node> reorderedChildren = gcGetChildrenReordered(aboveNode);
					if (firstIteration && aboveNode.selectedChild != null) {
						reorderedChildren.remove(aboveNode.selectedChild);
					}
					newAboveNodes.addAll(reorderedChildren);
				}

				List<Node> newBelowNodes = new ArrayList<>();
				for (Node belowNode: belowNodes) {
					List<Node> reorderedChildren = gcGetChildrenReordered(belowNode);
					if (firstIteration && belowNode.selectedChild != null) {
						reorderedChildren.remove(belowNode.selectedChild);
					}
					newAboveNodes.addAll(reorderedChildren);
				}
				
				aboveNodes = newAboveNodes;
				belowNodes = newBelowNodes;
				
				remainingCapacity -= levelCount;
				firstIteration = false;
			}
		}
	}

	private List<Node> gcGetChildrenReordered(Node node) {
		Node selectedChild = node.selectedChild;
		List<Node> result = new ArrayList<>(node.children);
		if (!result.isEmpty()) {
			Collections.reverse(result);
			if (selectedChild != null) {
				Node firstItem = result.get(0);
				if (firstItem != selectedChild) {
					int selectedIndex = result.indexOf(selectedChild);
					if (selectedIndex != (-1)) {
						result.set(0, selectedChild);
						result.set(selectedIndex, firstItem);
					}
				}
			}
		}
		return result;
	}
	
	private Node lookUp(Command command) {
		if (previousNode.command == command) {
			return previousNode;
		}
		
		List<Node> level = new ArrayList<Node>();
		level.add(rootNode);
		while (!level.isEmpty()) {
			List<Node> nextLevel = new ArrayList<Node>();
			for (Node node: level) {
				for (Node childNode: node.children) {
					if (childNode.command == command) {
						return childNode;
					}
					nextLevel.add(childNode);
				}
			}
			level = nextLevel;
		}
		
		return null;
	}

	private void onChanged(Listener.OperationType operationType) {
		if (operationType == Listener.OperationType.INSERT && gcOnInsert) {
			gc();
		}
		
		for (Listener listener: listeners) {
			listener.changed(this, operationType);
		}
	}
	
	private class Node {
		
		Node parent;
		
		Command command;
		
		List<Node> children = new ArrayList<Node>();
		
		Node selectedChild = null;
		
		Node(Node parent, Command command) {
			this.parent = parent;
			this.command = command;
		}
		
		Node branch(Command command) {
			selectedChild = new Node(this, command);
			children.add(selectedChild);
			return selectedChild;
		}
		
		List<Node> getPath() {
			List<Node> parents = new ArrayList<>();
			parents.add(this);
			Node parent = this;
			while (parent.parent != null) {
				parent = parent.parent;
				parents.add(parent);
			}
			Collections.reverse(parents);
			return parents;
		}
		
		boolean select(Node child) {
			if (!children.contains(child)) {
				return false;
			}
			
			selectedChild = child;
			return true;
		}
		
		void ripOut() {
			if (parent != null) {
				parent.children.remove(this);
				if (parent.selectedChild == this) {
					parent.selectedChild = null;
				}
				parent = null;
			}
			selectedChild = null;
			for (Node childNode: children) {
				childNode.parent = null;
			}
		}
		
		@Override
		public String toString() {
			return "Node of " + command;
		}
		
	}
	
}
