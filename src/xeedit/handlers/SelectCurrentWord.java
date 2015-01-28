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

public class SelectCurrentWord extends AbstractHandler {

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

		selectCurrentWord(styledText);

		return null;
	}

	// a word contains of letter, digit characters and under_score "_"
	private void selectCurrentWord(StyledText styledText) {
		String content = styledText.getText();
		IDocument doc = new Document(content);

		int carretOffset = styledText.getCaretOffset();
		
		try {

			int beginOffset = carretOffset-1;
			while (beginOffset >= 0 ) {
				char ch = doc.getChar(beginOffset);
				if (Character.isLetterOrDigit(ch) || (ch == '_'))
					beginOffset--;
				else
					break;
			}
			beginOffset++;
			
			int endOffset = carretOffset;
			while (endOffset < doc.getLength()) {
				char ch;
				ch = doc.getChar(endOffset);
				if (Character.isLetterOrDigit(ch) || (ch == '_'))
					endOffset++;
				else
					break;
			}
			
			// if nothing is choosen, just select a char at current cursor
			if ((beginOffset == endOffset) && (beginOffset > 0))
				 beginOffset--;
				
			styledText.setSelection(beginOffset, endOffset);
		} catch (BadLocationException e) {
			// e.printStackTrace();
		}
	}


}
