package be.mobiledatacaptator.drawing_model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Point;

public class Shape extends BaseFigure {

	private float l, r, b, o;
	private Point startPoint, endPoint;
	private Point centerPoint;

	@Override
	public void draw(Canvas canvas) {
		canvas.drawRect(l, b, r, o, getPaint());
	}

	public void setStartPoint(Point p) {
		centerPoint = p;
	}

	public Boolean addPoint(Point p) {
		endPoint = p;

		startPoint = new Point();
		startPoint.x = (centerPoint.x - (endPoint.x - centerPoint.x));
		startPoint.y = (centerPoint.y - (endPoint.y - centerPoint.y));
		bereken();
		return true;
	}

	@Override
	public void setDown(Point p) {
		setStartPoint(p);
	}

	@Override
	public void setMove(Point p) {
		addPoint(p);
	}

	@Override
	public Boolean setUp(Point p) {
		return addPoint(p);
	}

	private void bereken() {
		if (startPoint.x < endPoint.x) {
			l = startPoint.x;
			r = endPoint.x;
		} else {
			l = endPoint.x;
			r = startPoint.x;
		}
		if (startPoint.y < endPoint.y) {
			b = startPoint.y;
			o = endPoint.y;
		} else {
			b = endPoint.y;
			o = startPoint.y;
		}
	}

	@Override
	public String toString() {
		return "Shape";
	}

	@Override
	public void appendXml(Document doc, float screensize, float drawingsize) {
		Element element = doc.createElement("Element");
		element.setAttribute("Type", "Polygoon");
		doc.getFirstChild().appendChild(element);

		Element layer = doc.createElement("Layer");
		layer.appendChild(doc.createTextNode(this.getLayer().toString()));
		element.appendChild(layer);

		Element closed = doc.createElement("Gesloten");
		closed.appendChild(doc.createTextNode("JA"));
		element.appendChild(closed);

		String startX = String.valueOf((int) (this.startPoint.x / screensize * drawingsize));
		String startY = String.valueOf((int) (this.startPoint.y / screensize * drawingsize));
		String endX = String.valueOf((int) (this.endPoint.x / screensize * drawingsize));
		String endY = String.valueOf((int) (this.endPoint.y / screensize * drawingsize));

		Element punt1 = doc.createElement("Punt");
		Element punt1X = doc.createElement("X");
		punt1X.appendChild(doc.createTextNode(startX));
		Element punt1Y = doc.createElement("Y");
		punt1Y.appendChild(doc.createTextNode(startY));
		element.appendChild(punt1);
		punt1.appendChild(punt1X);
		punt1.appendChild(punt1Y);

		Element punt2 = doc.createElement("Punt");
		Element punt2X = doc.createElement("X");
		punt2X.appendChild(doc.createTextNode(startX));
		Element punt2Y = doc.createElement("Y");
		punt2Y.appendChild(doc.createTextNode(endY));
		element.appendChild(punt2);
		punt2.appendChild(punt2X);
		punt2.appendChild(punt2Y);

		Element punt3 = doc.createElement("Punt");
		Element punt3X = doc.createElement("X");
		punt3X.appendChild(doc.createTextNode(endX));
		Element punt3Y = doc.createElement("Y");
		punt3Y.appendChild(doc.createTextNode(endY));
		element.appendChild(punt3);
		punt3.appendChild(punt3X);
		punt3.appendChild(punt3Y);

		Element punt4 = doc.createElement("Punt");
		Element punt4X = doc.createElement("X");
		punt4X.appendChild(doc.createTextNode(endX));
		Element punt4Y = doc.createElement("Y");
		punt4Y.appendChild(doc.createTextNode(startY));
		element.appendChild(punt4);
		punt4.appendChild(punt4X);
		punt4.appendChild(punt4Y);

	}

	public void setXMLStartPoint(Point startPoint) {
		this.startPoint = startPoint;

	}

	public void setXMLEndPoint(Point endPoint) {
		this.endPoint = endPoint;
		bereken();

	}
}
