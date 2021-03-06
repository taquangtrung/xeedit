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
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import xeedit.Xeedit;
import xeedit.util.TextUtil;

public class SelectTextUpwardByIndent extends AbstractHandler {

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

		selectText(styledText);

		return null;
	}

	private void selectText(StyledText styledText) {
		int cursorOffset = styledText.getCaretOffset();
		String content = styledText.getText();
		IDocument doc = new Document(content);
		int tabSize = styledText.getTabs();

		Point selection = styledText.getSelection();
		int startOffset = (selection.x < cursorOffset) ? selection.x : selection.y;

		try {
			int lineNum = doc.getLineOfOffset(cursorOffset);
			int numOfLine = doc.getNumberOfLines();
			int docLen = doc.getLength();

			if (lineNum <= 0) {
				styledText.setSelection(startOffset, 0);
				return;
			}

			// ignore empty lines when going back;
			int beginOffsetCurrentLine = 0;
			int endOffsetCurrentLine = 0;
			String currentLine = "";
			while (lineNum > 0) {
				beginOffsetCurrentLine = doc.getLineOffset(lineNum);
				endOffsetCurrentLine = (lineNum < numOfLine-1) ? doc.getLineOffset(lineNum+1) - 1 : docLen - 1;
				currentLine = doc.get(beginOffsetCurrentLine, endOffsetCurrentLine - beginOffsetCurrentLine + 1);
				if (currentLine.trim().isEmpty()) {
					lineNum--;
				}
				else
					break;
			}
			if (lineNum == 0) {
				styledText.setSelection(startOffset, 0);
				return;
			}


			// if previous line has different indentation and cursor is not
			// in the beginning of current block, then go to the beginning.
			beginOffsetCurrentLine = doc.getLineOffset(lineNum);
			int cursorColumn = cursorOffset - beginOffsetCurrentLine;
			int newOffset = beginOffsetCurrentLine;
			while (newOffset < docLen) {
				Character ch = doc.getChar(newOffset);
				if (ch != ' ' && ch != '\t' && ch != '\r' && ch != '\n')
					break;
				else
					newOffset++;
			}
			int currentIndent = newOffset - beginOffsetCurrentLine;


			int beginOffsetPrevLine = doc.getLineOffset(lineNum-1);
			int endOffsetPrevLine = doc.getLineOffset(lineNum) - 1;
			String prevLine = doc.get(beginOffsetPrevLine, endOffsetPrevLine - beginOffsetPrevLine + 1);
			int prevIndent = TextUtil.indentationOfLine(prevLine, tabSize);
			if (prevLine.trim().isEmpty() && cursorColumn > currentIndent) {
				newOffset = beginOffsetCurrentLine;// + currentIndent;
				styledText.setSelection(startOffset, newOffset);
				return;
			}
			else if ((prevIndent != currentIndent) && (cursorColumn > currentIndent)) {
				newOffset = beginOffsetCurrentLine;// + currentIndent;
				styledText.setSelection(startOffset, newOffset);
				return;
			}

			// ignore empty lines when going back;
			lineNum--;
			endOffsetCurrentLine = 0;
			currentLine = "";
			while (lineNum > 0) {
				beginOffsetCurrentLine = doc.getLineOffset(lineNum);
				endOffsetCurrentLine = (lineNum < numOfLine-1) ? doc.getLineOffset(lineNum+1) - 1 : docLen;
				currentLine = doc.get(beginOffsetCurrentLine, endOffsetCurrentLine - beginOffsetCurrentLine + 1);
				if (currentLine.trim().isEmpty()) {
					lineNum--;
				}
				else
					break;
			}
			if (lineNum == 0) {
				styledText.setSelection(startOffset, 0);
				return;
			}


			// search back to find the last line has different indentation
			currentIndent = TextUtil.indentationOfLine(currentLine, tabSize);
			while (lineNum > 0) {
				beginOffsetPrevLine = doc.getLineOffset(lineNum-1);
				endOffsetPrevLine = doc.getLineOffset(lineNum) - 1;
				prevLine = doc.get(beginOffsetPrevLine, endOffsetPrevLine - beginOffsetPrevLine + 1);
				prevIndent = TextUtil.indentationOfLine(prevLine, tabSize);

				if (prevLine.trim().isEmpty()) {
					break;
				}
				else if (prevIndent == currentIndent) {
					lineNum--;
					currentLine = prevLine;
				}
				else
					break;
			}
			newOffset = doc.getLineOffset(lineNum);
			styledText.setSelection(startOffset, newOffset);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}


}
