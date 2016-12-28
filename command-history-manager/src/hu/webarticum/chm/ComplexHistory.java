package hu.webarticum.chm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


public class ComplexHistory implements History {

	private int capacity;

	private boolean gcOnInsert;
	
	private InternalNode rootNode = new InternalNode(null, null);
	
	private InternalNode previousNode = rootNode;
	
	private final List<Listener> listeners = new ArrayList<Listener>(1);
	
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
	public Iterator<Command> iterator() {
		return new SelectedRouteIterator(rootNode);
	}

	public CommandNode getRootCommandNode() {
		return new CommandNode(rootNode);
	}

	public CommandNode getPreviousCommandNode() {
		return new CommandNode(previousNode);
	}
	
	@Override
	public boolean isEmpty() {
		return rootNode.children.isEmpty();
	}
	
	@Override
	public boolean contains(Command command) {
		return (lookUp(command) != null);
	}

	@Override
	public boolean addAndExecute(Command command) {
		if (!command.execute()) {
			return false;
		}
		
		previousNode = previousNode.branch(command);
		onChanged(Listener.OperationType.INSERT);
		return true;
	}

	@Override
	public boolean hasNext() {
		return (previousNode.selectedChild != null);
	}
	
	@Override
	public Command getNext() {
		if (previousNode.selectedChild == null) {
			return null;
		}
		
		return previousNode.selectedChild.command;
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
	public boolean hasPrevious() {
		return (previousNode != rootNode);
	}

	@Override
	public Command getPrevious() {
		return previousNode.command;
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
		
		InternalNode commandNode = lookUp(command);
		
		if (commandNode == null) {
			return false;
		}
		
		List<InternalNode> commandPath = commandNode.getPath();
		
		if (commandPath.get(0) != rootNode) {
			return false;
		}
		
		List<InternalNode> currentPath = previousNode.getPath();
		
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
			InternalNode currentPathNode = currentPath.get(i);
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
				InternalNode commandPathNode = commandPath.get(i);
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
			boolean changed = false;
			
			List<InternalNode> aboveNodes = new ArrayList<InternalNode>();
			InternalNode parentNode = previousNode;
			while (parentNode != null) {
				aboveNodes.add(parentNode);
				parentNode = parentNode.parent;
			}

			List<InternalNode> belowNodes = new ArrayList<InternalNode>();
			InternalNode childNode = previousNode;
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
					List<InternalNode> nodesToRipOut = new ArrayList<InternalNode>();
					
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
						InternalNode belowNode = belowNodes.get(i);
						List<InternalNode> reorderedChildren = gcGetChildrenReordered(belowNode);
						for (InternalNode belowChildNode: reorderedChildren) {
							if (!firstIteration || belowChildNode != belowNode.selectedChild) {
								nodesToRipOut.add(belowChildNode);
							}
						}
					}
					for (int i = keepBelowCount; i < belowCount; i++) {
						InternalNode belowNode = belowNodes.get(i);
						nodesToRipOut.add(belowNode);
						if (firstIteration) {
							break;
						}
					}

					for (int i = 0; i < keepAboveCount; i++) {
						InternalNode aboveNode = aboveNodes.get(i);
						List<InternalNode> reorderedChildren = gcGetChildrenReordered(aboveNode);
						for (InternalNode aboveChildNode: reorderedChildren) {
							if (!firstIteration || aboveChildNode != aboveNode.selectedChild) {
								nodesToRipOut.add(aboveChildNode);
							}
						}
					}
					if (firstIteration) {
						if (keepAboveCount < aboveCount) {
							InternalNode lastKeepingAboveNode = aboveNodes.get(keepAboveCount - 1);
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
							InternalNode aboveNode = aboveNodes.get(i);
							nodesToRipOut.add(aboveNode);
						}
					}
					
					for (InternalNode nodeToRipOut: nodesToRipOut) {
						nodeToRipOut.ripOut();
						changed = true;
					}
					
					break;
				}
				
