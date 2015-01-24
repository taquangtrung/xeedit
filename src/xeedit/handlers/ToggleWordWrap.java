package xeedit.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import xeedit.Xeedit;

public class ToggleWordWrap extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		IEditorPart activeEditor = page.getActiveEditor();

		Control control = (Control)activeEditor.getAdapter(Control.class);
		if (!(control instanceof StyledText))
		{
			Xeedit.logError("Move cursor: cannot get styled text editor");
			return null;
		}

		final StyledText styledText = (StyledText) control;
		styledText.setWordWrap(!styledText.getWordWrap());

		return null;
	}

}
