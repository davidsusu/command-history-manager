package hu.webarticum.chm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SampleDocument {
	
	private final History history;
	
	private int position = 0;
	
	private LinkedList<Character> characters = new LinkedList<>();
	
	public SampleDocument(History history) {
		this.history = history;
	}
	
	public History getHistory() {
		return history;
	}
	
	public int moveTo(int newPosition) {
		position = Math.min(characters.size(), Math.max(0, newPosition));
		return position;
	}

	public int moveToStart() {
		position = 0;
		return position;
	}
	
	public int moveToEnd() {
		position = characters.size();
		return position;
	}
	
	public boolean removeChar() {
		if (position == 0) {
			return false;
		}
		
		return history.executeAsNext(new RemoveCommand(position));
	}

	public boolean printChar(char character) {
		return history.executeAsNext(new PrintCommand(position, character));
	}
	
	public List<Character> getCharacters() {
		return new ArrayList<Character>(characters);
	}
	
	public int getPosition() {
		return position;
	}
	
	@Override
	public String toString() {
		StringBuilder resultBuilder = new StringBuilder();
		resultBuilder.append((position == 0) ? '|' : ' ');
		int after = 0;
		for (Character character: characters) {
			after++;
			resultBuilder.append(character);
			resultBuilder.append((after == position) ? '|' : ' ');
		}
		return resultBuilder.toString();
	}
	
	private class PrintCommand extends AbstractCommand {

		final int insertPosition;
		
		final Character character;
		
		PrintCommand(int insertPosition, char character) {
			this.insertPosition = insertPosition;
			this.character = character;
		}

		@Override
		protected boolean _execute() {
			characters.add(insertPosition, character);
			position = insertPosition + 1;
			return true;
		}

		@Override
		protected boolean _rollBack() {
			characters.remove(insertPosition);
			position = insertPosition;
			return true;
		}
		
		@Override
		public String toString() {
			return "Command {print '" + character + "' at " + insertPosition + "}";
		}
		
	}

	private class RemoveCommand extends AbstractCommand {
		
		final int removePosition;
		
		Character character = null;
		
		RemoveCommand(int removePosition) {
			this.removePosition = removePosition;
		}

		@Override
		protected boolean _execute() {
			character = characters.remove(removePosition - 1);
			position = removePosition - 1;
			return true;
		}

		@Override
		protected boolean _rollBack() {
			characters.add(removePosition - 1, character);
			position = removePosition;
			return true;
		}
		
		@Override
		public String toString() {
			return "Command {remove '" + character + "' at " + removePosition + "}";
		}
		
	}

}
