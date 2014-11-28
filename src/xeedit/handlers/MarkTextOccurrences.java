package xeedit.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import xeedit.Xeedit;

public class MarkTextOccurrences extends AbstractHandler {
	
	private static final String XEEDIT_MARK_OCCURRENCE_ID = "xeedit.marker.occurrence";


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		IEditorPart activeEditor = page.getActiveEditor();
		
		if (!(activeEditor instanceof ITextEditor))
		{
			Xeedit.logError("Mark text occurrence: Cannot get text editor");
			return null;
		}
			
	    ITextEditor textEditor = (ITextEditor)activeEditor;
	    IDocument document = textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		Control control = (Control)activeEditor.getAdapter(Control.class);
		
		if (!(control instanceof StyledText)) 
		{
			Xeedit.logError("Mark text occurrence: cannot get styled text editor");
			return null;
		}

		final StyledText styledText = (StyledText) control;
		IEditorInput input = textEditor.getEditorInput();
		
		try {
			// delete old marker
			IFile ifile = ((FileEditorInput) input).getFile();
			IMarker markes[];
			markes = ifile.findMarkers(XEEDIT_MARK_OCCURRENCE_ID, false, 0);
			for (int i = 0; i < markes.length; i++)
				markes[i].delete();
			
			// find all the occurrences of selected text and mark it
			String text = styledText.getSelectionText();
			if (text.length() > 0) {
				FindReplaceDocumentAdapter docFind = new FindReplaceDocumentAdapter(document);
				IRegion region = docFind.find(0, text, true, true, false, false);
				while (region != null) {
					// mark the found text
					IResource resource = (IResource) input.getAdapter(IResource.class);
					IMarker marker = resource.createMarker(XEEDIT_MARK_OCCURRENCE_ID);
					int startOffset = region.getOffset();
					int endOffset = region.getOffset() + region.getLength();
					marker.setAttribute(IMarker.CHAR_START, startOffset);
					marker.setAttribute(IMarker.CHAR_END, endOffset);
					marker.setAttribute(IMarker.MESSAGE, "");
					region = docFind.find(endOffset + 1, text, true, true, false, false);
				}
			}
		} catch (CoreException | BadLocationException e) {
			e.printStackTrace();
		}


		return null;
	}

}
