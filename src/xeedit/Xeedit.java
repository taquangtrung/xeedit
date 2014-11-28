package xeedit;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Xeedit extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "EditorPP"; //$NON-NLS-1$

	// The shared instance
	private static Xeedit plugin;
	
	/**
	 * The constructor
	 */
	public Xeedit() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Xeedit getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	private static String getPosition() {
		try {
			// get the stack element corresponding to the caller of the log method
			StackTraceElement element = new Exception().getStackTrace()[2];
			return " \n[" + element.getClassName() + "#" + element.getMethodName() + " : "
					+ element.getLineNumber() + "]";
		} catch (Throwable e) {
			return "";
		}
	}

	/** Log an informative message (in Eclipse log) */
	public static void logInfo(String msg) {
		Status status = new Status(IStatus.INFO, "EditorPP", 0, msg, null);
		plugin.getLog().log(status);
	}

	/** Log a warning message (in Eclipse log) */
	public static void logWarning(String msg) {
		Status status = new Status(IStatus.WARNING, "EditorPP", 0,
				msg + getPosition(), null);
		plugin.getLog().log(status);
	}

	/** Log an error message (in Eclipse log) */
	public static void logError(String msg) {
		Status status = new Status(IStatus.ERROR, "EditorPP", 0,
				msg + getPosition(), null);
		plugin.getLog().log(status);
	}
	
	/**
	 * Log an error message in Eclipse log.
	 * 
	 * @param exception
	 *            The exception that triggered this error message.
	 */
	public static void logError(String msg, Throwable exception) {
		Status status = new Status(IStatus.ERROR, "EditorPP", 0, msg, exception);
		plugin.getLog().log(status);
	}
	
	/**
	 * @param exception
	 *            The exception to log.
	 */
	public static void logError(Throwable e) {
		Status status = new Status(IStatus.ERROR, "ocaml", 0, e.getMessage(), e);
		plugin.getLog().log(status);
	}
	


}
