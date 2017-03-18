package hu.webarticum.chm;

import java.util.Iterator;

/**
 * Stores a history of commands based on each other.
 */

public interface History extends Iterable<Command> {
    
    /**
     * Returns an iterator which iterates through the last executed path's commands.
     *
     * @return an iterator which iterates through the last executed path's commands
     */
    @Override
    public Iterator<Command> iterator();
    
    /**
     * Returns {@code true} if this history contains no commands.
     *
     * @return {@code true} if this history contains no commands
     */
    public boolean isEmpty();
    
    /**
     * Returns {@code true} if this list contains the specified element.
     * 
     * @param command the command whose presence in this list is to be tested
     * @return {@code true} if this history contains the specified command
     */
    public boolean contains(Command command);
    
    /**
     * Appends the given command after the last executed one and execute it.
     * 
     * @return {@code true} if the command successfully executed and inserted
     */
    public boolean addAndExecute(Command command);
    
    /**
     * Returns {@code true} if there is a next (most recently rolled back) command.
     * 
     * @return {@code true} if there is a next command
     */
    public boolean hasNext();
    
    /**
     * Returns the next (most recently rolled back) command.
     * 
     * @return the next command if any or null
     */
    public Command getNext();
    
    /**
     * Executes the next (most recently rolled back) command.
     * 
     * @return {@code true} if a next command is successfully executed
     */
    public boolean executeNext();
    
    /**
     * Returns {@code true} if there is a previous (most recently executed) command.
     * 
     * @return {@code true} if there is a previous command
     */
    public boolean hasPrevious();
    
    /**
     * Returns the previous (most recently executed) command.
     * 
     * @return the previous command if any or null
     */
    public Command getPrevious();
    
    /**
     * Rolls back the previous (most recently executed) command.
     * 
     * @return {@code true} if a previous command is successfully rolled back
     */
    public boolean rollBackPrevious();
    
    /**
     * Moves the internal pointer before the given command.
     * Commands in the shortest route between current and given command
     * will be executed or rolled back.
     * 
     * @return {@code true} if the command found and pointer successfully moved
     */
    public boolean moveBefore(Command command);
    
    /**
     * Moves the internal pointer after the given command.
     * Commands in the shortest route between current and given command
     * will be executed or rolled back.
     * 
     * @return {@code true} if the command found and pointer successfully moved
     */
    public boolean moveAfter(Command command);
    
    /**
     * Appends a new listener to this history.
     * 
     * @param listener the new listener
     */
    public void addListener(Listener listener);
    
    /**
     * Removes a listener from this history.
     * 
     * @return {@code true} if the listener found and removed
     */
    public boolean removeListener(Listener listener);
    
    /**
     * Listens to history changes.
     */
    public interface Listener {
        
        /**
         * Type of operation which occurred on the command history
         */
        public enum OperationType {
            
            /**
             * Insertion operation (caused by (@code addAndExecute()))
             */
            INSERT,
            
            /**
             * Explicit single execution operation (caused by {@code executeNext()})
             */
            REDO,
            
            /**
             * Explicit single execution operation (caused by {@code rollBackPrevious()})
             */
            UNDO,
            
            /**
             * Random movement in the history (caused by {@code moveBefore(Command)} or {@code moveAfter(Command)})
             */
            MOVE,
            
            /**
             * Any operation without any execution or roll back
             */
            CHANGE,
            
        }
        
        /**
         * Called when a change occurred in the history.
         * 
         * @param history the history that have been changed
         * @param operationType type of the operation
         */
        public void changed(History history, OperationType operationType);
        
    }
    
}
