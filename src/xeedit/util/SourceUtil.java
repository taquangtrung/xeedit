package xeedit.util;

public class SourceUtil {
	
	public static int indentationOfLine(String line, int tabSize) {
		if (line.trim().isEmpty())
			return 0;

		if (line.charAt(0) == ' ')
			return 1 + indentationOfLine(line.substring(1), tabSize);
		
		if (line.charAt(0) == '\t') {
			return tabSize + indentationOfLine(line.substring(1), tabSize);
		}
		
		return 0;
	}
}
