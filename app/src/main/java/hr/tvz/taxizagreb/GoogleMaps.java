package hr.tvz.taxizagreb;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;

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

    //lista tocaka na mapi
    ArrayList<LatLng> markerPoints;

    //varijable za spremanje koordinata polazista, odredista i json-a koji je odgovor Google Maps API-ja
    LatLng polLatLng;
    LatLng odrLatLng;
    String jsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //postavljanje mape na ekran
        setContentView(R.layout.activity_google_maps);

        //dohvacanje podataka koji su dodani uz activity pri instanciranju i pokretanju i spremanje u globalne varijable
        Bundle extra = this.getIntent().getExtras();
        polLatLng = new LatLng(extra.getDouble("polLat"), extra.getDouble("polLng"));
        odrLatLng = new LatLng(extra.getDouble("odrLat"), extra.getDouble("odrLng"));
        jsonString = extra.getString("jsonString");

        Log.i("LatLngPM", Double.toString(polLatLng.latitude));
        Log.i("LatLngPM", Double.toString(polLatLng.longitude));
        Log.i("LatLngOM", Double.toString(odrLatLng.latitude));
        Log.i("LatLngOM", Double.toString(odrLatLng.longitude));
        Log.i("LatLngString", jsonString);


        markerPoints = new ArrayList<LatLng>();

        // referenciranje na fragment od mape
        SupportMapFragment fm = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);

        // dohvacanje mape
        mMap = fm.getMap();

        // ako mapa postoji
        if(mMap!=null){

            // omogucavanje buttona za centriranje na lokaciju korisnika
            mMap.setMyLocationEnabled(true);

            for(int i = 0; i < 2; i++ ){
                //u prvom prolazu dodaj polaziste, u drogom odrediste
                LatLng latLng = polLatLng;
                if(i == 1) latLng = odrLatLng;

                markerPoints.add(latLng);

                // opcije za markere
                MarkerOptions options = new MarkerOptions();

                // postavljajnje pozicije markera
                options.position(latLng);

                // za polaziste postavi zeleni marker, za odrediste crveni
                if(i == 0){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                }else if(i == 1){
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }

                // dodavanje markera na mapu
                mMap.addMarker(options);

                // intanciranje  i pokretanje JsonParsera
                DoInBackground DoInBackground = new DoInBackground();
                DoInBackground.execute();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //ucitavanje global menua koji je cist i koji nema dodatne buttone po sebi jer su nepotrebni.
        getMenuInflater().inflate(R.menu.global, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // ako activity od mape nije prethodno unisten, po nastavku izvodenja provjeri da li je mapa intancirana i postavljena
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    // ako mapa nije instancirana, dohvati ju i postavi.
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
/*            // provjera da li je uspjesno dohvacena
            if (mMap != null) {
                setUpMap();
            }*/
        }
    }

    // klasa za izvodenje radnji u drugoj dretvi
    private class DoInBackground extends AsyncTask<Void, Void, String> {

        // Iscrtavanje u pozadinskoj dretvi
        @Override
        protected String doInBackground(Void... temp) {

            ParserTask parserTask = new ParserTask();
            parserTask.execute(jsonString);

            return "";
        }

        // nakon sto se ruta iscrtala, kameru postavi na srediste rute
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

    // klasa za parsiranje
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // parsiranje podataka u pozadinskoj dretvi
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                JSONParser parser = new JSONParser();

                // pocetak parsiranja
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            // vracanje liste s listama koordinata
            return routes;
        }

        // kad je parsiranje gotovo, iscrtavanje
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;


            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // dohvat 0-te, jedine rute
            List<HashMap<String, String>> path = result.get(0);

            // dohvat svih koordinata tocaka iz rute i spremanje u listu points
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // spremanje tocaka u lineOptions, dodavanje njihove sirine i boje
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.BLUE);


            // crtanje rute
            mMap.addPolyline(lineOptions);
        }
    }
}
