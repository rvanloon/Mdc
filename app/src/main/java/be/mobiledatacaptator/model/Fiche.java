package be.mobiledatacaptator.model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Fiche {

	private String name;
	private String path;
	private List<Group> groups = new ArrayList<Group>();

	public void appendXml(Document doc) {
		Element element = doc.createElement("DataFiche");
		doc.appendChild(element);
		for (Group group : groups) {
			group.appendXml(doc, element);
		}
	}

	public void loadExistingData(Element element) {
		for (Node childNode = element.getFirstChild(); childNode != null;) {
			Node nextChild = childNode.getNextSibling();
			String s = childNode.getNodeName();
			for (Group group : groups) {
				if (group.getName().equals(s)) {
					group.loadExistingData((Element) childNode);
				}
			}
			childNode = nextChild;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Group> getGroups() {
		return groups;
	}

}
