package hu.webarticum.chm;

public class PathEntry {
    
    private final Command command;
    
    private boolean toRollBack;
    
    public PathEntry(Command command, boolean toRollBack) {
        this.command = command;
        this.toRollBack = toRollBack;
    }
    
    public Command getCommand() {
        return command;
    }
    
    public boolean isToRollBack() {
        return toRollBack;
    }
    
}
