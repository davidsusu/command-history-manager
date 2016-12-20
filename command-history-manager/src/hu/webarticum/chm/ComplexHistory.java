package hu.webarticum.chm;

import java.util.ArrayList;
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
		
		if (!previousNode.command.execute()) {
			return false;
		}
		
		previousNode = previousNode.parent;
		return true;
	}

	@Override
	public boolean contains(Command command) {
		return (lookUp(command) != null);
	}

	@Override
	public boolean moveTo(Command command) {
		
		// TODO
		
		return false;
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
		
		boolean select(Node child) {
			if (!children.contains(child)) {
				return false;
			}
			
			selectedChild = child;
			return true;
		}
		
		void snip() {
			if (parent != null) {
				parent.children.remove(this);
				if (parent.selectedChild == this) {
					parent.selectedChild = null;
				}
				parent = null;
			}
		}
		
	}

}
