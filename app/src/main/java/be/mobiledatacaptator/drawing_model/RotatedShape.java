package be.mobiledatacaptator.drawing_model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;

public class RotatedShape extends BaseFigure {

	Point[] punten = new Point[4];

	public Point[] getPunten() {
		return punten;
	}

	@Override
	public void draw(Canvas canvas) {
		Path path = new Path();
		path.moveTo(punten[0].x, punten[0].y);
		path.lineTo(punten[1].x, punten[1].y);
		if (punten[2] != null) {
			path.lineTo(punten[2].x, punten[2].y);
			path.lineTo(punten[3].x, punten[3].y);
			path.close();
		}
		canvas.drawPath(path, getPaint());
	}

	@Override
	public void setDown(Point p) {
		if (punten[0] == null) {
			punten[0] = p;
		} else if (punten[1] == null) {
			punten[1] = p;
		} else {
			punten[2] = p;
			bereken();
		}
	}

	@Override
	public void setMove(Point p) {
		if (punten[2] == null) {
			punten[1] = p;
		} else {
			punten[2] = p;
			bereken();
		}
	}

	@Override
	public Boolean setUp(Point p) {
		if (punten[2] == null) {
			punten[1] = p;
			return false;
		} else {
			punten[2] = p;
			bereken();
			return true;
		}
	}

	private double m = 0, b = 0;

	private void bereken() {
		if (m == 0 || b == 0) {
			m = (double) (punten[1].y - punten[0].y) / (punten[1].x - punten[0].x);
			b = (double) punten[0].y - (m * punten[0].x);
		}

		double x = (m * punten[2].y + punten[2].x - m * b) / (m * m + 1);
		double y = (m * m * punten[2].y + m * punten[2].x + b) / (m * m + 1);
		punten[1] = new Point((int) x, (int) y);

		punten[3] = new Point(punten[2].x + (punten[0].x - punten[1].x), punten[2].y + (punten[0].y - punten[1].y));
	}

	@Override
	public String toString() {
		return "RotatedShape";
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

		for (int i = 0; i < 4; i++) {

			Element point = doc.createElement("Punt");
			Element x = doc.createElement("X");
			x.appendChild(doc.createTextNode(String.valueOf((int) (punten[i].x / screensize * drawingsize))));
			Element y = doc.createElement("Y");
			y.appendChild(doc.createTextNode(String.valueOf((int) (punten[i].y / screensize * drawingsize))));
			element.appendChild(point);
			point.appendChild(x);
			point.appendChild(y);

		}

	}

}
