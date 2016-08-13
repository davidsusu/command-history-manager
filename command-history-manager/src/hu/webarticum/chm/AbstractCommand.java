package hu.webarticum.chm;

import java.util.ArrayList;
import java.util.List;

abstract public class AbstractCommand implements Command {
	
	private boolean executed = false;
	
	private List<Command> attachedCommands = new ArrayList<Command>();
	
	@Override
	public boolean execute() {
		if (executed) {
			return false;
		} else {
			return _execute();
		}
	}

	@Override
	public boolean rollBack() {
		if (!executed) {
			return false;
		} else {
			return _rollBack();
		}
	}

	@Override
	public boolean isExecuted() {
		return executed;
	}

	@Override
	public boolean attachCommand(Command command) {
		if (executed && !command.isExecuted()) {
			if (!command.execute()) {
				return false;
			}
		} else if (!executed && command.isExecuted()) {
			if (!command.rollBack()) {
				return false;
			}
		}
		attachedCommands.add(command);
		return true;
	}
	
	abstract protected boolean _execute();
	
	abstract protected boolean _rollBack();
	
}
