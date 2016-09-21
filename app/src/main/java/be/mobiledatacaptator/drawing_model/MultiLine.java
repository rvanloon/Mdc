package be.mobiledatacaptator.drawing_model;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Point;

public class MultiLine extends BaseFigure {
	List<Point> punten = new ArrayList<Point>();
	private boolean closed;

	public MultiLine(boolean closed) {
		super();
		this.closed = closed;
	}

	@Override
	public void draw(Canvas canvas) {
		Path path = new Path();
		path.moveTo(punten.get(0).x, punten.get(0).y);
		for (Point p : punten) {
			path.lineTo(p.x, p.y);
		}
		if (closed && punten.size() > 2)
			path.close();

		canvas.drawPath(path, getPaint());
	}

	@Override
	public void setDown(Point p) {
		if (!PointEqualsPresident(p))
			punten.add(p);
		if (punten.size() == 1)
			punten.add(p);
	}

	@Override
	public void setMove(Point p) {
		if (punten.size() == 0) {
			punten.add(p);
		} else {
			punten.set(punten.size() - 1, p);
		}
	}

	@Override
	public Boolean setUp(Point p) {
		if (!PointEqualsPresident(p))
			punten.add(p);
		return false;
	}

	@Override
	public String toString() {
		return null;
	}

	@Override
	public void appendXml(Document doc, float screensize, float drawingsize) {
		Element element = doc.createElement("Element");
		element.setAttribute("Type", "MultiLine");
		doc.getFirstChild().appendChild(element);

		Element layer = doc.createElement("Layer");
		layer.appendChild(doc.createTextNode(this.getLayer().toString()));
		element.appendChild(layer);

		if (closed) {
			Element closed = doc.createElement("Gesloten");
			closed.appendChild(doc.createTextNode("JA"));
			element.appendChild(closed);
		}

		for (int i = 0; i < punten.size(); i++) {

			Element point = doc.createElement("Punt");
			Element x = doc.createElement("X");
			x.appendChild(doc.createTextNode(String.valueOf((int) (punten.get(i).x / screensize * drawingsize))));
			Element y = doc.createElement("Y");
			y.appendChild(doc.createTextNode(String.valueOf((int) (punten.get(i).y / screensize * drawingsize))));
			element.appendChild(point);
			point.appendChild(x);
			point.appendChild(y);

		}

	}

	private boolean PointEqualsPresident(Point p) {
		if (punten.size() == 0)
			return false;
		return punten.get(punten.size() - 1).equals(p.x, p.y);
	}

}
