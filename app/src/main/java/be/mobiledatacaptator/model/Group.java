package be.mobiledatacaptator.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.adapters.FichePagerAdapter;

public class Group extends ViewPager {

	private String name;
	private boolean expandable;
	private List<Tab> tabs = new ArrayList<Tab>();
	private Fiche fiche;

	private FichePagerAdapter fichePagerAdapter;

	private Element xmlTemplate;
	private Element xmlTabTemplate;

	public Group(Context context, Element xml, FichePagerAdapter adapter, Fiche fiche) {
		super(context);
		xmlTemplate = xml;
		fichePagerAdapter = adapter;
		this.fiche = fiche;
		setAdapter(fichePagerAdapter);
		loadTemplate();
	}

	public Group(Context context) {
		super(context);
	}

	private void loadTemplate() {
		// Velden invullen aan hand van xml element.
		if (xmlTemplate.hasAttribute("Name"))
			name = xmlTemplate.getAttribute("Name");
		if (xmlTemplate.hasAttribute("Expandable")
				&& xmlTemplate.getAttribute("Expandable").toLowerCase(Locale.getDefault()).equals("true"))
			expandable = true;
		NodeList tabsNodes = xmlTemplate.getElementsByTagName("Tab");
		for (int i = 0; i < tabsNodes.getLength(); i++) {
			Tab tab = new Tab();
			tab.setContext(getContext());
			tab.setXmlTemplate((Element) tabsNodes.item(i));
			tabs.add(tab);
		}

		// Tabtemplate
		tabsNodes = xmlTemplate.getElementsByTagName("TabTemplate");
		if (tabsNodes != null && tabsNodes.getLength() > 0) {
			xmlTabTemplate = (Element) tabsNodes.item(0);
		}
	}

	public TabSpec getTabSpec(TabHost tabHost) {
		TabSpec spec = tabHost.newTabSpec(name);
		spec.setIndicator(name);
		final Group group = this;
		spec.setContent(new TabContentFactory() {

			@Override
			public View createTabContent(String tag) {

				PagerTitleStrip strip = new PagerTitleStrip(getContext());
				ViewPager.LayoutParams layoutParams = new ViewPager.LayoutParams();
				layoutParams.height = android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
				layoutParams.width = android.view.ViewGroup.LayoutParams.MATCH_PARENT;
				layoutParams.gravity = Gravity.TOP;
				strip.setBackgroundColor(Color.DKGRAY);
				addView(strip, layoutParams);

				for (Tab tab : tabs) {
					fichePagerAdapter.addItem(tab);
				}

				if (expandable) {
					LinearLayout layout = new LinearLayout(getContext());
					layout.setOrientation(LinearLayout.VERTICAL);
					Button button = new Button(getContext());
					button.setText(R.string.AddNew);
					layout.addView(button);
					layout.addView(group);
					button.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							group.addTabFromTemplate();
						}
					});
					return layout;
				}
				return group;
			}
		});
		return spec;
	}

	private void addTabFromTemplate() {
		Tab tab = new Tab();
		tab.setContext(getContext());
		tab.setXmlTemplate(xmlTabTemplate);
		tab.setGroup(this);
		tabs.add(tab);
		fichePagerAdapter.addItem(tab);
		setCurrentItem(tabs.size());
	}

	public void appendXml(Document doc, Element root) {
		Element element = doc.createElement(name);
		root.appendChild(element);
		for (Tab tab : tabs) {
			tab.appendXml(doc, element);
		}
	}

	public void loadExistingData(Element element) {
		for (Node childNode = element.getFirstChild(); childNode != null;) {
			Node nextChild = childNode.getNextSibling();
			if (childNode.getNodeType() == Node.ELEMENT_NODE) {
				String s = childNode.getNodeName();
				if (expandable) {
					Tab tab = new Tab();
					tab.setGroup(this);
					tab.setContext(getContext());
					tab.setXmlTemplate(xmlTabTemplate);
					tab.setName(s);
					tab.loadExistingData((Element) childNode);
					tabs.add(tab);
				} else {
					for (Tab tab : tabs) {
						if (tab.getName().equals(s)) {
							tab.loadExistingData((Element) childNode);
						}
					}
				}
			}
			childNode = nextChild;
		}
	}

	public void notifyDataSetChanged() {
		fichePagerAdapter.notifyDataSetChanged();
	}

	public String getName() {
		return name;
	}

	public List<Tab> getTabs() {
		return tabs;
	}

	public Fiche getFiche() {
		return fiche;
	}

}
