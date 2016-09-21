package be.mobiledatacaptator.utilities;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import be.mobiledatacaptator.model.UnitOfWork;

public class MdcExceptionLogger {
	private static UnitOfWork unitOfWork;
	private static String tagClassName;
	private static Context myContext;

	public enum Level {
		DEBUG, INFO, WARN, ERROR
	}

	private static void loggerSetup(Context setupContext) {
		myContext = setupContext;
		unitOfWork = UnitOfWork.getInstance();
		tagClassName = setupContext.getClass().getSimpleName();
	}

	public static void debug(Exception e, Context context) {
		loggerSetup(context);
		Log.d(tagClassName, e.getLocalizedMessage());
		writeToLogFile(Level.DEBUG, e);
	}

	public static void info(Exception e, Context context) {
		loggerSetup(context);
		Log.i(tagClassName, e.getLocalizedMessage());
		writeToLogFile(Level.INFO, e);
	}

	public static void warn(Exception e, Context context) {
		loggerSetup(context);
		Log.w(tagClassName, e.getLocalizedMessage());
		writeToLogFile(Level.WARN, e);
	}

	public static void error(Exception e, Context context) {
		loggerSetup(context);
		Log.e(tagClassName, e.getLocalizedMessage());
		writeToLogFile(Level.ERROR, e);
	}

	@SuppressLint("SimpleDateFormat")
	private static void writeToLogFile(Level level, Exception e, Object... parameters) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
		Date date = new Date();

		StringBuilder eMsg = new StringBuilder();
		eMsg.append(level.toString());
		eMsg.append("_");
		eMsg.append(dateFormat.format(date));
		eMsg.append("_");

		try {
			eMsg.append(unitOfWork.getActiveProject().getName());
			// Nog geen project gekozen -> error
			eMsg.append("_");
		} catch (Exception e1) {
			eMsg.append("NoProjectChoice");
			eMsg.append("_");
		}
		eMsg.append(tagClassName);
		eMsg.append("_");
		eMsg.append(e.getStackTrace()[0].getMethodName());
		eMsg.append("_");
		eMsg.append(e.getLocalizedMessage());

		try {
			// Voor SPC-account:
			 unitOfWork.getDao().appendStringToFile("MobileDataCaptator/exception_log.txt",
			 eMsg.toString() + ";\n");

			// Voor DataCaptator-account:
			// unitOfWork.getDao().appendStringToFile("DataCaptator/ExceptionLog/exception_log.txt",
			// eMsg.toString() + ";\n");

			if (level == Level.ERROR) {
				showExceptionDialog(e);
			}

		} catch (Exception ex) {
			showExceptionDialog(ex);

		}
	}

	private static void showExceptionDialog(Exception ex) {

		if (myContext != null) {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(myContext);
			alertDialogBuilder.setTitle("Application Error!");
			alertDialogBuilder.setMessage("Contact your IT-Administrator \n" + ex.getMessage()).setCancelable(true)
					.setNegativeButton("OK", null);
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		}

	}
}