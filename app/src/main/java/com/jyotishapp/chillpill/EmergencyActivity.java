package com.jyotishapp.chillpill;

import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.microsoft.azure.maps.mapcontrol.AzureMap;
import com.microsoft.azure.maps.mapcontrol.AzureMaps;
import com.microsoft.azure.maps.mapcontrol.MapControl;
import com.microsoft.azure.maps.mapcontrol.events.OnReady;
import com.microsoft.azure.maps.mapcontrol.layer.LineLayer;
import com.microsoft.azure.maps.mapcontrol.layer.SymbolLayer;
import com.microsoft.azure.maps.mapcontrol.source.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.microsoft.azure.maps.mapcontrol.options.CameraOptions.center;
import static com.microsoft.azure.maps.mapcontrol.options.CameraOptions.zoom;
import static com.microsoft.azure.maps.mapcontrol.options.LineLayerOptions.strokeColor;
import static com.microsoft.azure.maps.mapcontrol.options.LineLayerOptions.strokeWidth;
import static com.microsoft.azure.maps.mapcontrol.options.SymbolLayerOptions.iconImage;

public class EmergencyActivity extends AppCompatActivity {

    static ArrayList<Adapter> hospitals = new ArrayList<>();

    static {
        AzureMaps.setSubscriptionKey("IriRm8tu9TLUJIdNj1Wo126lzi9aKHcVk2TG24ABP4s ");
    }

    private RequestQueue mQueue ;
    static String urlGetHosp = "https://atlas.microsoft.com/search/poi/json?subscription-key=IriRm8tu9TLUJIdNj1Wo126lzi9aKHcVk2TG24ABP4s &api-version=1.0&query=hospital&limit=10&lat="+"&lon="+"&radius=10000" ;
    static String urlDistance = "https://atlas.microsoft.com/route/directions/json?subscription-key=IriRm8tu9TLUJIdNj1Wo126lzi9aKHcVk2TG24ABP4s &api-version=1.0&query="+"," + ":" + "," + "" ;
    private FusedLocationProviderClient fusedLocationClient;
    MapControl mapControl;
    static Location loc;
    int idx = 0 , min = Integer.MAX_VALUE ;
    String name ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        mapControl = findViewById(R.id.mapcontrol);

        mapControl.onCreate(savedInstanceState);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        // Got last known location. In some rare situations this can be null.
                        if (location != null ) {
                            loc = location;
                        }
                    }
                });


        //Wait until the map resources are ready.
        mapControl.onReady(map -> {
            //Add your post map load code here.
            map.setCamera(center(loc.getLatitude(), loc.getLongitude()), zoom(14
            ));

            //Create a data source and add it to the map.
            DataSource dataSource = new DataSource();
            map.sources.add(dataSource);

            DataSource dataSource1 = new DataSource();
            map.sources.add(dataSource1);


            urlGetHosp = "https://atlas.microsoft.com/search/poi/json?subscription-key=IriRm8tu9TLUJIdNj1Wo126lzi9aKHcVk2TG24ABP4s&api-version=1.0&query=hospital&limit=5&lat="
                    +loc.getLatitude()+"&lon="+loc.getLongitude()+"&radius=10000" ;

            mQueue = Volley.newRequestQueue(this) ;
            JsonObjectRequest request123 = new JsonObjectRequest(Request.Method.GET, urlGetHosp, null,
                    new Response.Listener<JSONObject>()
                    {
                        @Override
                        public void onResponse(JSONObject response)
                        {
                            try {
                                JSONObject object = response ;
                                JSONArray array = object.getJSONArray("results");
                                for (int i = 0; i < array.length(); i++)
                                {
                                    JSONObject obj = array.getJSONObject(i);
                                    JSONObject poi = obj.getJSONObject("poi");
                                    JSONObject coo = obj.getJSONObject("position");
                                    String name = poi.getString("name");
//                                    String phone = poi.getString("phone");
                                    hospitals.add(new Adapter(name , Point.fromLngLat( coo.getDouble("lon"),coo.getDouble("lat")) , "AA" ));
                                    for (int j = 0; j < hospitals.size(); j++)
                                    {
                                        String url = "https://atlas.microsoft.com/route/directions/json?subscription-key=IriRm8tu9TLUJIdNj1Wo126lzi9aKHcVk2TG24ABP4s&api-version=1.0&query="+ EmergencyActivity.loc.getLatitude() + "," + EmergencyActivity.loc.getLatitude() +
                                                ":" + hospitals.get(0).getLoc().latitude() + "," + hospitals.get(0).getLoc().longitude() ;
                                        int finalJ = j;
                                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response1)
                                                    {
                                                        try {
                                                            JSONArray jsonArray = response1.getJSONArray("routes");
                                                            JSONObject object1 = jsonArray.getJSONObject(0);
                                                            int tt = object1.getInt("travelTimeInSeconds") + object1.getInt("trafficDelayInSeconds");
                                                            if (tt < min )
                                                            {
                                                                min = tt ;
                                                                idx = finalJ;
                                                            }
                                                        } catch (JSONException e) {
                                                            e.printStackTrace();
                                                        }

                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {

                                            }
                                        }) ;

                                    }
                                }
                                Log.e("HOSPIS", min + " " + hospitals.get(idx).getName() );
                                Adapter fin = new Adapter(hospitals.get(0).getName(),hospitals.get(0).getLoc(),hospitals.get(0).getPhone());


                                dataSource.add(Feature.fromGeometry(Point.fromLngLat(loc.getLongitude(), loc.getLatitude())));
                                //Add a custom image icon to the map resources.
                                map.images.add("my-icon", R.drawable.mapcontrol_point_circle_blue_o);

                                //Create a symbol layer and add it to the map.


                                dataSource.add(Feature.fromGeometry(Point.fromLngLat(fin.loc.longitude(), fin.loc.latitude())));

                                map.images.add("icon" , R.drawable.mapcontrol_marker_red );

                                map.layers.add(new SymbolLayer(dataSource,
                                        iconImage("icon")));
                                map.layers.add(new SymbolLayer(dataSource,
                                        iconImage("my-icon")));

                                List<Point> points = Arrays.asList(
                                        Point.fromLngLat(loc.getLongitude(), loc.getLatitude()),
                                        Point.fromLngLat(fin.loc.longitude(), fin.loc.latitude()));

                                //Create a LineString feature and add it to the data source.
                                dataSource.add(LineString.fromLngLats(points));

                                //Create a line layer and add it to the map.
                                map.layers.add(new LineLayer(dataSource,
                                        strokeColor("blue"),
                                        strokeWidth(5f)));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            }) ;
            mQueue.add(request123);



        });
    }

    @Override
    public void onResume() {
        super.onResume();
        mapControl.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapControl.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapControl.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapControl.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapControl.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapControl.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapControl.onSaveInstanceState(outState);
    }


}
