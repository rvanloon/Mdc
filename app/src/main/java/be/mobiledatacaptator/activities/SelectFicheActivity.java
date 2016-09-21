package be.mobiledatacaptator.activities;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.model.Fiche;
import be.mobiledatacaptator.model.LayerCategory;
import be.mobiledatacaptator.model.PhotoCategory;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcExceptionLogger;
import be.mobiledatacaptator.utilities.MdcUtil;

public class SelectFicheActivity extends Activity implements OnClickListener {
	private Project project;
	private ListView listViewFiches;
	private UnitOfWork unitOfWork;
	private String ficheName = "";
	private Button buttonAddNumber, buttonOpenFiche, buttonOpenPhoto, buttonOpenDrawing;
	private EditText editTextFicheName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			setContentView(R.layout.activity_select_fiche);

			unitOfWork = UnitOfWork.getInstance();
			project = unitOfWork.getActiveProject();

			setTitle(getString(R.string.project) + " " + project.getName());

			listViewFiches = (ListView) findViewById(R.id.listViewFiches);
			// TODO - 3 regels moeten in comment na testen
			// loadProjectData();
			// loadDataFiches();
			// listViewFiches.requestFocus();

			buttonAddNumber = (Button) findViewById(R.id.buttonAddNumber);
			buttonOpenFiche = (Button) findViewById(R.id.buttonOpenFiche);
			buttonOpenPhoto = (Button) findViewById(R.id.buttonOpenPhoto);
			buttonOpenDrawing = (Button) findViewById(R.id.buttonOpenDrawing);
			editTextFicheName = (EditText) findViewById(R.id.editTextFicheName);

			buttonAddNumber.setOnClickListener(this);
			buttonOpenFiche.setOnClickListener(this);
			buttonOpenPhoto.setOnClickListener(this);
			buttonOpenDrawing.setOnClickListener(this);
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

		try {
			// TODO - 3 regels moeten uit comment na testen
			loadProjectData();
			loadDataFiches();
			listViewFiches.requestFocus();
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			switch (item.getItemId()) {
			case android.R.id.home:
				UnitOfWork.getInstance().setActiveFiche(null);
				finish();
				return (true);
			}
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}

