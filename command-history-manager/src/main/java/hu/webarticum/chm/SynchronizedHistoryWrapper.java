package hu.webarticum.chm;

import java.util.Iterator;

/**
 * Synchronized wrapper for concurrently used history managers.
 */

public class SynchronizedHistoryWrapper implements History {
    
    private final History history;
    
    public SynchronizedHistoryWrapper(History history) {
        this.history = history;
    }

    /**
     * Returns an iterator which iterates through the last executed path's commands.
     * 
     * This iterator is not synchronized, so operations should be explicitly synchronized on client code.
     *
     * @return an iterator which iterates through the last executed path's commands
     */
    @Override
    public Iterator<Command> iterator() {
        return history.iterator();
    }

    @Override
    public boolean isEmpty() {
        synchronized (history) {
            return history.isEmpty();
        }
    }

    @Override
    public boolean contains(Command command) {
        synchronized (history) {
            return history.contains(command);
        }
    }

    @Override
    public boolean addAndExecute(Command command) {
        synchronized (history) {
            return history.addAndExecute(command);
        }
    }

    @Override
    public boolean hasNext() {
        synchronized (history) {
            return history.hasNext();
        }
    }

    @Override
    public Command getNext() {
        synchronized (history) {
            return history.getNext();
        }
    }

    @Override
    public boolean executeNext() {
        synchronized (history) {
            return history.executeNext();
        }
    }

    @Override
    public boolean hasPrevious() {
        synchronized (history) {
            return history.hasPrevious();
        }
    }

    @Override
    public Command getPrevious() {
        synchronized (history) {
            return history.getPrevious();
        }
    }

    @Override
    public boolean rollBackPrevious() {
        synchronized (history) {
            return history.rollBackPrevious();
        }
    }

    @Override
    public boolean moveBefore(Command command) {
        synchronized (history) {
            return history.moveBefore(command);
        }
    }

    @Override
    public boolean moveAfter(Command command) {
        synchronized (history) {
            return history.moveAfter(command);
        }
    }

    @Override
    public void addListener(Listener listener) {
        synchronized (history) {
            history.addListener(listener);
        }
    }

    @Override
    public boolean removeListener(Listener listener) {
        synchronized (history) {
            return history.removeListener(listener);
        }
    }

}
