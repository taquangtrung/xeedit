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

public class SelectTextUpwardByBlock extends AbstractHandler {
	
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
			
			if (lineNum <= 0) {
				styledText.setSelection(startOffset, 0);
				return null;
			}
			
			int currentLineOffset = doc.getLineOffset(lineNum);
			
			// if previous line is empty and cursor is not in beginning of
			// current block, then go to the beginning.
			int beginOffset = doc.getLineOffset(lineNum-1);
			int endOffset = doc.getLineOffset(lineNum) - 1;
			String prevLine= doc.get(beginOffset, endOffset - beginOffset + 1);
			boolean isPrevLineEmpty = prevLine.trim().isEmpty();
			if (isPrevLineEmpty && (cursorOffset != currentLineOffset)) {
				styledText.setSelection(startOffset, currentLineOffset);
				return null;
			}

			// find previous non-empty line which follows an empty line
			lineNum--;
			while (lineNum > 0) {
				beginOffset = doc.getLineOffset(lineNum-1);
				endOffset = doc.getLineOffset(lineNum) - 1;
				prevLine = doc.get(beginOffset, endOffset - beginOffset + 1);
				if (!prevLine.trim().isEmpty()) {
					isPrevLineEmpty = false;
					lineNum--;
				}
				else if (isPrevLineEmpty)
					lineNum--;
				else {
					break;		// stop at this non-empty line
				}
			}
			int newOffset = doc.getLineOffset(lineNum);
			styledText.setSelection(startOffset, newOffset);
		} catch (BadLocationException e) {
			return null;
		}
		
		return null;
	}
	

}
