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
		this.maximumCapacity = maximumCapacity;
	}
	
	public boolean executeAsNext(Command command) {
		if (command.execute()) {
			while (queue.size() > position) {
				queue.remove(position);
			}
			queue.add(command);
			position++;
			if (maximumCapacity >= 0) {
				while (queue.size() > maximumCapacity) {
					queue.removeFirst();
					position--;
				}
			}
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
		if (hasPreviousCommand()) {
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
