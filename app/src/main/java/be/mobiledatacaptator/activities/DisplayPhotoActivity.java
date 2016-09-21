package be.mobiledatacaptator.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcExceptionLogger;

public class DisplayPhotoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			setContentView(R.layout.activity_display_photo);

			Bitmap bitMap;
			ImageView imageViewDisplayPhoto;
			Project project;
			UnitOfWork unitOfWork;

			imageViewDisplayPhoto = (ImageView) findViewById(R.id.imageViewDisplayPhoto);

			String photoToDisplay = null;

			unitOfWork = UnitOfWork.getInstance();
			project = unitOfWork.getActiveProject();

			photoToDisplay = getIntent().getExtras().get("photoToDisplay").toString();

			setTitle(getString(R.string.photo) + " " + photoToDisplay);

			bitMap = unitOfWork.getDao().getBitmapFromFile(project.getDataLocation() + photoToDisplay + ".jpg");
			imageViewDisplayPhoto.setImageBitmap(bitMap);

		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			switch (item.getItemId()) {
			case android.R.id.home:
				onBackPressed();
				return (true);
			}
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
		return (super.onOptionsItemSelected(item));
	}

}