				List<InternalNode> newAboveNodes = new ArrayList<InternalNode>();
				for (InternalNode aboveNode: aboveNodes) {
					List<InternalNode> reorderedChildren = gcGetChildrenReordered(aboveNode);
					if (firstIteration && aboveNode.selectedChild != null) {
						reorderedChildren.remove(aboveNode.selectedChild);
					}
					newAboveNodes.addAll(reorderedChildren);
				}

				List<InternalNode> newBelowNodes = new ArrayList<InternalNode>();
				for (InternalNode belowNode: belowNodes) {
					List<InternalNode> reorderedChildren = gcGetChildrenReordered(belowNode);
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
			
			if (changed) {
				onChanged(Listener.OperationType.CHANGE);
			}
		}
	}

	private List<InternalNode> gcGetChildrenReordered(InternalNode node) {
		InternalNode selectedChild = node.selectedChild;
		List<InternalNode> result = new ArrayList<InternalNode>(node.children);
		if (!result.isEmpty()) {
			Collections.reverse(result);
			if (selectedChild != null) {
				InternalNode firstItem = result.get(0);
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
	
	private InternalNode lookUp(Command command) {
		if (command == null) {
			return null;
		}
		
		if (previousNode.command == command) {
			return previousNode;
		}
		
		List<InternalNode> level = new ArrayList<InternalNode>();
		level.add(rootNode);
		while (!level.isEmpty()) {
			List<InternalNode> nextLevel = new ArrayList<InternalNode>();
			for (InternalNode node: level) {
				for (InternalNode childNode: node.children) {
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
	
	private class InternalNode {
		
		InternalNode parent;
		
		Command command;
		
		List<InternalNode> children = new ArrayList<InternalNode>();
		
		InternalNode selectedChild = null;
		
		InternalNode(InternalNode parent, Command command) {
			this.parent = parent;
			this.command = command;
		}
		
		InternalNode branch(Command command) {
			selectedChild = new InternalNode(this, command);
			children.add(selectedChild);
			return selectedChild;
		}
		
		List<InternalNode> getPath() {
			List<InternalNode> parents = new ArrayList<InternalNode>();
			parents.add(this);
			InternalNode parent = this;
			while (parent.parent != null) {
				parent = parent.parent;
				parents.add(parent);
			}
			Collections.reverse(parents);
			return parents;
		}
		
		boolean select(InternalNode child) {
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
			for (InternalNode childNode: children) {
				childNode.parent = null;
			}
		}
		
		@Override
		public String toString() {
			return "Node of " + command;
		}
		
	}
	
	public class CommandNode {
		
		private final InternalNode node;
		
		private CommandNode(InternalNode node) {
			this.node = node;
		}
		
		public Command getCommand() {
			return node.command;
		}
		
		public Command getSelectedNextCommand() {
			if (node.selectedChild == null) {
				return null;
			}
			return node.selectedChild.command;
		}
		
		public CommandNode getParent() {
			if (node.parent == null) {
				return null;
			}
			return new CommandNode(node.parent);
		}
		
		public List<CommandNode> getChildren() {
			List<CommandNode> result = new ArrayList<CommandNode>(node.children.size());
			for (InternalNode childNode: node.children) {
				result.add(new CommandNode(childNode));
			}
			return result;
		}
		
	}
	
	private class SelectedRouteIterator implements Iterator<Command> {
		
		private InternalNode previousNode;
		
		public SelectedRouteIterator(InternalNode rootNode) {
			this.previousNode = rootNode;
		}

		@Override
		public boolean hasNext() {
			return (previousNode.selectedChild != null);
		}

		@Override
		public Command next() {
			if (previousNode.selectedChild == null) {
				throw new NoSuchElementException();
			}
			previousNode = previousNode.selectedChild;
			return previousNode.command;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
	
}
