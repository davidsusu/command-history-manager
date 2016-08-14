package hu.webarticum.chm;

abstract public class AbstractCommand implements Command {
	
	private boolean executed = false;
	
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
	
	abstract protected boolean _execute();
	
	abstract protected boolean _rollBack();
	
}