		return (super.onOptionsItemSelected(item));
	}

	private void loadProjectData() {
		try {
			String xml = unitOfWork.getDao().getFilecontent(unitOfWork.getActiveProject().getTemplate());

			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document dom = db.parse(new ByteArrayInputStream(xml.getBytes()));

			Element root = dom.getDocumentElement();

			project.setDataLocation(root.getAttribute("DataLocatie"));
			project.setFilePrefix(root.getAttribute("FilePrefix"));

			// photo-functionality
			if ((root.getAttribute("LoadFotoActivity").equals("true"))) {
				project.setLoadPhotoActivity(true);
				NodeList nodes = root.getElementsByTagName("FotoCategorie");
				project.setPhotoCategories(new ArrayList<PhotoCategory>());
				for (int i = 0; i < nodes.getLength(); i++) {
					Node node = nodes.item(i);
					PhotoCategory photoCategorie = new PhotoCategory(((Element) node).getAttribute("Name"),
							((Element) node).getAttribute("Suffix"));
					project.getPhotoCategories().add(photoCategorie);
				}

				try {
					project.setPhotoHeight(Integer.parseInt(root.getAttribute("PhotoHeight")));
					project.setPhotoWidth(Integer.parseInt(root.getAttribute("PhotoWidth")));
				} catch (Exception e) {
					project.setPhotoHeight(960);
					project.setPhotoWidth(1280);

					Exception myException = new Exception("photoWidth & photoHeight programmatically set");
					MdcExceptionLogger.warn(myException, this);
					myException = null;
				}

			} else // no photo-Activity
			{
				project.setLoadPhotoActivity(false);
				buttonOpenPhoto.setVisibility(View.INVISIBLE);
			}

			// drawing-functionality
			if ((root.getAttribute("LoadSchetsActivity").equals("true"))) {

				project.setLoadSchetsActivity(true);

				NodeList layerCategories = root.getElementsByTagName("LayerCategorie");
				project.setLayerCategories(new ArrayList<LayerCategory>());
				for (int i = 0; i < layerCategories.getLength(); i++) {
					Node node = layerCategories.item(i);

					LayerCategory layerCategorie = new LayerCategory(((Element) node).getAttribute("Name"),
							Color.parseColor(((Element) node).getAttribute("Color")));
					project.getLayerCategories().add(layerCategorie);

				}

				try {
					project.setDrawingSize(Integer.parseInt(root.getAttribute("DrawingSize")));
				} catch (Exception e) {

					project.setDrawingSize(10000);

					Exception myException = new Exception("drawingSize programmatically set");
					MdcExceptionLogger.warn(myException, this);
					myException = null;
				}

			} else {

				project.setLoadSchetsActivity(false);
				try {
					buttonOpenDrawing.setVisibility(View.INVISIBLE);
				} catch (Exception e) {

				}

			}

		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	private void loadDataFiches() {
		try {
			List<String> listDataFicheNames = unitOfWork.getDao().getAllFilesFromPathWithExtension(
					project.getDataLocation(), ".xml", false);

			Collections.sort(listDataFicheNames, Collections.reverseOrder());

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_activated_1, listDataFicheNames);
			listViewFiches.setAdapter(adapter);
			listViewFiches.setItemsCanFocus(true);
			listViewFiches.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

			listViewFiches.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int indexListItem, long arg3) {
					String textListItem = (String) listViewFiches.getItemAtPosition(indexListItem);
					editTextFicheName.setText(textListItem.substring(project.getFilePrefix().length()));
					setTitle(MdcUtil.setActivityTitle(textListItem, unitOfWork, getApplicationContext()));

				}
			});

			// ////////////////////

			listViewFiches.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
				@Override
				public boolean onItemLongClick(AdapterView<?> parent, View view, int indexListItem, long id) {
					try {

						listViewFiches.performItemClick(listViewFiches.getAdapter().getView(indexListItem, null, null),
								indexListItem, listViewFiches.getAdapter().getItemId(indexListItem));

						buttonOpenFiche.performClick();

					} catch (Exception e) {
						MdcExceptionLogger.error(e, SelectFicheActivity.this);
					}
					return true;
				}
			});

			// /////////////////////////////////////

		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	@Override
	public void onClick(View v) {
		try {
			ficheName = editTextFicheName.getText().toString();
			unitOfWork.setActiveFiche(null);

			if (ficheName != null && !(ficheName.equals(""))) {
				Fiche fiche = new Fiche();
				fiche.setName(project.getFilePrefix() + ficheName);
				fiche.setPath(project.getDataLocation() + fiche.getName() + ".xml");
				unitOfWork.setActiveFiche(fiche);
			}

			switch (v.getId()) {

			case R.id.buttonAddNumber:
				increaseFicheNumber();
				break;

			case R.id.buttonOpenFiche:
				openFiche();
				break;

			case R.id.buttonOpenPhoto:
				openPhoto();
				break;

			case R.id.buttonOpenDrawing:
				openDrawing();
				break;

			default:
				break;
			}
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	private void increaseFicheNumber() {
		try {
			if (ficheName != null && !(ficheName.equals(""))) {

				String input = ficheName;
				String result = input;
				Pattern p = Pattern.compile("[0-9]+$");
				Matcher m = p.matcher(input);
				if (m.find()) {
					result = m.group();
					int t = Integer.parseInt(result);
					result = input.substring(0, input.length() - result.length()) + ++t;
					editTextFicheName.setText(result);
				} else {
					editTextFicheName.setText(result + "1");
				}

				setTitle(MdcUtil.setActivityTitle(editTextFicheName.getText().toString(), unitOfWork,
						getApplicationContext()));
			}
		} catch (NumberFormatException e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	private void openFiche() {
		try {
			if (UnitOfWork.getInstance().getActiveFiche() != null) {

				final Intent intent = new Intent(this, FicheActivity.class);
				if (unitOfWork.getDao().existsFile(UnitOfWork.getInstance().getActiveFiche().getPath())) {
					DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							switch (which) {
							case DialogInterface.BUTTON_POSITIVE:
								startActivity(intent);
								break;
							case DialogInterface.BUTTON_NEGATIVE:
								// No button clicked
								break;
							}
						}
					};

					AlertDialog.Builder builder = new AlertDialog.Builder(this);

					String ficheName = unitOfWork.getActiveFiche().getName();
					// String builderMessage = getString(R.string.wil_u_fiche) +
					// " " + ficheName + " " + getString(R.string.openen);
					String builderMessage = String.format(getString(R.string.do_you_want_to_open_fiche_x), ficheName);

					builder.setNegativeButton(R.string.button_no, dialogClickListener).setMessage(builderMessage)
							.setPositiveButton(R.string.button_yes, dialogClickListener).show();
				} else {
					startActivity(intent);
				}

			} else {
				MdcUtil.showToastShort(getString(R.string.select_fiche_first), getApplicationContext());
			}

		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	private void openPhoto() {
		try {
			if (UnitOfWork.getInstance().getActiveFiche() != null) {
				final Intent takePictureIntent = new Intent(this, TakePhotoActivity.class);
				takePictureIntent.putExtra("prefixFichePhotoName", project.getFilePrefix() + ficheName);
				startActivity(takePictureIntent);

			} else {
				MdcUtil.showToastShort(getString(R.string.select_fiche_first), getApplicationContext());
			}

		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	private void openDrawing() {
		try {
			if (UnitOfWork.getInstance().getActiveFiche() != null) {
				final Intent drawingIntent = new Intent(this, DrawingActivity.class);
				drawingIntent.putExtra("prefixFicheDrawingName", project.getFilePrefix() + ficheName);
				startActivity(drawingIntent);

			} else {
				MdcUtil.showToastShort(getString(R.string.select_fiche_first), getApplicationContext());
			}

		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

}
