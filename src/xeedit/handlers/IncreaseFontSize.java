package xeedit.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;

import xeedit.util.FontUtil;

public class IncreaseFontSize extends AbstractHandler{
	
	public Object execute(ExecutionEvent arg0) {
		FontUtil.increaseFont();
		return null;
	}
	
}
