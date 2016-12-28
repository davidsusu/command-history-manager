package hu.webarticum.chm;

public interface History extends Iterable<Command> {
	
	public boolean isEmpty();

	public boolean contains(Command command);

	public boolean addAndExecute(Command command);
	
	public boolean hasNext();

	public Command getNext();

	public boolean executeNext();
	
	public boolean hasPrevious();

	public Command getPrevious();
	
	public boolean rollBackPrevious();
	
	public boolean moveBefore(Command command);
	
	public boolean moveAfter(Command command);
	
	public void addListener(Listener listener);

	public boolean removeListener(Listener listener);
	
	public interface Listener {
		
		public enum OperationType {
			INSERT, REDO, UNDO, MOVE
		}
		
		public void changed(History history, OperationType operationType);
		
	}
	
}
