package be.mobiledatacaptator.drawing_model;

import android.graphics.Color;
import android.graphics.Paint;
import be.mobiledatacaptator.model.LayerCategory;

public abstract class BaseFigure implements IDrawable {

	private LayerCategory layer;

	private Paint paint;

	public BaseFigure() {
		paint = new Paint();

		paint.setAntiAlias(true);
		paint.setStrokeWidth(2);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
	}

	@Override
	public abstract String toString();

	public LayerCategory getLayer() {
		return layer;
	}

	public void setLayer(LayerCategory layer) {
		this.layer = layer;
		if (layer != null)
			this.getPaint().setColor(layer.getColorValue());
	}

	public Paint getPaint() {
		return paint;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}

}
