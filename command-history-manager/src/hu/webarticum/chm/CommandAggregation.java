package hu.webarticum.chm;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;

/**
 * Aggregation to execute and roll back multiple commands together.
 */

public class CommandAggregation extends AbstractCommand implements Closeable {
    
    private List<Command> commands = new ArrayList<Command>();
    
    private boolean closed = false;
    
    /**
     * Appends the given command to the end of this aggregation.
     * 
     * If this aggregation is closed, then operation will fail.
     * If this aggregation is not closed and already executed,
     * and the given command is not executed, then the command
     * will be executed immediately.
     * 
     * @return {@code true} if the command successfully appended
     */
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
    
    /**
     * Closes this aggregation.
     * 
     * No new command can be added after this invoked.
     */
    public void close() {
        closed = true;
    }
    
    @Override
    public void setExecuted(boolean executed) {
        for (Command command: commands) {
            command.setExecuted(executed);
        }
        super.setExecuted(executed);
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
