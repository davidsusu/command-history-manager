package hu.webarticum.chm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * The most simple  history implementation which can store only a single command.
 */

public class SingleCommandHistory implements History {
    
    private Command command = null;
    
    private final List<Listener> listeners = new ArrayList<Listener>(1);
    
    @Override
    public Iterator<Command> iterator() {
        if (command == null) {
            return Collections.<Command>emptyIterator();
        } else {
            return Collections.singletonList(command).iterator();
        }
    }
    
    @Override
    public boolean isEmpty() {
        return (command == null);
    }
    
    @Override
    public boolean contains(Command command) {
        return (command != null && command == this.command);
    }
    
    @Override
    public boolean addAndExecute(Command command) {
        if (!command.execute()) {
            return false;
        }
        
        this.command = command;
        
        return true;
    }
    
    @Override
    public boolean hasNext() {
        return (command != null && !command.isExecuted());
    }
    
    @Override
    public Command getNext() {
        return hasNext() ? command : null;
    }
    
    @Override
    public boolean executeNext() {
        return hasNext() ? command.execute() : false;
    }
    
    @Override
    public boolean hasPrevious() {
        return (command != null && command.isExecuted());
    }
    
    @Override
    public Command getPrevious() {
        return hasPrevious() ? command : null;
    }
    
    @Override
    public boolean rollBackPrevious() {
        return hasPrevious() ? command.rollBack() : false;
    }
    
    @Override
    public boolean moveBefore(Command command) {
        if (command != this.command) {
            return false;
        }
        
        if (!this.command.isExecuted()) {
            return true;
        }
        
        return this.command.rollBack();
    }
    
    @Override
    public boolean moveAfter(Command command) {
        if (command != this.command) {
            return false;
        }
        
        if (this.command.isExecuted()) {
            return true;
        }
        
        return this.command.execute();
    }
    
    @Override
    public void addListener(Listener listener) {
        listeners.add(listener);
    }
    
    @Override
    public boolean removeListener(Listener listener) {
        return listeners.remove(listener);
    }
    
}
