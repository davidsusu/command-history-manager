package hu.webarticum.chm;

import java.util.Date;

/**
 * Commons for commands.
 */

abstract public class AbstractCommand implements Command {
	
	private boolean executed = false;
	
	private Date firstExecutionTime = null;
	
	@Override
	public boolean execute() {
		if (executed) {
			return false;
		} else {
			if (_execute()) {
				executed = true;
				firstExecutionTime = new Date();
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean rollBack() {
		if (!executed) {
			return false;
		} else {
			if (_rollBack()) {
				executed = false;
				return true;
			} else {
				return false;
			}
		}
	}

	@Override
	public boolean isExecuted() {
		return executed;
	}
	
	@Override
	public Date getTime() {
		return firstExecutionTime;
	}
	
	abstract protected boolean _execute();
	
	abstract protected boolean _rollBack();
	
}
