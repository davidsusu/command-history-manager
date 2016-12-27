package hu.webarticum.chm;

import java.util.ArrayList;
import java.util.List;

public class SingleCommandHistory implements History {

	private Command command = null;

	private final List<Listener> listeners = new ArrayList<>(1);
	
	@Override
	public boolean isEmpty() {
		return (command == null);
	}

	@Override
	public boolean contains(Command command) {
		return (command != null && command == this.command);
	}

	@Override
	public boolean addAndExecute(Command command) {
		if (!command.execute()) {
			return false;
		}
		
		this.command = command;
		
		return true;
	}

	@Override
	public boolean hasNext() {
		return (command != null && !command.isExecuted());
	}

	@Override
	public Command getNext() {
		return hasNext() ? command : null;
	}

	@Override
	public boolean executeNext() {
		return hasNext() ? command.execute() : false;
	}

	@Override
	public boolean hasPrevious() {
		return (command != null && command.isExecuted());
	}

	@Override
	public Command getPrevious() {
		return hasPrevious() ? command : null;
	}

	@Override
	public boolean rollBackPrevious() {
		return hasPrevious() ? command.rollBack() : false;
	}

	@Override
	public boolean moveBefore(Command command) {
		if (command != this.command) {
			return false;
		}
		
		if (!this.command.isExecuted()) {
			return true;
		}

		return this.command.rollBack();
	}

	@Override
	public boolean moveAfter(Command command) {
		if (command != this.command) {
			return false;
		}
		
		if (this.command.isExecuted()) {
			return true;
		}

		return this.command.execute();
	}

	@Override
	public void addListener(Listener listener) {
		listeners.add(listener);
	}
	
	@Override
	public boolean removeListener(Listener listener) {
		return listeners.remove(listener);
	}

}
