package hu.webarticum.chm;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class SampleDocumentTest {
	
	private History history;
	
	public SampleDocumentTest(History history) {
		this.history = history;
	}
	
	@Test
	public void test() {
		SampleDocument document = new SampleDocument(history);
		assertEquals("|", document.toString());
		document.printChar('A');
		assertEquals(" A|", document.toString());
		document.printChar('B');
		assertEquals(" A B|", document.toString());
		document.moveTo(1);
		assertEquals(" A|B ", document.toString());
		document.printChar('W');
		assertEquals(" A W|B ", document.toString());
		
		Command deadCommand = history.getPreviousCommand();
		
		history.rollBackPrevious();
		assertEquals(" A|B ", document.toString());
		document.moveToEnd();
		assertEquals(" A B|", document.toString());
		document.printChar('C');
		assertEquals(" A B C|", document.toString());
		document.moveTo(1);
		assertEquals(" A|B C ", document.toString());
		document.printChar('X');
		assertEquals(" A X|B C ", document.toString());
		document.printChar('Y');
		assertEquals(" A X Y|B C ", document.toString());
		document.printChar('Z');
		assertEquals(" A X Y Z|B C ", document.toString());
		document.moveTo(3);
		assertEquals(" A X Y|Z B C ", document.toString());
		document.removeChar();
		assertEquals(" A X|Z B C ", document.toString());
		
		Command liveCommand = history.getPreviousCommand();

		history.rollBackPrevious();
		assertEquals(" A X Y|Z B C ", document.toString());
		history.rollBackPrevious();
		assertEquals(" A X Y|B C ", document.toString());
		history.rollBackPrevious();
		assertEquals(" A X|B C ", document.toString());
		history.rollBackPrevious();
		assertEquals(" A|B C ", document.toString());
		history.rollBackPrevious();
		assertEquals(" A B|", document.toString());
		history.executeNext();
		assertEquals(" A B C|", document.toString());
		history.executeNext();
		assertEquals(" A X|B C ", document.toString());

		assertEquals(true, history.moveTo(liveCommand));
		assertEquals(" A X|Z B C ", document.toString());
		
		if (history instanceof CommandQueue) {
			assertEquals(false, history.moveTo(deadCommand));
			assertEquals(" A X|Z B C ", document.toString());
		} else if (history instanceof ComplexHistory) {
			assertEquals(true, history.moveTo(deadCommand));
			assertEquals(" A W|B ", document.toString());
			history.rollBackPrevious();
			assertEquals(" A|B ", document.toString());
			history.rollBackPrevious();
			assertEquals(" A|", document.toString());
			history.executeNext();
			assertEquals(" A B|", document.toString());
			history.executeNext();
			assertEquals(" A W|B ", document.toString());
		}
	}
	
	@Parameters
	public static Collection<History> data() {
		List<History> data = new ArrayList<>();
		data.add(new CommandQueue());
		data.add(new ComplexHistory());
		return data;
	}
	
}
