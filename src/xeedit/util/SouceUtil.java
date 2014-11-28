package xeedit.util;

public class SouceUtil {
	
	public static int indentationOfLine(String line) {
		if (line.trim().isEmpty())
			return 0;

		if (line.charAt(0) == ' ')
			return 1 + indentationOfLine(line.substring(1));
		
		if (line.charAt(0) == '\t') {
			// FIXME: need an exact value of tabsize
			int tabSize = 1;
			return tabSize + indentationOfLine(line.substring(1));
		}
		
		return 0;
	}
}
