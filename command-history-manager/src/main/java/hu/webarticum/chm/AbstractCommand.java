package hu.webarticum.chm;

import java.util.Date;

/**
 * Commons for commands.
 */

public abstract class AbstractCommand implements Command {
    
    private boolean executed = false;
    
    private Date firstExecutionTime = null;
    
    @Override
    public boolean execute() {
        if (executed) {
            return false;
        } else {
            if (_execute()) {
                executed = true;
                firstExecutionTime = new Date();
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
    public void setExecuted(boolean executed) {
        this.executed = executed;
    }

    @Override
    public boolean isExecuted() {
        return executed;
    }
    
    @Override
    public Date getTime() {
        return firstExecutionTime;
    }
    
    protected abstract boolean _execute();
    
    protected abstract boolean _rollBack();
    
}
