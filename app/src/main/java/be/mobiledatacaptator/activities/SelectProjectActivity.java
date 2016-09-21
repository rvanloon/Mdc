package be.mobiledatacaptator.activities;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.dao.StartDropBoxApi;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcExceptionLogger;
import be.mobiledatacaptator.utilities.MdcUtil;

public class SelectProjectActivity extends Activity {

	public final static int REQUEST_INITDROPBOX = 1;

	private UnitOfWork unitOfWork;
	private ListView listViewProjects;
	private Button buttonOpenProject = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onStart() {
		super.onStart();
		try {
			// Hier wordt de dropboxapi gestart.
			Intent intent = new Intent(this, StartDropBoxApi.class);
			startActivityForResult(intent, REQUEST_INITDROPBOX);

		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	private void start() {
		try {
			unitOfWork = UnitOfWork.getInstance();

			setTitle(getString(R.string.select_project));

			setContentView(R.layout.activity_select_project);

			listViewProjects = (ListView) findViewById(R.id.listViewProjects);
			buttonOpenProject = (Button) findViewById(R.id.buttonOpenProject);

			loadProjects();
			unitOfWork.setActiveProject(null);

			listViewProjects.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int indexListItem, long arg3) {

					try {
						UnitOfWork.getInstance().setActiveProject(
								(Project) listViewProjects
										.getItemAtPosition(indexListItem));
					} catch (Exception e) {
						MdcExceptionLogger.error(e, SelectProjectActivity.this);
					}
				}
			});

			listViewProjects
					.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
						@Override
						public boolean onItemLongClick(AdapterView<?> parent,
								View view, int indexListItem, long id) {
							try {

								listViewProjects.performItemClick(
										listViewProjects.getAdapter().getView(
												indexListItem, null, null),
										indexListItem,
										listViewProjects.getAdapter()
												.getItemId(indexListItem));

								saveProjectData();
							} catch (Exception e) {
								MdcExceptionLogger.error(e,
										SelectProjectActivity.this);
							}
							return true;
						}
					});

			buttonOpenProject.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View button) {
					try {
						if (UnitOfWork.getInstance().getActiveProject() != null) {
							Intent intent = new Intent(button.getContext(),
									SelectFicheActivity.class);
							startActivity(intent);

						} else {
							MdcUtil.showToastShort(
									getString(R.string.select_project_first),
									getApplicationContext());
						}
					} catch (Exception e) {
						MdcExceptionLogger.error(e, SelectProjectActivity.this);
					}
				}
			});
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	private void loadProjects() {
		try {
			ArrayList<Project> projects = new ArrayList<Project>();

			String xml = unitOfWork.getDao().getFilecontent(
					getString(R.string.dropbox_location_projects));
			DocumentBuilder db = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder();
			Document dom = db.parse(new ByteArrayInputStream(xml.getBytes()));

			Element root = dom.getDocumentElement();
			NodeList forms = root.getElementsByTagName("Project");
			for (int i = 0; i < forms.getLength(); i++) {
				Project myProject = new Project();
				Node projectNode = forms.item(i);

				myProject.setName(projectNode.getAttributes()
						.getNamedItem("Name").getNodeValue());
				myProject.setTemplate(projectNode.getAttributes()
						.getNamedItem("Template").getNodeValue());

				projects.add(myProject);
			}

			ArrayAdapter<Project> myAdapter = new ArrayAdapter<Project>(this,
					android.R.layout.simple_list_item_activated_1, projects);

			listViewProjects.setAdapter(myAdapter);
			listViewProjects.setItemsCanFocus(true);
			listViewProjects.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

	private void saveProjectData() {
		try {
			if (isExternalStorageWritable()
					&& unitOfWork.getActiveProject() != null) {
				//
				final Context ctxt = this;
				DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							try {
								UnitOfWork.getInstance().getDao().dumpToSd();
							} catch (Exception e) {
								e.printStackTrace();
							}
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							break;
						}
					}
				};

				AlertDialog.Builder builder = new AlertDialog.Builder(this);

				String builderMessage = getString(R.string.VraagExportData);

				builder.setNegativeButton(R.string.button_no,
						dialogClickListener)
						.setMessage(builderMessage)
						.setPositiveButton(R.string.button_yes,
								dialogClickListener).show();

			}
		} catch (Exception e) {
			MdcExceptionLogger.error(e, SelectProjectActivity.this);
		}
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			if (requestCode == REQUEST_INITDROPBOX) {
				start();
			} else {
				super.onActivityResult(requestCode, resultCode, data);
			}
		} catch (Exception e) {
			MdcExceptionLogger.error(e, this);
		}
	}

}
