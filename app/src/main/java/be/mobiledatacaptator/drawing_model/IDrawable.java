package be.mobiledatacaptator.drawing_model;

import org.w3c.dom.Document;

import android.graphics.Canvas;
import android.graphics.Point;

public interface IDrawable {
	void draw(Canvas canvas) throws Exception;
	
	void setDown(Point p);
	void setMove(Point p);
	Boolean setUp(Point p);

	void appendXml(Document doc, float screensize, float drawingsize) throws Exception;
}
