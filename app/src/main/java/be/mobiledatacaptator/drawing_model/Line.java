package be.mobiledatacaptator.drawing_model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import android.graphics.Canvas;
import android.graphics.Point;
import be.mobiledatacaptator.model.LayerCategory;

public class Line extends BaseFigure {
	private Point startPoint;
	private Point endPoint;

	public Line() {
	};

	public Line(LayerCategory layer, Point startPoint, Point endPoint) {
		setLayer(layer);
		setStartPoint(startPoint);
		setEndPoint(endPoint);
	}

	public Point getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
	}

	public Boolean addPoint(Point point) {
		this.endPoint = point;
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

	public Point getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}

	@Override
	public void draw(Canvas canvas) {
		canvas.drawLine(getStartPoint().x, getStartPoint().y, getEndPoint().x, getEndPoint().y, this.getPaint());

	}

	@Override
	public String toString() {
		return "MdcLine";
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
		closed.appendChild(doc.createTextNode("NEEN"));
		element.appendChild(closed);

		Element startPoint = doc.createElement("Punt");
		Element sX = doc.createElement("X");
		sX.appendChild(doc.createTextNode(String.valueOf((int) (this.getStartPoint().x / screensize * drawingsize))));
		Element sY = doc.createElement("Y");
		sY.appendChild(doc.createTextNode(String.valueOf((int) (this.getStartPoint().y / screensize * drawingsize))));
		element.appendChild(startPoint);
		startPoint.appendChild(sX);
		startPoint.appendChild(sY);

		Element endPoint = doc.createElement("Punt");
		Element eX = doc.createElement("X");
		eX.appendChild(doc.createTextNode(String.valueOf((int) (this.getEndPoint().x / screensize * drawingsize))));
		Element eY = doc.createElement("Y");
		eY.appendChild(doc.createTextNode(String.valueOf((int) (this.getEndPoint().y / screensize * drawingsize))));
		element.appendChild(endPoint);
		endPoint.appendChild(eX);
		endPoint.appendChild(eY);

	}

}
