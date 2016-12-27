package hu.webarticum.chm;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class CommandQueueTest {

	@Test
	public void testStatusStackOfASet() {
		final Set<String> words = new TreeSet<String>();
		
		CommandQueue commandQueue = new CommandQueue();
		
		class AddToWordsCommand extends AbstractCommand {
			
			private boolean had = false;
			
			private final String word;
			
			public AddToWordsCommand(String word) {
				this.word = word;
			}
			
			@Override
			protected boolean _rollBack() {
				if (!had) {
					words.remove(word);
				}
				return true;
			}
			
			@Override
			protected boolean _execute() {
				had = words.contains(word);
				if (!had) {
					words.add(word);
				}
				return true;
			}
			
			@Override
			public String toString() {
				return "AddToWordsCommand(" + word + ")";
			}
			
		};
		
		commandQueue.addAndExecute(new AddToWordsCommand("foo"));

		assertTrue(words.contains("foo"));
		assertFalse(words.contains("bar"));
		
		commandQueue.addAndExecute(new AddToWordsCommand("foo"));

		assertTrue(words.contains("foo"));
		assertFalse(words.contains("bar"));
		
		commandQueue.addAndExecute(new AddToWordsCommand("bar"));

		assertTrue(words.contains("foo"));
		assertTrue(words.contains("bar"));
		
		commandQueue.rollBackPrevious();

		assertTrue(words.contains("foo"));
		assertFalse(words.contains("bar"));

		commandQueue.executeNext();

		assertTrue(words.contains("foo"));
		assertTrue(words.contains("bar"));
		
		commandQueue.rollBackPrevious();

		assertTrue(words.contains("foo"));
		assertFalse(words.contains("bar"));

		commandQueue.rollBackPrevious();

		assertTrue(words.contains("foo"));
		assertFalse(words.contains("bar"));

		commandQueue.rollBackPrevious();

		assertFalse(words.contains("foo"));
		assertFalse(words.contains("bar"));

	}

}
