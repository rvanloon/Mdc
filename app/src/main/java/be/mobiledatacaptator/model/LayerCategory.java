package be.mobiledatacaptator.model;

public class LayerCategory {

	private String layer;
	private int colorValue;

	public LayerCategory(String layer, int colorValue) {
		setLayer(layer);
		setColorValue(colorValue);
	}

	public String getLayer() {
		return layer;
	}

	public void setLayer(String layer) {
		this.layer = layer;
	}

	public int getColorValue() {
		return colorValue;
	}

	public void setColorValue(int colorValue) {
		this.colorValue = colorValue;
	}

	@Override
	public String toString() {
		return layer;
	}

}
