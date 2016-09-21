package be.mobiledatacaptator.drawing_model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.Log;
import be.mobiledatacaptator.model.LayerCategory;

public class Text extends BaseFigure {

	private Point point;
	private String text;

	public Text() {
	}

	public Text(String text, int x, int y, LayerCategory layer) {
		setPoint(new Point(x, y));
		setText(text);
		setLayer(layer);
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void draw(Canvas canvas) {
		try {
			Paint paint = getPaint();
			paint.setTextSize(20);
			canvas.drawText(text, getPoint().x, getPoint().y, paint);
		} catch (Exception e) {
			Log.e("draw text", e.getLocalizedMessage());

		}
	}

	@Override
	public String toString() {
		return text;
	}

	@Override
	public void setDown(Point p) {
		this.point = p;
	}

	@Override
	public void setMove(Point p) {
		this.point = p;
	}

	@Override
	public Boolean setUp(Point p) {
		this.point = p;
		return true;
	}

	@Override
	public void appendXml(Document doc, float screensize, float drawingsize) {
		Element element = doc.createElement("Element");
		element.setAttribute("Type", "Tekst");
		doc.getFirstChild().appendChild(element);

		Element layer = doc.createElement("Layer");
		layer.appendChild(doc.createTextNode(this.getLayer().toString()));
		element.appendChild(layer);

		Element tekst = doc.createElement("Tekst");
		tekst.appendChild(doc.createTextNode(this.getText()));
		element.appendChild(tekst);

		Element centrum = doc.createElement("Centrum");
		Element x = doc.createElement("X");
		x.appendChild(doc.createTextNode(String.valueOf((int) (this.getPoint().x / screensize * drawingsize))));
		Element y = doc.createElement("Y");
		y.appendChild(doc.createTextNode(String.valueOf((int) (this.getPoint().y / screensize * drawingsize))));
		element.appendChild(centrum);
		centrum.appendChild(x);
		centrum.appendChild(y);

	}

}
