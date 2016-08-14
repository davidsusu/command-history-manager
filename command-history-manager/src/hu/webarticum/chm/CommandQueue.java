package hu.webarticum.chm;

import java.util.LinkedList;

public class CommandQueue {
	
	private int maximumCapacity;
	
	private LinkedList<Command> queue = new LinkedList<Command>();
	
	private int position = 0;
	
	public CommandQueue() {
		this(-1);
	}
	
	public CommandQueue(int maximumCapacity) {
		if (maximumCapacity < 1) {
			throw new IllegalArgumentException("maximumCapacity must be greater than zero, " + maximumCapacity + " given");
		}
		this.maximumCapacity = maximumCapacity;
	}
	
	public boolean executeAsNext(Command command) {
		if (command.execute()) {
			while (queue.size() > position) {
				queue.remove(position);
			}
			while (queue.size() > maximumCapacity - 1) {
				queue.removeFirst();
				position--;
			}
			queue.add(command);
			position++;
			return true;
		} else {
			return false;
		}
	}

	public boolean hasNextCommand() {
		return (queue.size() > position);
	}
	
	public Command getNextCommand() {
		if (hasNextCommand()) {
			return queue.get(position);
		} else {
			return null;
		}
	}
	
	public boolean hasPreviousCommand() {
		return (position > 0);
	}

	public Command getPreviousCommand() {
		if (hasNextCommand()) {
			return queue.get(position - 1);
		} else {
			return null;
		}
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public boolean executeNext() {
		if (!hasNextCommand()) {
			return false;
		} else {
			Command command = getNextCommand();
			if (!command.execute()) {
				return false;
			} else {
				position++;
				return true;
			}
		}
	}

	public boolean rollBackPrevious() {
		if (!hasPreviousCommand()) {
			return false;
		} else {
			Command command = getPreviousCommand();
			if (!command.rollBack()) {
				return false;
			} else {
				position--;
				return true;
			}
		}
	}
	
}
