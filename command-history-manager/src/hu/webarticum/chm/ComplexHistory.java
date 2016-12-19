package hu.webarticum.chm;

import java.util.ArrayList;
import java.util.List;


// TODO: limit size...
// TODO: test...

public class ComplexHistory implements History {
	
	private Node rootNode = null; // TODO: fake final rootNode
	
	private Node previousNode = null;
	
	@Override
	public boolean executeAsNext(Command command) {
		if (previousNode == null) {
			// TODO
		} else {
			// TODO
		}
		return false;
	}

	@Override
	public boolean hasNextCommand() {
		if (previousNode != null) {
			return (previousNode.selectedChild != null);
		} else {
			return (rootNode != null);
		}
	}

	@Override
	public boolean hasPreviousCommand() {
		return (previousNode != null);
	}

	@Override
	public boolean executeNext() {
		Node subNode = null;
		if (previousNode != null) {
			subNode = previousNode.selectedChild;
		} else if (rootNode != null) {
			subNode = rootNode;
		}
		if (subNode != null) {
			if (subNode.command.execute()) {
				previousNode = subNode;
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean rollBackPrevious() {
		if (previousNode != null) {
			if (previousNode.command.rollBack()) {
				previousNode = previousNode.parent;
				return true;
			}
		}
		return false;
	}

	class Node {
		
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
		
		void cut() {
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
