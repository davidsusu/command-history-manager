package hu.webarticum.chm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


// TODO: limit size...
// TODO: test...

public class ComplexHistory implements History {
	
	private Node rootNode = new Node(null, null);
	
	private Node previousNode = rootNode;
	
	@Override
	public boolean executeAsNext(Command command) {
		if (!command.execute()) {
			return false;
		}
		
		previousNode = previousNode.branch(command);
		return true;
	}

	@Override
	public boolean hasNextCommand() {
		return (previousNode.selectedChild != null);
	}

	@Override
	public boolean hasPreviousCommand() {
		return (previousNode != rootNode);
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
		return true;
	}
	
	@Override
	public Command getPreviousCommand() {
		if (previousNode == null) {
			return null;
		}
		
		return previousNode.command;
	}

	@Override
	public boolean contains(Command command) {
		return (lookUp(command) != null);
	}

	@Override
	public boolean moveTo(Command command) {
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
		
		for (int i = branchPosition; i < commandPathSize; i++) {
			Node commandPathNode = commandPath.get(i);
			if (!commandPathNode.command.execute()) {
				return false;
			}
			commandPathNode.parent.select(commandPathNode);
			previousNode = commandPathNode;
		}
		
		return true;
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
	
	private class Node {
		
		Node parent;
		
		final Command command;
		
		final List<Node> children = new ArrayList<Node>();
		
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
		
		// TODO ...
		@SuppressWarnings("unused")
		void snip() {
			if (parent != null) {
				parent.children.remove(this);
				if (parent.selectedChild == this) {
					parent.selectedChild = null;
				}
				parent = null;
			}
		}
		
		@Override
		public String toString() {
			return "Node of " + command;
		}
		
	}
	
}
