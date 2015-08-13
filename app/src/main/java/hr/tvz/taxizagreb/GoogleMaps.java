package hr.tvz.taxizagreb;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GoogleMaps extends ActionBarActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ArrayList<LatLng> markerPoints;

    LatLng polLatLng;
    LatLng odrLatLng;
    String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps);
      //  setUpMapIfNeeded();

        Bundle extra = this.getIntent().getExtras();
        polLatLng = new LatLng(extra.getDouble("polLat"), extra.getDouble("polLng"));
        odrLatLng = new LatLng(extra.getDouble("odrLat"), extra.getDouble("odrLng"));
        jsonString = extra.getString("jsonString");

        Log.i("LatLngPM", Double.toString(polLatLng.latitude));
        Log.i("LatLngPM", Double.toString(polLatLng.longitude));
        Log.i("LatLngOM", Double.toString(odrLatLng.latitude));
        Log.i("LatLngOM", Double.toString(odrLatLng.longitude));
        Log.i("LatLngString", jsonString);

        // Initializing
        markerPoints = new ArrayList<LatLng>();

        // Getting reference to SupportMapFragment of the activity_main
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting Map for the SupportMapFragment
        mMap = fm.getMap();

        if(mMap!=null){

            // Enable MyLocation Button in the Map
            mMap.setMyLocationEnabled(true);

            for(int i = 0; i < 2; i++ ){
            // Setting onclick event listener for the map

                // Adding new item to the ArrayList
                //u prvom prolazu koristi polaziste, u drogom odrediste
                LatLng latLng = polLatLng;
                if(i == 1) latLng = odrLatLng;

                markerPoints.add(latLng);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(latLng);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if(i == 0){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }else if(i == 1){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                DownloadTask downloadTask = new DownloadTask();
                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Start downloading json data from Google Directions API
                downloadTask.execute();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //ucitavanje global menua koji je cist i koji nema dodatne button po sebi jer su nepotrebni.
        getMenuInflater().inflate(R.menu.global, menu);
        //onemogucavanje buttona za pomoc
      //  menu.findItem(R.id.action_help).setEnabled(false);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<Void, Void, String> {

        // Iscrtavanje u pozadinskoj dretvi
        @Override
        protected String doInBackground(Void... temp) {

            ParserTask parserTask = new ParserTask();
            parserTask.execute(jsonString);

            return "";
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            double centLat;
            double centLng;
            if(polLatLng.latitude > odrLatLng.latitude) centLat = (polLatLng.latitude - odrLatLng.latitude) / 2 + odrLatLng.latitude;
            else centLat = (odrLatLng.latitude - polLatLng.latitude) / 2 + polLatLng.latitude;

            if(polLatLng.longitude > odrLatLng.longitude) centLng = (polLatLng.longitude - odrLatLng.longitude) / 2 + odrLatLng.longitude;
            else centLng = (odrLatLng.longitude - polLatLng.longitude) / 2 + polLatLng.longitude;

            LatLng centLatLng = new LatLng(centLat,centLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(centLatLng));
            mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                JSONParser parser = new JSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.BLUE);
            }

            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }
}
