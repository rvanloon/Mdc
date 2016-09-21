package be.mobiledatacaptator;

import android.app.Application;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcExceptionLogger;

public class MdcApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		try {
			// De 'unitOfWork' hier eerst initialiseren, dan blijft hij zeker
			// bestaan zolang de app draait.
			UnitOfWork.getInstance();

		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

}
