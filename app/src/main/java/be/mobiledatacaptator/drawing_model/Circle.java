package be.mobiledatacaptator.drawing_model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Point;
import be.mobiledatacaptator.model.LayerCategory;

public class Circle extends BaseFigure {
	private Point point;
	private float radius;

	public Circle() {
	};

	public Circle(int radius, int x, int y, LayerCategory layer) {
		setPoint(new Point(x, y));
		setRadius(radius);
		setLayer(layer);
	}

	public Boolean addPoint(Point addPoint) {
		float a = addPoint.x - point.x;
		float b = addPoint.y - point.y;

		radius = (float) Math.sqrt(a * a + b * b);
		return true;
	}

	@Override
	public void setDown(Point p) {
		this.point = p;
	}

	@Override
	public void setMove(Point p) {
		addPoint(p);
	}

	@Override
	public Boolean setUp(Point p) {
		return addPoint(p);
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public String toString() {
		return "Circle";
	}

	@Override
	public void appendXml(Document doc, float screensize, float drawingsize) {
		Element element = doc.createElement("Element");
		element.setAttribute("Type", "Cirkel");
		doc.getFirstChild().appendChild(element);

		Element layer = doc.createElement("Layer");
		layer.appendChild(doc.createTextNode(this.getLayer().toString()));
		element.appendChild(layer);

		Element straal = doc.createElement("Straal");
		straal.appendChild(doc.createTextNode(String.valueOf((int) (this.getRadius() / screensize * drawingsize))));
		element.appendChild(straal);

		Element centrum = doc.createElement("Centrum");
		Element x = doc.createElement("X");
		x.appendChild(doc.createTextNode(String.valueOf((int) (this.getPoint().x / screensize * drawingsize))));
		Element y = doc.createElement("Y");
		y.appendChild(doc.createTextNode(String.valueOf((int) (this.getPoint().y / screensize * drawingsize))));
		element.appendChild(centrum);
		centrum.appendChild(x);
		centrum.appendChild(y);
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawCircle(getPoint().x, getPoint().y, getRadius(), getPaint());
	}

}
