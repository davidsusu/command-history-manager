package hu.webarticum.chm;

public interface Command {
	
	public boolean execute();
	
	public boolean rollBack();
	
	public boolean isExecuted();
	
	public boolean attachCommand(Command command);
	
}
