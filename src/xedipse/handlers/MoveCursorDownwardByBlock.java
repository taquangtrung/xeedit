package xedipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import xedipse.Xedipse;

public class MoveCursorDownwardByBlock extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		IEditorPart activeEditor = page.getActiveEditor();
		
		if (!(activeEditor instanceof ITextEditor))
		{
			Xedipse.logError("Move cursor: Cannot get caret position");
			return null;
		}
			
	    ITextEditor textEditor = (ITextEditor)activeEditor;
	    IDocument doc = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		Control control = (Control)activeEditor.getAdapter(Control.class);
		
		if (!(control instanceof StyledText)) 
		{
			Xedipse.logError("Move cursor: Cannot get caret position");
			return null;
		}

		final StyledText styledText = (StyledText) control;
		int cursorOffset = styledText.getCaretOffset();

		try {
			int lineNum= doc.getLineOfOffset(cursorOffset);
			int numOfLine = doc.getNumberOfLines();
			
			if (lineNum == (numOfLine - 2)) {
				int lastLineOffset = doc.getLineOffset(lineNum+1);
				styledText.setSelection(lastLineOffset);
				return null;
			}
			
			if (lineNum >= (numOfLine - 1)) {
				styledText.setSelection(doc.getLength());
				return null;
			}
			
			int beginOffset = doc.getLineOffset(lineNum);
			int endOffset = doc.getLineOffset(lineNum+1) - 1;
			String currentLine = doc.get(beginOffset, endOffset - beginOffset + 1);
			boolean isCurrentLineEmpty = currentLine.trim().isEmpty();

			// find next non-empty line which follows an empty line
			lineNum++;
			while (lineNum < numOfLine - 1) {
				beginOffset = doc.getLineOffset(lineNum);
				endOffset = doc.getLineOffset(lineNum+1) - 1;
				String nextLine = doc.get(beginOffset, endOffset - beginOffset + 1);
				if (nextLine.trim().isEmpty()) {
					isCurrentLineEmpty = true;
					lineNum++;
				}
				else if (!isCurrentLineEmpty)
					lineNum++;
				else
					break;
			}
			int newOffset = doc.getLineOffset(lineNum);
			styledText.setSelection(newOffset);
		} catch (BadLocationException e) {
			e.printStackTrace();
			return null;
		}
		
		return null;
	}
	

}
