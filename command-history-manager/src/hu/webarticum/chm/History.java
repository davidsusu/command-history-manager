package hu.webarticum.chm;

public interface History {
	
	public boolean executeAsNext(Command command);
	
	public boolean hasNextCommand();

	public Command getNextCommand();
	
	public boolean hasPreviousCommand();

	public Command getPreviousCommand();
	
	public boolean executeNext();
	
	public boolean rollBackPrevious();
	
	public boolean contains(Command command);
	
	public boolean moveTo(Command command);

	public void addListener(Listener listener);

	public boolean removeListener(Listener listener);
	
	public interface Listener {
		
		public enum OperationType {
			INSERT, REDO, UNDO, MOVE
		}
		
		public void changed(History history, OperationType operationType);
		
	}
	
}
