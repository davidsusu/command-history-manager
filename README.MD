# Command history manager


## Version

Beta. Not versioned yet.


## Description

This library provides advanced history management functionality for Java.

This package focuses strictly on history handling and includes no supplementary tools.


## Usage

Here is an example with the built-in SampleDocument:

```java
History history = new LinearHistory();

SampleDocument document = new SampleDocument(history);

document.printChars('A', 'B');

System.out.println(document); // prints " A B|"
Command abCommand = history.getPrevious();

document.printChar('C');
document.moveTo(1);
document.printChar('X');

System.out.println(document); // prints " A X|B C"

history.rollBackPrevious();

System.out.println(document); // prints " A|B C"

document.moveToEnd();
document.printChar('D');

System.out.println(document); // prints " A B C D|"

history.moveAfter(abCommand);

System.out.println(document); // prints " A B|"

history.executeNext();

System.out.println(document); // prints " A B C|"

history.executeNext();

System.out.println(document); // prints " A B C D|"
```

Output:

```
 A B|
 A X|B C 
 A|B C 
 A B C D|
 A B|
 A B C|
 A B C D|
```

When you develop your custom history functionality you need two general things:
a history manager and a set of possible commands. This library provides
three types of history managers:

- `SingleHistory`: minimal implementation, it can store only one command
- `LinearHistory`: this is the traditional implementation, stores a single timeline of commands
- `ComplexHistory`: an advanced history manager, can store a tree of commands

`LinearHistory` and `ComplexHistory` support maximum capacity to avoid excessive memory allocation.

Of course you can build a custom history manager too, in this case you must implement the `History` interface.

Commands must implement the `Command` interface, and it is recommended to extend `AbstractCommand`. For example:

```java
class AppendToStringListCommand extends AbstractCommand {
    
    private final List<String> list;
    
    private final String item;
    
    public AppendToStringListCommand(List<String> list, String item) {
        this.list = list;
        this.item = item;
    }
    
    @Override
    protected boolean _execute() {
        list.add(item);
        return true;
    }
    
    @Override
    protected boolean _rollBack() {
        list.remove(list.size() - 1);
        return true;
    }
    
}

History history = new ComplexHistory();

List<String> list = new LinkedList<String>();

history.addAndExecute(new AppendToStringListCommand(list, "first"));

System.out.println(list); // [first]

Command deadSecondCommand = new AppendToStringListCommand(list, "second");
history.addAndExecute(deadSecondCommand);

System.out.println(list); // [first, second]

history.rollBackPrevious();

System.out.println(list); // [first]

history.addAndExecute(new AppendToStringListCommand(list, "third"));

System.out.println(list); // [first, third]

CommandAggregation fourthFifthAndSixth = new CommandAggregation();
fourthFifthAndSixth.add(new AppendToStringListCommand(list, "fourth"));
fourthFifthAndSixth.add(new AppendToStringListCommand(list, "fifth"));
fourthFifthAndSixth.add(new AppendToStringListCommand(list, "sixth"));
fourthFifthAndSixth.close();
history.addAndExecute(fourthFifthAndSixth);

System.out.println(list); // [first, third, fourth, fifth, sixth]

history.rollBackPrevious();

System.out.println(list); // [first, third]

history.moveAfter(deadSecondCommand);

System.out.println(list); // [first, second]
```

Output:

```
[first]
[first, second]
[first]
[first, third]
[first, third, fourth, fifth, sixth]
[first, third]
[first, second]
```

The last `history.moveAfter(deadSecondCommand);` moves pointer to a dead command
(not on the current timeline). This whould not have worked with `LinearHistory`.
