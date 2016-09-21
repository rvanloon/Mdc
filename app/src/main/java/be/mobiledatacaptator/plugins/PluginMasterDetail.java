package be.mobiledatacaptator.plugins;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.model.UnitOfWork;
import be.mobiledatacaptator.utilities.MdcUtil;

public class PluginMasterDetail extends Activity {

	UnitOfWork unitOfWork;
	ListView listViewPluginMasterDetail;
	Button buttonCancel;

	String masterValue = "";
	String detailValue = "";

	public static final int PLUGIN_MASTER_DETAIL_ACTRESULT = 1001;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plugin_master_detail);

		unitOfWork = UnitOfWork.getInstance();
		listViewPluginMasterDetail = (ListView) findViewById(R.id.listViewPluginMasterDetail);
		buttonCancel = (Button) findViewById(R.id.buttonCancel);
		buttonCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});

		try {
			final List<String> listDataFicheNames = unitOfWork.getDao().getAllFilesFromPathWithExtension(
					unitOfWork.getActiveProject().getDataLocation(), ".xml", false);

			Collections.sort(listDataFicheNames, Collections.reverseOrder());

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_activated_1, listDataFicheNames);
			listViewPluginMasterDetail.setAdapter(adapter);
			listViewPluginMasterDetail.setItemsCanFocus(true);
			listViewPluginMasterDetail.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

			listViewPluginMasterDetail.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int indexListItem, long arg3) {
					loadDetails(listDataFicheNames.get(indexListItem));
				}
			});
		} catch (Exception e) {
			MdcUtil.showToastShort(e.getMessage(), this);
		}

	}

	private void loadDetails(String ficheName) {
		masterValue = ficheName.substring(unitOfWork.getActiveProject().getFilePrefix().length());

		try {
			final List<String> waarden = new ArrayList<String>();
			List<String> teksten = new ArrayList<String>();

			String xml = unitOfWork.getDao().getFilecontent(
					unitOfWork.getActiveProject().getDataLocation() + ficheName + ".xml");
			DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document dom = db.parse(new ByteArrayInputStream(xml.getBytes()));
			Element root = dom.getDocumentElement();
			NodeList detailsXml = root.getElementsByTagName("Leidingen");
			if (detailsXml.getLength() > 0) {
				detailsXml = detailsXml.item(0).getChildNodes();
				for (int i = 0; i < detailsXml.getLength(); i++) {
					if (detailsXml.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element detailXml = (Element) detailsXml.item(i);
						NodeList nodeList;
						nodeList = detailXml.getElementsByTagName("LeidingLetter");
						if (nodeList.getLength() > 0) {
							waarden.add(nodeList.item(0).getTextContent());

							String s = nodeList.item(0).getTextContent();
							nodeList = detailXml.getElementsByTagName("LeidingVorm");
							s += nodeList.getLength() > 0 ? " - " + nodeList.item(0).getTextContent() : "";
							nodeList = detailXml.getElementsByTagName("LeidingBreedte");
							s += nodeList.getLength() > 0 ? " - " + nodeList.item(0).getTextContent() : "";
							nodeList = detailXml.getElementsByTagName("LeidingHoogte");
							s += nodeList.getLength() > 0 ? " - " + nodeList.item(0).getTextContent() : "";
							// ...
							teksten.add(s);
						}
					}
				}
			}

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_activated_1, teksten);
			listViewPluginMasterDetail.setAdapter(adapter);
			listViewPluginMasterDetail.setItemsCanFocus(true);
			listViewPluginMasterDetail.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

			listViewPluginMasterDetail.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1, int indexListItem, long arg3) {
					detailValue = waarden.get(indexListItem);
					getIntent().putExtra("masterValue", masterValue);
					getIntent().putExtra("detailValue", detailValue);
					setResult(RESULT_OK, getIntent());
					finish();
				}
			});

		} catch (Exception e) {
			MdcUtil.showToastShort(e.getMessage(), this);
		}
	}
}
