package hu.webarticum.chm;


/**
 * Interface for memento classes.
 *
 * @param <T> The subject type of this memento
 */

public interface Memento<T> {
    
    /**
     * Applies this memento to the given subject.
     * 
     * @param subject
     */
    public void apply(T subject);
    
}
