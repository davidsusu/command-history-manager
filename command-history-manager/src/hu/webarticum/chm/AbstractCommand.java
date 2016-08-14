package hu.webarticum.chm;

abstract public class AbstractCommand implements Command {
	
	private boolean executed = false;
	
	@Override
	public boolean execute() {
		if (executed) {
			return false;
		} else {
			if (_execute()) {
				executed = true;
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
	
	abstract protected boolean _execute();
	
	abstract protected boolean _rollBack();
	
}
