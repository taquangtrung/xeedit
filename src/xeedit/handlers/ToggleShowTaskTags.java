package xeedit.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

import xeedit.Xeedit;

public class ToggleShowTaskTags extends AbstractHandler {


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
		IEditorInput input = textEditor.getEditorInput();

		try {
			// get all current task tags marker
			IFile ifile = ((FileEditorInput) input).getFile();
			IMarker markes[];
			markes = ifile.findMarkers(IMarker.TASK, false, 0);

			// If task tags is enable or there are some task tags, then remove them 
			if (Xeedit.showTaskTags || (markes.length > 0)) {
				// unset show task tags flag
				Xeedit.showTaskTags = false;
				
				// remove all task tags
				for (int i = 0; i < markes.length; i++)
					markes[i].delete();
			}
			// Otherwise, show all task tags in current file
			else {
				// set show task tags flag
				Xeedit.showTaskTags = true;
				
				// remove all old task tags
				for (int i = 0; i < markes.length; i++)
					markes[i].delete();
	
				// find all the occurrences of XXX and mark it
				findAndMarkTaskTags("XXX", IMarker.PRIORITY_LOW, textEditor);
	
				// find all the occurrences of TODO and mark it
				findAndMarkTaskTags("TODO", IMarker.PRIORITY_NORMAL, textEditor);
	
				// find all the occurrences of FIXME and mark it
				findAndMarkTaskTags("FIXME", IMarker.PRIORITY_HIGH, textEditor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}
	
	public void findAndMarkTaskTags(String taskTag, int priority, ITextEditor editor) {
		IDocument document = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		IEditorInput input = editor.getEditorInput();

		FindReplaceDocumentAdapter docFind = new FindReplaceDocumentAdapter(document);
		
		try {
			IRegion tagRegion = docFind.find(0, taskTag, true, true, true, false);
			while (tagRegion != null) {
				IResource resource = (IResource) input.getAdapter(IResource.class);
				IMarker marker = resource.createMarker(IMarker.TASK);
				int startOffset = tagRegion.getOffset();
				int endOffset = tagRegion.getOffset() + tagRegion.getLength();
				int lineNum = document.getLineOfOffset(startOffset);
				int endLineOffset = document.getLineOffset(lineNum) + document.getLineLength(lineNum);
				// description of marker is extracted from this tag to the end of the line containing it
				String taskDescription = document.get(startOffset, endLineOffset - startOffset - 1);
				marker.setAttribute(IMarker.CHAR_START, startOffset);
				marker.setAttribute(IMarker.CHAR_END, endOffset);
				marker.setAttribute(IMarker.MESSAGE, taskDescription);
				marker.setAttribute(IMarker.PRIORITY, priority);
				marker.setAttribute(IMarker.LINE_NUMBER, lineNum);
				tagRegion = docFind.find(endOffset + 1, taskTag, true, true, true, false);
			}
		} catch (Exception e) {
			return;
		}
	}

}
