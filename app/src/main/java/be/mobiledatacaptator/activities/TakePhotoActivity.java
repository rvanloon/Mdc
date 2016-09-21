package be.mobiledatacaptator.activities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.model.PhotoCategory;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcExceptionLogger;
import be.mobiledatacaptator.utilities.MdcUtil;

public class TakePhotoActivity extends Activity implements OnClickListener,
		OnItemLongClickListener, OnItemClickListener {

	final static int TAKE_PICTURE = 0;
	private Project project;
	private UnitOfWork unitOfWork;
	private ListView listViewPhotos;
	private Intent startCameraIntent;
	private String prefixFichePhotoName, photoNameToSave, tempFileName,
			textSelectedPhoto;
	private List<String> listFotoNames, listThisFicheFotoNames;
	private TableLayout tableLayoutPhotoCategory;
	private Button buttonFreeSuffix, buttonDisplayPhoto, buttonDeletePhoto;
	private EditText editTextFreeSuffix;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activity_take_photo);
			unitOfWork = UnitOfWork.getInstance();
			project = unitOfWork.getActiveProject();

			// format prefixFichePhotoName = PUT3014
			prefixFichePhotoName = getIntent().getExtras().getString(
					"prefixFichePhotoName");

			listViewPhotos = (ListView) findViewById(R.id.listViewPhotos);
			tableLayoutPhotoCategory = (TableLayout) findViewById(R.id.tableLayoutPhotoCategory);
			editTextFreeSuffix = (EditText) findViewById(R.id.editTextFreeSuffix);
			buttonFreeSuffix = (Button) findViewById(R.id.buttonFreeSuffix);
			buttonDisplayPhoto = (Button) findViewById(R.id.buttonDisplayPhoto);
			buttonDeletePhoto = (Button) findViewById(R.id.buttonDeletePhoto);

			listViewPhotos.setOnItemClickListener(this);
			listViewPhotos.setOnItemLongClickListener(this);

			buttonFreeSuffix.setOnClickListener(photoCategoryListener);
			buttonDisplayPhoto.setOnClickListener(this);
			buttonDeletePhoto.setOnClickListener(this);

			setTitle(MdcUtil.setActivityTitle(unitOfWork,
					getApplicationContext()));

			int index = 0;
			for (PhotoCategory photoCat : project.getPhotoCategories()) {
				addPhotoCategoriesToLayout(photoCat, index);
				index++;
			}

			loadPhotoNames();

			// hide the keyboard
			getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
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

	private void loadPhotoNames() {
		try {
			listFotoNames = unitOfWork.getDao()
					.getAllFilesFromPathWithExtension(
							project.getDataLocation(), ".jpg", false);

			listThisFicheFotoNames = new ArrayList<String>();

			for (String myFotoName : listFotoNames) {
				if (myFotoName.startsWith(prefixFichePhotoName + "_")) {
					listThisFicheFotoNames.add(myFotoName);
				}
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_activated_1,
					listThisFicheFotoNames);
			listViewPhotos.setAdapter(adapter);
			listViewPhotos.setItemsCanFocus(true);
			listViewPhotos.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	private void addPhotoCategoriesToLayout(PhotoCategory photoCategorie,
			int index) {
		try {
			// Android provides a service
			// getSystemService(Context.LAYOUT_INFLATER_SERVICE)' to inflate a
			// layout
			LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View newPhotoCategoryView = inflater.inflate(
					R.layout.new_table_row_photo_category, null);

			// Get a reference to the buttonNewPhotoCategory on the
			// new_table_row_photo_category.xml and set
			// its text + register its onClickListener
			Button buttonNewPhotoCategory = (Button) newPhotoCategoryView
					.findViewById(R.id.buttonNewPhotoCategory);
			buttonNewPhotoCategory.setText(photoCategorie.getName());
			buttonNewPhotoCategory.setTag(photoCategorie.getSuffix());
			buttonNewPhotoCategory.setOnClickListener(photoCategoryListener);

			// Adds programmatically the new_tag_view.xml to the
			// tableLayoutPhotoCategory at the specified index)
			tableLayoutPhotoCategory.addView(newPhotoCategoryView, index);
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	private String composePhotoName(String photoNameToSave) {
		try {
			int number = 0;
			List<Integer> numbers = new ArrayList<Integer>();
			for (String thisFotoName : listThisFicheFotoNames) {
				if (thisFotoName.startsWith(photoNameToSave)) {
					String numberFoto = thisFotoName.substring(photoNameToSave
							.length());

					Pattern p = Pattern.compile("\\d+");
					Matcher m = p.matcher(numberFoto);
					while (m.find()) {
						numbers.add(Integer.valueOf(m.group()));
					}
				}
			}

			try {
				number = Collections.max(numbers) + 1;
			} catch (Exception e) {
				number = 1;
			}

			photoNameToSave = photoNameToSave + number;

			return photoNameToSave;
		} catch (NumberFormatException e) {
			MdcExceptionLogger.error(e, this);
		}

		return null;
	}

	private void startCamera(String photoNameToSave) {

		try {

			File storageDir = Environment
					.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
			File image;

			image = File.createTempFile(photoNameToSave, /* prefix */
					".jpg", /* suffix */
					storageDir /* directory */
			);

			tempFileName = image.getAbsolutePath();

			startCameraIntent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(image));

			startActivityForResult(startCameraIntent, TAKE_PICTURE);

		} catch (IOException IOexception) {
			MdcExceptionLogger.error(IOexception, this);
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}

	}

	// Annonymous Inner Class that implements Interface OnClickListener to
	// respond to the click event
	public OnClickListener photoCategoryListener = new OnClickListener() {

		@Override
		public void onClick(View buttonClicked) {
			try {
				if (buttonClicked.getId() == R.id.buttonFreeSuffix) {

					String suffix = editTextFreeSuffix.getText().toString();

					if (suffix.length() > 0) {
						photoNameToSave = prefixFichePhotoName + "_" + suffix
								+ "_";
						photoNameToSave = composePhotoName(photoNameToSave);

						startCamera(photoNameToSave);

					} else {
						MdcUtil.showToastShort(
								getString(R.string.enter_suffix),
								getApplicationContext());
					}

				} else {
					TableRow buttonTableRow = (TableRow) buttonClicked
							.getParent();
					Button buttonNewPhotoCategory = (Button) buttonTableRow
							.findViewById(R.id.buttonNewPhotoCategory);

					photoNameToSave = prefixFichePhotoName + "_"
							+ buttonNewPhotoCategory.getTag().toString() + "_";
					photoNameToSave = composePhotoName(photoNameToSave);

					startCamera(photoNameToSave);

				}
			} catch (Exception e) {
				MdcExceptionLogger.error(e, TakePhotoActivity.this);
			}

		}
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		try {
			if (requestCode == TAKE_PICTURE) {
				if (resultCode == RESULT_OK) {
					savePhoto();
				} else {
					// throw new Exception("resultcode != RESULT_OK");
				}
			} else {
				throw new Exception("requestCode != TAKE_PICTURE");
			}
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		} finally {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

	private void savePhoto() {
		try {

			File tempFile = new File(tempFileName);
			Bitmap bitmap = BitmapFactory.decodeFile(tempFileName);

			// Grootte aanpassen
			int origWidth = bitmap.getWidth();
			int origHeight = bitmap.getHeight();

			int destWidth = project.getPhotoWidth();
			int destHeight = project.getPhotoHeight();

			// Hoogte en breedte aanpassen naar gelang portrait of landscape
			if (origHeight > origWidth) {
				if (destWidth > destHeight) {
					int temp = destWidth;
					destWidth = destHeight;
					destHeight = temp;
				}
			} else {
				if (destWidth < destHeight) {
					int temp = destWidth;
					destWidth = destHeight;
					destHeight = temp;
				}
			}

			// Hoogte aanpassen
			if (origHeight > destHeight) {
				origWidth = (int) (origWidth / ((double) origHeight / destHeight));
				origHeight = destHeight;
				bitmap = Bitmap.createScaledBitmap(bitmap, origWidth,
						origHeight, false);
			}

			// Breedte aanpassen
			if (origWidth > destWidth) {
				bitmap = Bitmap.createScaledBitmap(bitmap, destWidth,
						(int) (origHeight / ((double) origWidth / destWidth)),
						false);
			}

			// Wegschrijven
			try {
				FileOutputStream stream = new FileOutputStream(tempFile);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 80, stream);
				stream.flush();
				stream.close();
				unitOfWork.getDao().saveFile(
						project.getDataLocation() + photoNameToSave + ".jpg",
						tempFile);
			} catch (Exception e) {
				MdcExceptionLogger.error(e, this);
			}

			// Tijdelijke file verwijderen
			tempFile.delete();

			loadPhotoNames();

		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);

		}
	}

	@Override
	public void onClick(View v) {
		try {
			switch (v.getId()) {
			case R.id.buttonDeletePhoto:
				if (textSelectedPhoto != null && !textSelectedPhoto.isEmpty()) {
					deleteSelectedPhoto(textSelectedPhoto);
				} else {
					MdcUtil.showToastShort(
							getString(R.string.select_photo_first),
							getApplicationContext());
				}
				break;

			case R.id.buttonDisplayPhoto:
				try {
					if (textSelectedPhoto != null
							&& !textSelectedPhoto.isEmpty()) {
						final Intent displayPhotoIntent = new Intent(this,
								DisplayPhotoActivity.class);
						displayPhotoIntent.putExtra("photoToDisplay",
								textSelectedPhoto);
						startActivity(displayPhotoIntent);
					} else {
						MdcUtil.showToastShort(
								getString(R.string.select_photo_first),
								getApplicationContext());
					}
				} catch (Exception e) {
					MdcExceptionLogger.error(e, this);
				}
				break;

			default:
				break;
			}
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	private void deleteSelectedPhoto(String selectedPhotoName) {
		try {
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setTitle(getString(R.string.delete_photo_));
			alertDialogBuilder
					.setMessage(
							String.format(
									getString(R.string.click_yes_to_delete_photo),
									textSelectedPhoto))
					.setCancelable(false)
					.setPositiveButton(getString(R.string.button_yes),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									try {
										unitOfWork.getDao().delete(
												project.getDataLocation()
														+ textSelectedPhoto
														+ ".jpg");
										textSelectedPhoto = null;
										loadPhotoNames();
									} catch (Exception e) {
										MdcExceptionLogger.error(e,
												TakePhotoActivity.this);
									}
								}
							})
					.setNegativeButton(getString(R.string.button_no),
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int indexListItem, long arg3) {
		try {
			textSelectedPhoto = (String) listViewPhotos
					.getItemAtPosition(indexListItem);
			deleteSelectedPhoto(textSelectedPhoto);
			return true;
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int indexListItem,
			long arg3) {
		try {
			textSelectedPhoto = (String) listViewPhotos
					.getItemAtPosition(indexListItem);
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

}
