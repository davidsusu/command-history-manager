package hu.webarticum.chm;

import java.util.Date;

public interface Command {
	
	public boolean execute();
	
	public boolean rollBack();
	
	public boolean isExecuted();
	
	public Date getTime();
	
}
