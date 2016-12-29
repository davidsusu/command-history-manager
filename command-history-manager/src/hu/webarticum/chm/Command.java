package hu.webarticum.chm;

import java.util.Date;

/**
 * Undoable operation.
 */

public interface Command {
    
    /**
     * Executes this command
     * 
     * @return {@code true} if command was successfully executed
     */
    public boolean execute();
    
    /**
     * Rolls back this command
     * 
     * @return {@code true} if command was successfully rolled back
     */
    public boolean rollBack();
        
    /**
     * Returns {@code true} if command have been already executed and is not rolled back.
     * 
     * @return {@code true} if command have been already executed
     */
    public boolean isExecuted();
    
    /**
     * Returns time when this command was first executed.
     * 
     * @return time when this command was first executed or null
     */
    public Date getTime();
    
}
