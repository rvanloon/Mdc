package be.mobiledatacaptator.model;

import java.util.ArrayList;
import java.util.List;

public class Project {

	private String name;
	private String filePrefix;
	private String dataLocation;
	private String template;
	private boolean LoadPhotoActivity;
	private boolean LoadSchetsActivity;
	private List<PhotoCategory> photoCategories = new ArrayList<PhotoCategory>();
	private List<LayerCategory> layerCategories = new ArrayList<LayerCategory>();
	private int photoHeight, photoWidth, drawingSize;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFilePrefix() {
		return filePrefix;
	}

	public void setFilePrefix(String filePrefix) {
		this.filePrefix = filePrefix;
	}

	public String getDataLocation() {
		return dataLocation;
	}

	public void setDataLocation(String dataLocation) {
		if (!(dataLocation.endsWith("/")))
			dataLocation += "/";
		this.dataLocation = dataLocation;
	}

	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}

	public boolean isLoadPhotoActivity() {
		return LoadPhotoActivity;
	}

	public void setLoadPhotoActivity(boolean loadFotoActivity) {
		LoadPhotoActivity = loadFotoActivity;
	}

	public List<PhotoCategory> getPhotoCategories() {
		return photoCategories;
	}

	public void setPhotoCategories(List<PhotoCategory> photoCategories) {
		this.photoCategories = photoCategories;
	}

	public List<LayerCategory> getLayerCategories() {
		return layerCategories;
	}

	public void setLayerCategories(List<LayerCategory> layerCategories) {
		this.layerCategories = layerCategories;
	}

	public boolean isLoadSchetsActivity() {
		return LoadSchetsActivity;
	}

	public void setLoadSchetsActivity(boolean loadSchetsActivity) {
		LoadSchetsActivity = loadSchetsActivity;
	}

	public int getPhotoHeight() {
		return photoHeight;
	}

	public void setPhotoHeight(int photoHeight) {
		this.photoHeight = photoHeight;
	}

	public int getPhotoWidth() {
		return photoWidth;
	}

	public void setPhotoWidth(int photoWidth) {
		this.photoWidth = photoWidth;
	}

	public int getDrawingSize() {
		return drawingSize;
	}

	public void setDrawingSize(int drawingSize) {
		this.drawingSize = drawingSize;
	}

	@Override
	public String toString() {
		return getName();
	}

}
