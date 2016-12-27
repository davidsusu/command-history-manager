package hu.webarticum.chm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class LinearHistory implements History {
	
	private int capacity;
	
	private LinkedList<Command> queue = new LinkedList<Command>();
	
	private int position = 0;

	private final List<Listener> listeners = new ArrayList<>(1);
	
	public LinearHistory() {
		this(-1);
	}
	
	public LinearHistory(int capacity) {
		this.capacity = capacity;
	}

	@Override
	public boolean isEmpty() {
		return queue.isEmpty();
	}

	@Override
	public boolean contains(Command command) {
		return queue.contains(command);
	}

	@Override
	public boolean addAndExecute(Command command) {
		if (command.execute()) {
			while (queue.size() > position) {
				queue.remove(position);
			}
			queue.add(command);
			position++;
			onChanged(Listener.OperationType.INSERT);
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean hasNext() {
		return (queue.size() > position);
	}

	@Override
	public Command getNext() {
		if (hasNext()) {
			return queue.get(position);
		} else {
			return null;
		}
	}

	@Override
	public boolean executeNext() {
		if (!hasNext()) {
			return false;
		} else {
			Command command = getNext();
			if (!command.execute()) {
				return false;
			} else {
				position++;
				onChanged(Listener.OperationType.REDO);
				return true;
			}
		}
	}

	@Override
	public boolean hasPrevious() {
		return (position > 0);
	}

	@Override
	public Command getPrevious() {
		if (hasPrevious()) {
			return queue.get(position - 1);
		} else {
			return null;
		}
	}
	
	@Override
	public boolean rollBackPrevious() {
		if (!hasPrevious()) {
			return false;
		} else {
			Command command = getPrevious();
			if (!command.rollBack()) {
				return false;
			} else {
				position--;
				onChanged(Listener.OperationType.UNDO);
				return true;
			}
		}
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
		int commandPosition = queue.indexOf(command);
		if (commandPosition == position) {
			return true;
		} else if (commandPosition == (-1)) {
			return false;
		}
		
		int targetPosition = before ? commandPosition : commandPosition + 1;
		
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
		
		position = targetPosition;

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
		this.capacity = capacity;
		gc();
	}

	private void onChanged(Listener.OperationType operationType) {
		gc();
		
		for (Listener listener: listeners) {
			listener.changed(this, operationType);
		}
	}
	
	private void gc() {
		if (capacity >= 0) {
			while (queue.size() > capacity) {
				queue.removeFirst();
				position--;
			}
		}
	}
	
}
