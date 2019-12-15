package com.example.testlibrary;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import androidx.annotation.NonNull;

import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;

import java.util.ArrayList;
import java.util.List;


public class PinView extends SubsamplingScaleImageView implements OnTouchListener {

    private final Paint paint = new Paint();
    private final Paint textPaint = new Paint();
    private final PointF vPin = new PointF();
    private PointF start;
    private PointF destination;
    private Bitmap pin;

    private List<LocationInfo> route;
    private PointF[][] routes;

    private List<LocationInfo> markers;

    private List<Connection> addedConnections;

    //for ontouch

    private PointF vStart;
    private boolean drawing = false;
    private int strokeWidth;

    private List<PointF> sPoints;

    //used when adding location
    private PointF currentLocation;

    private PointF curLocInfo;
    private ArrayList<LocationInfo> selectedConnection;

    public PinView(Context context) {
        this(context, null);
        initialise();
    }

    public PinView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();

    }

    public void setPin(PointF start, PointF destination) {
        this.start = start;
        this.destination = destination;
        initialise();
        invalidate();
    }

    public void setCurrentLocation(PointF current){
        this.currentLocation = current;
        initialise();
        invalidate();
    }

    public void setRoute(List<LocationInfo> route){
        this.route = route;
        initialise();
        invalidate();
    }

    public void setRoutes(PointF[][] routes){
        this.routes = routes;
        initialise();
        invalidate();
    }

    public void setMarker(List<LocationInfo> locationList){
        markers = locationList;
    }

    public void setAddedConnections(List<Connection> connections){
        addedConnections = connections;
        initialise();
        invalidate();
    }

    public void setConnections(PointF current ,ArrayList<LocationInfo> selected){
        this.curLocInfo = current;
        this.selectedConnection = selected;
        initialise();
        invalidate();
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Don't draw pin before image is ready so it doesn't move around during setup.
        if (!isReady()) {
            return;
        }

        paint.setAntiAlias(true);

        //used to show only one route
        if (route != null && pin != null) {
            for (int i = 0; i< route.size()-1; i++) {
                sourceToViewCoord(route.get(i).getPoint(), vPin);
                float startx = vPin.x;
                float starty = vPin.y;
                sourceToViewCoord(route.get(i+1).getPoint(), vPin);
                float endx = vPin.x;
                float endy = vPin.y;
                canvas.drawLine(startx, starty, endx, endy, paint);
            }
            for (LocationInfo location: route){
                sourceToViewCoord(location.getPoint(), vPin);
                float vX = vPin.x;
                float vY = vPin.y;
                PointF start = getTagPoint(vX , vY);
                canvas.drawBitmap(pin, start.x, start.y, paint);
                PointF startLabel = getLabelPoint(vX, vY);
                String locationName = location.getName();
                if(route.indexOf(location) == 0){
                    locationName = "Current location";
                }
                canvas.drawText(locationName, startLabel.x, startLabel.y, textPaint);
            }
        }
//        use to stimulate the whole network
        if(routes != null && pin != null){
            for(PointF[] route : routes){
                for (int i = 0; i< route.length-1; i++) {
                    sourceToViewCoord(route[i], vPin);
                    float startx = vPin.x;
                    float starty = vPin.y;
                    sourceToViewCoord(route[i+1], vPin);
                    float endx = vPin.x;
                    float endy = vPin.y;
                    canvas.drawLine(startx, starty, endx, endy, paint);
                }
            }
        }

        //use to draw the destination and starting point
        if (start != null && pin != null && destination != null) {

            String curLocationName = "Current Location";
            sourceToViewCoord(start, vPin);
//            float vX = ((vPin.x/width)*image_width) - (pin.getWidth()/2);
//            float vY = ((vPin.y/height)*image_height) - pin.getHeight();
            float vX = vPin.x;
            float vY = vPin.y;
            PointF start = getTagPoint(vX , vY);
            canvas.drawBitmap(pin, start.x, start.y, paint);
            PointF startLabel = getLabelPoint(vX, vY);
            canvas.drawText(curLocationName, startLabel.x, startLabel.y, textPaint);

//            String desLocationName = "Destination";
//            destination = viewToSourceCoord(destination);
//            destination = sourceToViewCoord(destination);
//            PointF des = getTagPoint(vPin.x, vPin.y);
//            canvas.drawBitmap(pin, des.x, des.y, paint);
//            PointF desLabel = getLabelPoint(vPin.x, vPin.y);
//            canvas.drawText(desLocationName, desLabel.x, desLabel.y, textPaint);
        }

//          TODO this is used to generate the whole network of the map
//        if (vStart != null && pin != null ) {
//            // might need to use this to get the real coordinate
//            sourceToViewCoord(vStart);
//            System.out.println(vStart);
//            float vX = vStart.x - (pin.getWidth()/2);
//            float vY = vStart.y - pin.getHeight();

////            canvas.drawBitmap(pin, vX, vY, paint);
//            canvas.drawText("Hererere", vX, vY, textPaint);
//        }

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
        //Used this function to draw the current location
        if(currentLocation != null & pin != null){
            String curLocationName = "Current Location";
            sourceToViewCoord(currentLocation, vPin);
            float vX = vPin.x;
            float vY = vPin.y;
            PointF start = getTagPoint(vX , vY);
            canvas.drawBitmap(pin, start.x, start.y, paint);
            PointF startLabel = getLabelPoint(vX, vY);
            canvas.drawText(curLocationName, startLabel.x, startLabel.y, textPaint);
        }
        //Used to generate the connection
        if(curLocInfo != null & pin != null & selectedConnection != null){
            sourceToViewCoord(curLocInfo, vPin);
            for(LocationInfo connect : selectedConnection){
                PointF temp = sourceToViewCoord(connect.getPoint());
                canvas.drawLine(vPin.x, vPin.y, temp.x, temp.y, paint);
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
            x -= pin.getWidth();
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
