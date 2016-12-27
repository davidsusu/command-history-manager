package hu.webarticum.chm;

import java.util.ArrayList;
import java.util.List;

public class CommandAggregation extends AbstractCommand {

	private List<Command> commands = new ArrayList<>();
	
	private boolean closed = false;
	
	public boolean add(Command command) {
		if (closed) {
			return false;
		}
		
		if (isExecuted()) {
			if (!command.isExecuted()) {
				if (!command.execute()) {
					return false;
				}
			}
		} else {
			if (command.isExecuted()) {
				if (!command.rollBack()) {
					return false;
				}
			}
		}
		
		commands.add(command);
		return true;
	}
	
	public void close() {
		closed = true;
	}
	
	@Override
	protected boolean _execute() {
		for (Command command: commands) {
			if (!command.execute()) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected boolean _rollBack() {
		for (int i = commands.size() - 1; i >= 0; i--) {
			if (!commands.get(i).rollBack()) {
				return false;
			}
		}
		return true;
	}

}
