package com.example.testlibrary;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;

import com.davemorrissey.labs.subscaleview.ImageSource;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

                //test for map and pin
        PinView imageView = (PinView)findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.map1));
        PointF start = new PointF(500, 1500);
        PointF destination = new PointF(2000, 500);
        PointF[] array = {start, new PointF(1500, 1500), destination};
        imageView.setPin(start, destination);
        imageView.setRoute(array);

        //TODO get all the points available in database
//        PointF centre = new PointF((float)525.9595, (float)787.95166);
//        PointF point = new PointF((float)125.9595, (float)787.95166);
//        PointF point_1 = new PointF((float)625.9595, (float)787.95166);
//        PointF point_2 = new PointF((float)525.9595, (float)287.95166);
//        PointF point_3 = new PointF((float)525.9595, (float)987.95166);
//        PointF[][] routes = {{centre, point}, {centre, point_1}, {centre, point_2}, {centre, point_3}};

//        imageView.setRoutes(routes);
        //TODO get all the vertex and calculate the distance
        //TODO create a matrix of the whole network
        //TODO put into dijkstra algo to get shortest path
        //TODO draw into the map

//        Dijsktra algo
        DijkstraAlgo dj = new DijkstraAlgo();
        float[][] adjacencyMatrix = { { 0, (float)0.2, 0, 0, 0, 0, 0, 8, 0 },
                { (float)0.11, 0, 8, 0, 0, 0, 0, 11, 0 },
                { 0, 8, 0, 7, 0, 4, 0, 0, 2 },
                { 0, 0, 7, 0, 9, 14, 0, 0, 0 },
                { 0, 0, 0, 9, 0, 10, 0, 0, 0 },
                { 0, 0, 4, 0, 10, 0, 2, 0, 0 },
                { 0, 0, 0, 14, 0, 2, 0, 1, 6 },
                { 8, 11, 0, 0, 0, 0, 1, 0, 7 },
                { 0, 0, 2, 0, 0, 0, 6, 7, 0 } };
        ArrayList<Integer> path = dj.dijkstra(adjacencyMatrix, 1);
        System.out.println(path);

        //create a function to check whether the route need to go up or down


    }

    public void goUpstair(View v){
        PinView imageView = (PinView)findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.map1));
        PointF start = new PointF(1500, 500);
        PointF destination = new PointF(500, 2000);
        PointF[] array = {start, new PointF(1500, 1500), destination};
        imageView.setPin(start, destination);
        imageView.setRoute(array);
    }

    public void goDownstair(View v){
        PinView imageView = (PinView)findViewById(R.id.imageView);
        imageView.setImage(ImageSource.resource(R.drawable.map1));
        PointF start = new PointF(1500, 500);
        PointF destination = new PointF(500, 2000);
        PointF[] array = {start, new PointF(1500, 1500), destination};
        imageView.setPin(start, destination);
        imageView.setRoute(array);
    }
    
    public void btn_gotoRating_page(View view) {
        startActivity(new Intent(getApplicationContext(),RatingActivity.class));
    }
}
