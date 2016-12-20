package hu.webarticum.chm;

import java.util.LinkedList;

public class CommandQueue implements History {
	
	private int maximumCapacity;
	
	private LinkedList<Command> queue = new LinkedList<Command>();
	
	private int position = 0;
	
	public CommandQueue() {
		this(-1);
	}
	
	public CommandQueue(int maximumCapacity) {
		this.maximumCapacity = maximumCapacity;
	}
	
	@Override
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

	@Override
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

	@Override
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

	@Override
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

	@Override
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

	@Override
	public boolean contains(Command command) {
		return queue.contains(command);
	}

	@Override
	public boolean moveTo(Command command) {
		int targetPosition = queue.indexOf(command);
		if (targetPosition == (-1)) {
			return false;
		}
		
		if (targetPosition > position) {
			for (int i = position; i < targetPosition; i++) {
				if (!queue.get(i).execute()) {
					position = i;
					return false;
				}
			}
		} else if (targetPosition < position) {
			for (int i = position - 1; i >= targetPosition; i--) {
				if (!queue.get(i).rollBack()) {
					position = i + 1;
					return false;
				}
			}
		}
		
		return true;
	}
	
}
