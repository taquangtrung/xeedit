package xeedit.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import xeedit.Xeedit;
import xeedit.util.SourceUtil;

public class MoveCursorDownwardByIndent extends AbstractHandler {
	
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
		styledText.addCaretListener(new CaretListener() {
			@Override
			public void caretMoved(CaretEvent event) {
				styledText.redraw();
				styledText.update();
				styledText.removeCaretListener(this);
			}
		});
		moveCursor(styledText);
		
		return null;
	}
	
	private void moveCursor(StyledText styledText) {
		try {
			int cursorOffset = styledText.getCaretOffset();
			String content = styledText.getText();
			IDocument doc = new Document(content);

			int lineNum= doc.getLineOfOffset(cursorOffset);
			int numOfLine = doc.getNumberOfLines();
			int docLen = doc.getLength();
			
			if (lineNum == (numOfLine - 2)) {
				int lastLineOffset = doc.getLineOffset(lineNum+1);
				styledText.setSelection(lastLineOffset);
				return;
			}
			
			if (lineNum >= (numOfLine - 1)) {
				styledText.setSelection(doc.getLength());
				return;
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
				while (newOffset < docLen) {
					Character ch = doc.getChar(newOffset);
					if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r')
						break;
					else
						newOffset++;
				}
				if (newOffset >= docLen)
					newOffset = beginOffset;
				styledText.setSelection(newOffset);
				return;
			}

			// find next line which has different identation
			int tabSize = styledText.getTabs();
			int currentIndent = SourceUtil.indentationOfLine(currentLine,tabSize);
			lineNum++;
			while (lineNum < numOfLine - 1) {
				beginOffset = doc.getLineOffset(lineNum);
				endOffset = (lineNum < numOfLine - 1) ? doc.getLineOffset(lineNum+1) - 1 : docLen - 1;
				String nextLine = doc.get(beginOffset, endOffset - beginOffset + 1);
				int nextIndent = SourceUtil.indentationOfLine(nextLine, tabSize);
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
			int newLineOffset = doc.getLineOffset(lineNum);
			int newOffset = newLineOffset;
			while (newOffset < docLen) {
				Character ch = doc.getChar(newOffset);
				if (ch != ' ' && ch != '\t' && ch != '\n' && ch != '\r')
					break;
				else
					newOffset++;
			}
			if (newOffset >= docLen)
				newOffset = newLineOffset;
			
			styledText.setSelection(newOffset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}
	

}
