package hu.webarticum.chm;

import static org.junit.Assert.*;

import org.junit.Test;

public class AggregationTest {

	@Test
	public void test() {
		History history = new CommandQueue();
		
		SampleDocument document = new SampleDocument(history);
		
		document.printChar('A');
		
		assertEquals(" A|", document.toString());
		
		document.printChars('B', 'C', 'D');

		assertEquals(" A B C D|", document.toString());
		
		history.rollBackPrevious();

		assertEquals(" A|", document.toString());

		history.executeNext();

		assertEquals(" A B C D|", document.toString());
		
	}

}
