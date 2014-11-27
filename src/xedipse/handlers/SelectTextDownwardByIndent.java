package xedipse.handlers;

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

import xedipse.Xedipse;
import xedipse.util.SouceUtil;

public class SelectTextDownwardByIndent extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		IEditorPart activeEditor = page.getActiveEditor();
		
		if (!(activeEditor instanceof ITextEditor))
		{
			Xedipse.logError("Select text: Cannot get text editor");
			return null;
		}
			
	    ITextEditor textEditor = (ITextEditor)activeEditor;
	    IDocument doc = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		Control control = (Control)activeEditor.getAdapter(Control.class);
		
		if (!(control instanceof StyledText)) 
		{
			Xedipse.logError("Select text: Cannot get styled text editor");
			return null;
		}

		final StyledText styledText = (StyledText) control;
		int cursorOffset = styledText.getCaretOffset();

		Point selection = styledText.getSelection();
		int startOffset = (selection.x < cursorOffset) ? selection.x : selection.y;
		
		try {
			int lineNum= doc.getLineOfOffset(cursorOffset);
			int numOfLine = doc.getNumberOfLines();
			int docLen = doc.getLength();
			
			if (lineNum == (numOfLine - 2)) {
				int lastLineOffset = doc.getLineOffset(lineNum+1);
				styledText.setSelection(startOffset, lastLineOffset);
				return null;
			}
			
			if (lineNum >= (numOfLine - 1)) {
				styledText.setSelection(startOffset, doc.getLength());
				return null;
			}
			
			// ignore empty lines while going down, go to the first non empty line
			int beginOffset = doc.getLineOffset(lineNum);
			int endOffset = doc.getLineOffset(lineNum+1) - 1;
			String currentLine = doc.get(beginOffset, endOffset - beginOffset + 1);
			
			if (currentLine.trim().isEmpty()) {
				lineNum++;
				while (lineNum < numOfLine) {
					beginOffset = doc.getLineOffset(lineNum);
					endOffset = (lineNum < numOfLine - 1) ? doc.getLineOffset(lineNum+1) - 1 : docLen - 1;
					currentLine = doc.get(beginOffset, endOffset - beginOffset + 1);
					if (currentLine.trim().isEmpty())
						lineNum++;
					else 
						break;
				}
				if (lineNum >= numOfLine)
					lineNum = numOfLine - 1;
				int newOffset = doc.getLineOffset(lineNum);
				int k = newOffset;
				while (k < docLen) {
					Character ch = doc.getChar(k);
					if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r')
						break;
					else {
						if (ch == '\n' || ch == '\r')
							newOffset = k + 1;
						k++;
					}
				}
				
				styledText.setSelection(startOffset, newOffset);
				return null;
			}

			// find next line which has different identation
			int currentIndent = SouceUtil.indentationOfLine(currentLine);
			lineNum++;
			while (lineNum < numOfLine - 1) {
				beginOffset = doc.getLineOffset(lineNum);
				endOffset = (lineNum < numOfLine - 1) ? doc.getLineOffset(lineNum+1) - 1 : docLen - 1;
				String nextLine = doc.get(beginOffset, endOffset - beginOffset + 1);
				int nextIndent = SouceUtil.indentationOfLine(nextLine);
				if (currentIndent != nextIndent) {
					break;
				}
				else if (nextLine.trim().isEmpty()) {
					currentIndent = -1;
					lineNum++;
				}
				else {
					lineNum++;
					currentLine = nextLine;
					currentIndent = nextIndent;
				}
			}
			
			// find location to jump to
			int newOffset = doc.getLineOffset(lineNum);
			int k = newOffset;
			while (k < docLen) {
				Character ch = doc.getChar(k);
				if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r')
					break;
				else {
					if (ch == '\n' || ch == '\r')
						newOffset = k + 1;
					k++;
				}
			}
			
			styledText.setSelection(startOffset, newOffset);
		} catch (BadLocationException e) {
			return null;
		}
		
		return null;
	}
	

}
