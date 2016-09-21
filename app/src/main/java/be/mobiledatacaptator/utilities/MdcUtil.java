package be.mobiledatacaptator.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.widget.Toast;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;

public class MdcUtil {

	public static void showToastShort(String message, Context context) {
		if (message == null || message == "") {
			message = context.getString(R.string.unknown_error);
		}

		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void showToastShort(int message, Context context) {
		if (message == -1) {
			message = R.string.unknown_error;
		}
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void showToastLong(String message, Context context) {
		if (message == null || message == "") {
			message = context.getString(R.string.unknown_error);
		}

		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
	}

	public static String setActivityTitle(UnitOfWork unitOfWork, Context context) {
		Project project = unitOfWork.getActiveProject();
		String projectName = unitOfWork.getActiveProject().getName();
		String inputString = unitOfWork.getActiveFiche().getName().substring(project.getFilePrefix().length());

		return projectName + " - " + context.getString(R.string.fiche) + " " + inputString;
	}

	public static String setActivityTitle(String ficheName, UnitOfWork unitOfWork, Context context) {
		Project project = unitOfWork.getActiveProject();
		String projectName = unitOfWork.getActiveProject().getName();
		String inputString = "";
		if (ficheName.startsWith(project.getFilePrefix())) {
			inputString = ficheName.substring(project.getFilePrefix().length());
		} else {
			inputString = ficheName;
		}

		return projectName + " - " + context.getString(R.string.fiche) + " " + inputString;
	}

	public String increaseNumber(String inputString) {
		if (inputString != null && !(inputString.equals(""))) {

			String input = inputString;
			String result = input;
			Pattern p = Pattern.compile("[0-9]+$");
			Matcher m = p.matcher(input);
			if (m.find()) {
				result = m.group();
				int t = Integer.parseInt(result);
				result = input.substring(0, input.length() - result.length()) + ++t;
				return result;
			} else {
				return result + "1";
			}

		}
		return null;
	}

}
