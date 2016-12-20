package hu.webarticum.chm;

public interface History {
	
	public boolean executeAsNext(Command command);
	
	public boolean hasNextCommand();
	
	public boolean hasPreviousCommand();
	
	public boolean executeNext();
	
	public boolean rollBackPrevious();
	
	public boolean contains(Command command);
	
	public boolean moveTo(Command command);
	
}
