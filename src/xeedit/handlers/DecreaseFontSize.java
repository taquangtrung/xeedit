package xeedit.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;

import xeedit.util.FontUtil;

public class DecreaseFontSize extends AbstractHandler{
	
	public Object execute(ExecutionEvent arg0) {
		FontUtil.decreaseFont();
		return null;
	}
}
