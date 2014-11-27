package xedipse.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.texteditor.ITextEditor;

import xedipse.Xedipse;

public class RemoveTrailingWhiteSpace extends AbstractHandler {

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
		String selectedText = styledText.getSelectionText();
		int startSelectedOffset = styledText.getSelection().x;
		int endSelectedOffset =  styledText.getSelection().y;
		
		try {
			int startLine = 0;
			int endLine = 0;
			if (selectedText.length() > 0) {
				startLine = doc.getLineOfOffset(startSelectedOffset);
				endLine = doc.getLineOfOffset(endSelectedOffset);
			}
			else {
				startLine = 0;
				endLine = doc.getNumberOfLines() - 1; 
			}
			String newContent = removeTrailingWhitespaces(doc, startLine, endLine);
			int startOffset = doc.getLineOffset(startLine);
			int length = 0;
			if (endLine < doc.getNumberOfLines() - 1)
				length = doc.getLineOffset(endLine + 1) - doc.getLineDelimiter(endLine).length() - startOffset + 1;
			else
				length = doc.getLength() - startOffset;
			doc.replace(startOffset, length, newContent);
			styledText.setSelectionRange(startOffset, newContent.length() - 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private String removeTrailingWhitespaces(IDocument doc, int startLine, int endLine) throws Exception {
		int line = startLine;
		String newContent = "";
		while (line <= endLine) {

			int offset1 = doc.getLineOffset(line);

			if (line < doc.getNumberOfLines() - 1) {
				String lineDelimiter = doc.getLineDelimiter(line);
				int offset2 = doc.getLineOffset(line+1) - lineDelimiter.length();
				
				String lineContent = doc.get(offset1, offset2 - offset1 + 1);
				lineContent = "L" + lineContent;
				lineContent = lineContent.trim();
				lineContent = lineContent.substring(1);
				newContent = newContent + lineContent + lineDelimiter;
			}
			else {
				String lineContent = doc.get(offset1, doc.getLength() - offset1);
				lineContent = "L" + lineContent;
				lineContent = lineContent.trim();
				lineContent = lineContent.substring(1);
				newContent = newContent + lineContent;
			}
			
			line++;
		}
		
		return newContent;
	}

}
