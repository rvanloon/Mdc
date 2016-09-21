package be.mobiledatacaptator.model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import be.mobiledatacaptator.fragments.ITitleFragment;
import be.mobiledatacaptator.plugins.PluginMasterDetail;
import be.mobiledatacaptator.plugins.PluginMasterDetailField;

public class Tab extends Fragment implements ITitleFragment {

	protected String name;
	private Group group;
	private List<TableRow> dataFields = new ArrayList<TableRow>();
	private Context context;

	private Element xmlTemplate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		ScrollView scrollView = new ScrollView(getActivity());
		TableLayout tableLayout = new TableLayout(getActivity());
		for (View v : dataFields) {
			ViewGroup parent = (ViewGroup) v.getParent();
			if (!(parent == null))
				parent.removeView(v);
			tableLayout.addView(v);
		}
		tableLayout.setColumnStretchable(1, true);
		scrollView.addView(tableLayout);

		return scrollView;
	}

	public Element getXmlTemplate() {
		return xmlTemplate;
	}

	public void setXmlTemplate(Element xmlTemplate) {
		this.xmlTemplate = xmlTemplate;
		loadTemplate();
	}

	public void setContext(Context context) {
		this.context = context;
	}

	@Override
	public String getTitle() {
		return name;
	}

	public void appendXml(Document doc, Element root) {
		String s = name.trim();
		if (s.startsWith("-"))
			s = s.substring(1);
		s = s.replace(' ', '_');
		Element element = doc.createElement(s);
		root.appendChild(element);

		for (TableRow tr : dataFields) {
			if (tr instanceof DataField)
				((DataField) tr).appendXml(doc, element);
		}
	}

	private void loadTemplate() {
		if (xmlTemplate != null) {
			if (xmlTemplate.hasAttribute("Name"))
				name = xmlTemplate.getAttribute("Name");
			NodeList fields = xmlTemplate.getElementsByTagName("Field");
			for (int k = 0; k < fields.getLength(); k++) {
				Element fieldEle = (Element) fields.item(k);
				if (fieldEle.hasAttribute("Type") && fieldEle.getAttribute("Type").equalsIgnoreCase("Plugin")) {

					// Link naar een plugin meegeven
					String pluginName = fieldEle.hasAttribute("Name") ? fieldEle.getAttribute("Name") : "";
					if (pluginName.equalsIgnoreCase("PluginMasterDetail"))
						dataFields.add(new PluginMasterDetailField(context, this));
				} else
					// 'Normaal' dataveld toevoegen
					dataFields.add(new DataField(context, fieldEle, this));
			}
		}
	}

	public void loadExistingData(Element element) {
		for (Node childNode = element.getFirstChild(); childNode != null;) {
			Node nextChild = childNode.getNextSibling();
			String s = childNode.getNodeName();
			DataField dataField = getDataField(s);
			if (dataField != null)
				dataField.setValue(childNode.getTextContent());
			childNode = nextChild;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<DataField> getDataFields() {
		List<DataField> list = new ArrayList<DataField>();
		for (TableRow row : dataFields) {
			if (row instanceof DataField)
				list.add((DataField) row);
		}
		return list;
	}

	public DataField getDataField(String name) {
		for (TableRow tr : dataFields) {
			if (tr instanceof DataField) {
				DataField dataField = (DataField) tr;
				if (dataField.getName().equals(name)) {
					return dataField;
				}
			}
		}
		return null;
	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == PluginMasterDetail.PLUGIN_MASTER_DETAIL_ACTRESULT) {
				String masterField = data.getStringExtra("masterField");
				String masterValue = data.getStringExtra("masterValue");
				String detailField = data.getStringExtra("detailField");
				String detailValue = data.getStringExtra("detailValue");

				DataField dataField = getDataField(masterField);
				if (dataField != null)
					dataField.setValue(masterValue);
				dataField = getDataField(detailField);
				if (dataField != null)
					dataField.setValue(detailValue);

			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
