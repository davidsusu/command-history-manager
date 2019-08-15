package hu.webarticum.chm;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * Generic command with automatic memento handling.
 *
 * @param <T> The subject type of the memento
 */

public class MementoCommand<T> extends AbstractCommand {

    private final Object title;

    private final T subject;
    
    private Supplier<Memento<T>> mementoSupplier;
    
    private Runnable commandAction;
    
    private Memento<T> oldMemento = null;
    
    private Memento<T> newMemento = null;
    

    /**
     * Creates a new MementoCommand with consuming callbacks.
     * 
     * @param title The title provider
     * @param subject The subject of the created mementos
     * @param mementoExtractor Function that extracts memento from the given subject
     * @param commandAction Consumer that executes the command on the given subject
     */
    public MementoCommand(
        Object title,
        final T subject,
        final Function<T, Memento<T>> mementoExtractor,
        final Consumer<T> commandAction
    ) {
        this(title, subject, new Supplier<Memento<T>>() {

            @Override
            public Memento<T> get() {
                return mementoExtractor.apply(subject);
            }
            
        }, new Runnable() {
            
            @Override
            public void run() {
                commandAction.accept(subject);
            }
            
        });
    }

    /**
     * Creates a new MementoCommand with non-consuming callbacks.
     * 
     * @param title The title provider
     * @param subject The subject of the created mementos
     * @param mementoSupplier Supplier that gets the current memento
     * @param commandAction Runnable that executes the command
     */
    public MementoCommand(
        Object title,
        T subject,
        Supplier<Memento<T>> mementoSupplier,
        final Runnable commandAction
    ) {
        this.title = title;
        this.subject = subject;
        this.mementoSupplier = mementoSupplier;
        this.commandAction = commandAction;
    }


    @Override
    protected boolean _execute() {
        if (oldMemento == null) {
            oldMemento = mementoSupplier.get();
            commandAction.run();
            newMemento = mementoSupplier.get();

            mementoSupplier = null;
            commandAction = null;
        } else {
            newMemento.apply(subject);
        }
        return true;
    }

    @Override
    protected boolean _rollBack() {
        oldMemento.apply(subject);
        return true;
    }
    
    @Override
    public String toString() {
        return title.toString();
    }

}
