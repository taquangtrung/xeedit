package xeedit.util;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

public class FontUtil {
	public static void increaseFont() {
		changeFont(1);
	}

	public static void decreaseFont() {
		changeFont(-1);
	}

	private static void changeFont(int changeBy) {
		changeFont("org.eclipse.ui.workbench", "org.eclipse.jdt.ui.editors.textfont", changeBy);
		changeFont("org.eclipse.ui.workbench", "org.eclipse.jface.textfont", changeBy);
	}

	private static void changeFont(String qualifier, String key, int changeBy) {
		final Font font = JFaceResources.getFontRegistry().get(key);
		
		final FontData[] newFontData = font.getFontData();
		newFontData[0].setHeight(newFontData[0].getHeight() + changeBy);
	
		JFaceResources.getFontRegistry().put(key, newFontData);
	}

}
