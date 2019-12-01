package com.example.testlibrary;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import androidx.annotation.NonNull;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.List;


public class PinViewAddLocation extends SubsamplingScaleImageView implements OnTouchListener {

    private final Paint paint = new Paint();
    private final Paint textPaint = new Paint();
    private final PointF vPin = new PointF();
    private PointF start;
    private PointF destination;
    private Bitmap pin;

    private PointF[] route;
    private PointF[][] routes;

    //for ontouch
    private PointF vStart;
    private boolean drawing = false;
    private int strokeWidth;

    private List<PointF> sPoints;

    public PinViewAddLocation(Context context) {
        this(context, null);
        initialise();
    }

    public PinViewAddLocation(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();

    }

    public PointF getPoint(){
        return vStart;
    }

    private void initialise() {
        float density = getResources().getDisplayMetrics().densityDpi;
        pin = BitmapFactory.decodeResource(this.getResources(), R.drawable.pushblue_pin);
        float w = (density/420f) * pin.getWidth()/16;
        float h = (density/420f) * pin.getHeight()/16;
        pin = Bitmap.createScaledBitmap(pin, (int)w, (int)h, true);
        //set paint
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(5);

        //set paint
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth * 2);
//        canvas.drawPath(vPath, paint);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.argb(255, 51, 181, 229));

        //for ontouch
        setOnTouchListener(this);
        strokeWidth = (int)(density/60f);

        //for text paint
        textPaint.setColor(Color.BLACK);
        textPaint.setStrokeWidth(5);
        textPaint.setTextSize(30);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        paint.setAntiAlias(true);

//          TODO this is used to generate the whole network of the map
        if (vStart != null && pin != null ) {
            // might need to use this to get the real coordinate
            sourceToViewCoord(vStart);
            System.out.println(vStart);
            float vX = vStart.x - (pin.getWidth()/2);
            float vY = vStart.y - pin.getHeight();

            canvas.drawBitmap(pin, vX, vY, paint);
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onTouchEvent(@NonNull MotionEvent event) {
        if (sPoints != null && !drawing) {
            return super.onTouchEvent(event);
        }
        boolean consumed = false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getActionIndex() == 0) {
                    vStart = new PointF(event.getX(), event.getY());
                }
                invalidate();
                break;
        }
        // Use parent to handle pinch and two-finger pan.
        return consumed || super.onTouchEvent(event);
    }

    public void reset() {
        this.sPoints = null;
        invalidate();
    }

    //this method is used to return the Point to draw the location name
    public PointF getLabelPoint(float x , float y){
        float width = getSWidth()*getScale();

        if(x > width/2){
            x -= pin.getWidth()/2;
            y += pin.getHeight()/10;
        }else{
            x += pin.getWidth()/2;
            y -= pin.getHeight()/2;
        }
        return new PointF(x, y);
    }
    //this method is used to return the Point to draw the tag icon of the location
    public PointF getTagPoint(float x, float y){
        x -= (pin.getWidth()/2);
        y -= pin.getHeight();
        return new PointF(x, y);
    }

}
