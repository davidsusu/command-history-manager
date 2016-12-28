package hu.webarticum.chm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Demo document for demonstrating history management.
 * 
 * Represents a list of characters.
 * New characters can be inserted, and existing ones can be removed.
 */

public class SampleDocument {
	
	private final History history;
	
	private int position = 0;
	
	private LinkedList<Character> characters = new LinkedList<Character>();
	
	/**
	 * @param history the history manager for handling commands
	 */
	public SampleDocument(History history) {
		this.history = history;
	}
	
	/**
	 * Gets the history manager of this document.
	 * 
	 * @return the history manager
	 */
	public History getHistory() {
		return history;
	}
	
	/**
	 * Moves internal pointer to the given position.
	 * 
	 * If less than zero, then will be corrected to zero.
	 * If greater then length of document, will be corrected to the length
	 * 
	 * @param newPosition the new position of the internal pointer
	 */
	public int moveTo(int newPosition) {
		position = Math.min(characters.size(), Math.max(0, newPosition));
		return position;
	}

	/**
	 * Moves internal pointer to begin (zero).
	 */
	public int moveToStart() {
		position = 0;
		return position;
	}

	/**
	 * Moves internal pointer to end (length of document).
	 */
	public int moveToEnd() {
		position = characters.size();
		return position;
	}
	
	/**
	 * Removes character before the current position of internal pointer.
	 * 
	 * Operation will fail if current position is zero.
	 * 
	 * @return {@code true} on success
	 */
	public boolean removeChar() {
		if (position == 0) {
			return false;
		}
		
		return history.addAndExecute(new RemoveCommand(position));
	}

	/**
	 * Inserts a character at position of internal pointer.
	 * 
	 * @return {@code true} on success
	 */
	public boolean printChar(char character) {
		return history.addAndExecute(new PrintCommand(position, character));
	}

	/**
	 * Inserts multiple characters at position of internal pointer.
	 * 
	 * @return {@code true} on success
	 */
	public boolean printChars(char... characters) {
		CommandAggregation aggregation = new CommandAggregation();
		for (int i = 0; i < characters.length; i++) {
			char character = characters[i];
			if (!aggregation.add(new PrintCommand(position + i, character))) {
				return false;
			}
		}
		return history.addAndExecute(aggregation);
	}
	
	/**
	 * Gets the characters of this document (the content).
	 * 
	 * @return characters of this document
	 */
	public List<Character> getCharacters() {
		return new ArrayList<Character>(characters);
	}

	/**
	 * Gets current position of internal pointer.
	 * 
	 * @return position of internal pointer
	 */
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
