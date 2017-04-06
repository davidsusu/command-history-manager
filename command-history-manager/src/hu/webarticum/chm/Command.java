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
     * Explicitly sets executed status, do not perform any execution or roll-backing.
     * 
     * Use this if operation already performed elsewhere.
     * It should not be invoked before this command executed
     * (unless the command's implementation is properly prepared for this).
     */
    public void setExecuted(boolean executed);
    
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
