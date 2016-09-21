package be.mobiledatacaptator.drawing_views;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import be.mobiledatacaptator.R;
import be.mobiledatacaptator.activities.DrawingActivity;
import be.mobiledatacaptator.drawing_model.BaseFigure;
import be.mobiledatacaptator.drawing_model.Circle;
import be.mobiledatacaptator.drawing_model.FigureType;
import be.mobiledatacaptator.drawing_model.IDrawable;
import be.mobiledatacaptator.drawing_model.Line;
import be.mobiledatacaptator.drawing_model.MultiLine;
import be.mobiledatacaptator.drawing_model.RotatedShape;
import be.mobiledatacaptator.drawing_model.Shape;
import be.mobiledatacaptator.drawing_model.Text;
import be.mobiledatacaptator.model.LayerCategory;
import be.mobiledatacaptator.utilities.MdcExceptionLogger;
import be.mobiledatacaptator.utilities.MdcUtil;

public class DrawingView extends View implements OnTouchListener {

	private FigureType figureType = FigureType.Line;
	private List<IDrawable> iDrawables = new ArrayList<IDrawable>();
	private LayerCategory layer;
	boolean startNewFigure = true;
	private BaseFigure activeFigure;
	private Boolean fromCenter = false;
	private String inputText;
	private DrawingActivity drawingActivity;

	public DrawingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setOnTouchListener(this);

	}

	public void setFigureType(FigureType figureType) {
		this.figureType = figureType;
		startNewFigure = true;
	}

	public Boolean getFromCenter() {
		return fromCenter;
	}

	public void setFromCenter(Boolean fromCenter) {
		this.fromCenter = fromCenter;
	}

	public LayerCategory getLayer() {
		return layer;
	}

	public void setLayer(LayerCategory layer) {
		this.layer = layer;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		try {
			for (IDrawable figure : iDrawables) {
				figure.draw(canvas);

			}
		} catch (Exception e) {
			MdcExceptionLogger.error(e, getContext());
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();

		Point p = new Point(x, y);

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (startNewFigure) {
				makeNewFigure();
			}

			if (fromCenter) {
				p.x = view.getWidth() / 2;
				p.y = view.getHeight() / 2;
			}

			activeFigure.setDown(p);

			activeFigure.setLayer(getLayer());

			if (activeFigure instanceof Text) {
				Text textInput = (Text) activeFigure;
				if (this.inputText != null) {
					textInput.setText(inputText);
				} else {
					textInput.setText("");
					MdcUtil.showToastShort(R.string.enter_text, getContext());
				}
			}
			break;

		case MotionEvent.ACTION_MOVE:
			activeFigure.setMove(p);
			invalidate();
			break;

		case MotionEvent.ACTION_UP:
			startNewFigure = activeFigure.setUp(p);
			invalidate();
			break;

		default:
			return super.onTouchEvent(event);
		}
		return true;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// Er voor zorgen dat deze view altijd een vierkant is.
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int imageSize = getMeasuredWidth();
		if (getMeasuredHeight() < imageSize)
			imageSize = getMeasuredHeight();
		setMeasuredDimension(imageSize, imageSize);
		if (drawingActivity != null)
			drawingActivity.loadDrawing();
	}

	public void addShapeToList(BaseFigure shape) {
		iDrawables.add(shape);
	}

	private void makeNewFigure() {
		switch (figureType) {
		case Line:
			activeFigure = new Line();
			break;
		case Circle:
			activeFigure = new Circle();
			break;
		case Shape:
			activeFigure = new Shape();
			break;
		case RotatedShape:
			activeFigure = new RotatedShape();
			break;
		case Multiline:
			activeFigure = new MultiLine(false);
			break;
		case MultiShape:
			activeFigure = new MultiLine(true);
			break;
		case Text:
			activeFigure = new Text();
			break;
		default:
			break;
		}

		iDrawables.add(activeFigure);
	}

	public void undo() {
		if (!(iDrawables.isEmpty())) {
			iDrawables.remove(iDrawables.size() - 1);
			invalidate();
			startNewFigure = true;
		}
	}

	public void setInputText(String inputText) {
		this.inputText = inputText;
	}

	public List<IDrawable> getiDrawables() {
		return iDrawables;
	}

	public void setDrawingActivity(DrawingActivity drawingActivity) {
		this.drawingActivity = drawingActivity;
	}

}
