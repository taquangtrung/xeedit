package xeedit.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import xeedit.Xeedit;

public class SelectTextDownwardByBlock extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		IEditorPart activeEditor = page.getActiveEditor();
		
		if (!(activeEditor instanceof ITextEditor))
		{
			Xeedit.logError("Select text: Cannot get text editor");
			return null;
		}
			
	    ITextEditor textEditor = (ITextEditor)activeEditor;
	    IDocument doc = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		Control control = (Control)activeEditor.getAdapter(Control.class);
		
		if (!(control instanceof StyledText)) 
		{
			Xeedit.logError("Select text: Cannot get styled text editor");
			return null;
		}

		final StyledText styledText = (StyledText) control;
		int cursorOffset = styledText.getCaretOffset();

		Point selection = styledText.getSelection();
		int startOffset = (selection.x < cursorOffset) ? selection.x : selection.y;
		
		try {
			int lineNum= doc.getLineOfOffset(cursorOffset);
			int numOfLine = doc.getNumberOfLines();
			
			if (lineNum == (numOfLine - 2)) {
				int lastLineOffset = doc.getLineOffset(lineNum+1);
				styledText.setSelection(startOffset, lastLineOffset);
				return null;
			}
			
			if (lineNum >= (numOfLine - 1)) {
				styledText.setSelection(startOffset, doc.getLength());
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
			styledText.setSelection(startOffset, newOffset);
		} catch (BadLocationException e) {
			return null;
		}
		
		return null;
	}
	

}
