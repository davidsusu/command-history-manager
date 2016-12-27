package hu.webarticum.chm;

import static org.junit.Assert.*;

import org.junit.Test;

public class SingleCommandHistoryTest {

	@Test
	public void test() {
		SingleCommandHistory history = new SingleCommandHistory();
		
		SampleDocument document = new SampleDocument(history);
		
		assertTrue(history.isEmpty());
		
		document.printChar('A');
		
		Command firstCommand = history.getPrevious();

		assertFalse(history.isEmpty());
		
		assertTrue(history.contains(firstCommand));
		assertTrue(history.hasPrevious());
		assertEquals(history.getPrevious(), firstCommand);
		assertFalse(history.hasNext());
		assertNull(history.getNext());
		
		assertTrue(history.rollBackPrevious());

		assertTrue(history.contains(firstCommand));
		assertFalse(history.hasPrevious());
		assertNull(history.getPrevious());
		assertTrue(history.hasNext());
		assertEquals(history.getNext(), firstCommand);

		assertTrue(history.executeNext());

		assertEquals(history.getPrevious(), firstCommand);
		
		document.printChar('B');

		Command secondCommand = history.getPrevious();

		assertFalse(history.contains(firstCommand));
		assertTrue(history.contains(secondCommand));
		assertTrue(history.hasPrevious());
		assertEquals(history.getPrevious(), secondCommand);
		assertFalse(history.hasNext());
		assertNull(history.getNext());

		assertTrue(history.rollBackPrevious());

		assertFalse(history.hasPrevious());
		assertTrue(history.hasNext());

		assertFalse(history.rollBackPrevious());

		assertFalse(history.hasPrevious());
		assertTrue(history.hasNext());

	}

}
