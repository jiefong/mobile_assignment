package com.example.testlibrary;

import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.List;
import java.util.Objects;


public class PinViewAddLocation extends SubsamplingScaleImageView implements OnTouchListener {

    private final Paint paint = new Paint();
    private final Paint textPaint = new Paint();
    private final PointF vPin = new PointF();
    private PointF start;
    private PointF hold;
    private PointF destination;
    private Bitmap pin;

    //use to draw all the added location
    private List<LocationInfo> markers;

    private List<Connection> addedConnections;

    private PointF[] route;
    private PointF[][] routes;

    //for ontouch
    private PointF vStart;
    private boolean drawing = false;
    private int strokeWidth;

    //images height n width
    private int image_width = 0;
    private int image_height = 0;

    private List<PointF> sPoints;

    public PinViewAddLocation(Context context) {
        this(context, null);
        initialise();
    }

    public PinViewAddLocation(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public PointF getPoint(){
        int width = this.getWidth();
        int height = this.getHeight();

        if(!(vStart == null)){
            float vX = ((vStart.x/width)*image_width);
            float vY = ((vStart.y/height)*image_height);
            hold = new PointF(vX, vY);
            return viewToSourceCoord(hold);
        }else{
            return null;
        }
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
//        paint.setStrokeWidth(strokeWidth * 2);
//        canvas.drawPath(vPath, paint);
//        paint.setStrokeWidth(strokeWidth);
        paint.setColor(Color.argb(255, 51, 181, 229));

        //for ontouch
        setOnTouchListener(this);
        strokeWidth = (int)(density/60f);

        //for text paint
        textPaint.setColor(Color.BLACK);
        textPaint.setStrokeWidth(5);
        textPaint.setTextSize(30);
    }

    public void setAddedConnections(List<Connection> connections){
        addedConnections = connections;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = this.getWidth();
        int height = this.getHeight();

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        paint.setAntiAlias(true);

//          TODO this is used to generate the whole network of the map
        if (vStart != null && pin != null ) {
            // might need to use this to get the real coordinate

            vStart = sourceToViewCoord(vStart);

            float vX = ((vStart.x/width)*image_width) - (pin.getWidth()/2);
            float vY = ((vStart.y/height)*image_height) - pin.getHeight();
            System.out.println(vStart);
            canvas.drawBitmap(pin, vX, vY, paint);
        }

        //use to draw the destination and starting point
        if (start != null && pin != null && destination != null) {

            String curLocationName = "Current Location";
            start = sourceToViewCoord(start);
            float vX = ((start.x/width)*image_width);
            float vY = ((start.y/height)*image_height);
            PointF point =getTagPoint(vX , vY);
            canvas.drawBitmap(pin, point.x, point.y, paint);
            PointF startLabel = getLabelPoint(vX, vY);
            canvas.drawText(curLocationName, startLabel.x, startLabel.y, textPaint);

        }

        //Used this function to generate the markers of the map
        if(markers != null){
            for (LocationInfo location : markers){
                String desLocationName = location.getName();
                PointF point = sourceToViewCoord(location.getPoint(), vPin);
                System.out.println(point);
                PointF des = getTagPoint(point.x, point.y);
                canvas.drawBitmap(pin, des.x, des.y, paint);
                PointF desLabel = getLabelPoint(vPin.x, vPin.y);
                canvas.drawText(desLocationName, desLabel.x, desLabel.y, textPaint);
            }
        }

        //Used to create all the connections added
        if(addedConnections != null){
            for(Connection connection: addedConnections){
                LocationInfo one = connection.getLocation_1();
                LocationInfo two = connection.getLocation_2();
                PointF pOne = sourceToViewCoord(one.getPoint());
                PointF pTwo = sourceToViewCoord(two.getPoint());

                canvas.drawLine(pOne.x, pOne.y, pTwo.x, pTwo.y, paint);
            }
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
                    hold = vStart;
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

    public void setPin(PointF start, PointF destination) {
        this.start = start;
        this.destination = destination;
        initialise();
        invalidate();
    }

    public void setImageResolution(int width, int height){
        image_width = width;
        image_height = height;
    }

    public void setMarker(List<LocationInfo> locationList){
        markers = locationList;
    }

    public static boolean isNull(Object obj) {
        return obj == null;
    }

}
