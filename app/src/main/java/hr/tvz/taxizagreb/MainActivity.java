package hr.tvz.taxizagreb;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.renderscript.Allocation;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Locale;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                    FragmentImenik.OnFragmentInteractionListener,
                    FragmentCijenaINavigacija.OnFragmentInteractionListener, FragmentPovijest.OnFragmentInteractionListener {

    static String jsonString;

    ProgressDialog diag;

    DownloadTask downloadTask;

    static LatLng polLatLng;
    static LatLng odrLatLng;

    static float cijenaCammeo;
    static float cijenaEko;
    static float cijenaRadio;
    static float cijenaZebra;

    static String polazisteGl;
    static String odredisteGl;
    static String distancaGl;
    static String vrijemeGl;
    //android.support.v4.app.FragmentManager manager;


    static String zastavica = "";

    static double GPSLat = 0;
    static double GPSLng = 0;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        /**neuspio pokusaj
        if(savedInstanceState != null) {
            Log.i("SavedInstance", "nije null");
            if (savedInstanceState.getBoolean("zastavica")) {
                getFragmentManager().beginTransaction().replace(R.id.container, new FragmentCijenaINavigacija()).commit();
            }
        }
         */
       // manager = getSupportFragmentManager();
    }

/**Dobar pokusaj, radi*/
    @Override
    protected void onResume()
    {
        super.onResume();
        if(zastavica.equals("karta")){
            //dohvat spremljenih podataka
         //   Bundle bundlePodaci = getIntent().getExtras();
           // String odredisteTemp = getIntent().getExtras().getString("odrediste");
            Bundle bundlePodaci = new Bundle();
            bundlePodaci.putString("tip", "cijena");
            bundlePodaci.putString("polaziste", polazisteGl);
            bundlePodaci.putString("odrediste", odredisteGl);
            bundlePodaci.putFloat("cijenaCammeo", cijenaCammeo);
            bundlePodaci.putFloat("cijenaRadio", cijenaRadio);
            bundlePodaci.putFloat("cijenaEko", cijenaEko);
            bundlePodaci.putFloat("cijenaZebra", cijenaZebra);
            bundlePodaci.putString("distanca", distancaGl);
            bundlePodaci.putString("vrijeme", vrijemeGl);
            //kako bi buttoni za pozivanje ostali aktivirani
            bundlePodaci.putBoolean("pokrenutaKarta", true);
            Log.i("podaci", "podaci primljeni u OnResume" + bundlePodaci.getString("polaziste"));

            FragmentCijenaINavigacija navigacijaCijena = new FragmentCijenaINavigacija();
            navigacijaCijena.setArguments(bundlePodaci);
            getFragmentManager().beginTransaction().replace(R.id.container, navigacijaCijena, "navigacijaCijena").commit();
        }
        zastavica = "";
    }


    @Override
    protected void onStop() {
        super.onStop();;
        //spremanje podataka za popunjavanje fragmenta pri povratku u glavnu aplikaciju
      //  Bundle izlaz = new Bundle();
     //   izlaz.putString("polaziste", polazisteGl);
     //   izlaz.putString("odrediste", odredisteGl);
     //   getIntent().putExtras(izlaz);
     //   Log.i("podaci", "podaci spremljeni u OnStop");
    }
/** Pokusaj spremanja stanja aplikacije */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putBoolean("zastavica", true);
        Log.i("SavedInstance", "pokrenut");
    }
        /**
        if(! jsonString.isEmpty()) {
            savedInstanceState.putString("json", jsonString);
            savedInstanceState.putDouble("polLat", polLatLng.latitude);
            savedInstanceState.putDouble("polLng", polLatLng.longitude);
            savedInstanceState.putDouble("odrLat", odrLatLng.latitude);
            savedInstanceState.putDouble("odrLng", odrLatLng.longitude);
            savedInstanceState.putString("polaziste", polazisteGl);
            savedInstanceState.putString("odrediste", odredisteGl);
            savedInstanceState.putString("distanca", distancaGl);
            savedInstanceState.putString("vrijeme", vrijemeGl);
            savedInstanceState.putFloat("cijenaCammeo", cijenaCammeo);
            savedInstanceState.putFloat("cijenaRadio", cijenaRadio);
            savedInstanceState.putFloat("cijenaEko", cijenaEko);
            savedInstanceState.putFloat("cijenaZebra", cijenaZebra);
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        Log.i("SavedInstance", "Pokrenut restore");
        getFragmentManager().beginTransaction().replace(R.id.container, new FragmentCijenaINavigacija()).commit();
    }
        /**
        jsonString = savedInstanceState.getString("json");
        polLatLng = new LatLng(savedInstanceState.getDouble("polLat"), savedInstanceState.getDouble("polLng"));
        odrLatLng = new LatLng(savedInstanceState.getDouble("odrLat"), savedInstanceState.getDouble("odrLng"));
        polazisteGl = savedInstanceState.getString("polaziste");
        odredisteGl = savedInstanceState.getString("odrediste");
        distancaGl = savedInstanceState.getString("distanca");
        vrijemeGl = savedInstanceState.getString("vrijeme");
        cijenaCammeo = savedInstanceState.getFloat("cijenaCammeo");
        cijenaRadio = savedInstanceState.getFloat("cijenaRadio");
        cijenaEko = savedInstanceState.getFloat("cijenaEko");
        cijenaZebra = savedInstanceState.getFloat("cijenaZebra");


        ((TextView)findViewById(R.id.txtAdresaPolazista)).setText(polazisteGl);
        ((TextView)findViewById(R.id.txtAdresaOdredista)).setText(odredisteGl);
        ((TextView)findViewById(R.id.txtCammeoCijena)).setText(String.valueOf(cijenaCammeo) );
        ((TextView)findViewById(R.id.txtRadioCijena)).setText(String.valueOf(cijenaRadio));
        ((TextView)findViewById(R.id.txtEkoCijena)).setText(String.valueOf(cijenaEko));
        ((TextView)findViewById(R.id.txtZebraCijena)).setText(String.valueOf(cijenaZebra));
        ((TextView)findViewById(R.id.txtVrijemeVoznje)).setText(String.valueOf(vrijemeGl));
        ((TextView)findViewById(R.id.txtUdaljenost)).setText(String.valueOf(distancaGl));

    }
*/
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

        switch (position)
        {
            case 0:
             //   FragmentImenik imenik = new FragmentImenik();
              //  android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
              //  transaction.add(R.id.container, imenik, "imenik");
              //  transaction.commit();

                getFragmentManager().beginTransaction().replace(R.id.container, new FragmentImenik(), "imenik").commit();

            //    getFragmentManager().beginTransaction().replace(R.id.container, new FragmentImenik()).addToBackStack("imenik").commit();


               // Toast.makeText(this, "odabran je: 1 ", Toast.LENGTH_SHORT).show();
                break;
            case 1:
               // Toast.makeText(this, "odabran je: 2 ", Toast.LENGTH_SHORT).show();
                getFragmentManager().beginTransaction().replace(R.id.container, new FragmentCijenaINavigacija()).commit();

            //    getFragmentManager().beginTransaction().replace(R.id.container, new FragmentCijenaINavigacija()).addToBackStack("imenik").commit();

                break;
            case 2:
            //    Toast.makeText(this, "odabran je: 3 ", Toast.LENGTH_SHORT).show();

                getFragmentManager().beginTransaction().replace(R.id.container, new FragmentPovijest()).commit();
            //    getFragmentManager().beginTransaction().replace(R.id.container, new FragmentPovijest()).addToBackStack("imenik").commit();

                break;
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //FragmentImenik i FragmentCijenaINavigacija
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //FragmentPovijest
    @Override
    public void onFragmentInteraction(String id) {

    }


    /**Disabling menu button*/
    @Override
    public boolean onMenuOpened(final int featureId, final Menu menu) {
        super.onMenuOpened(featureId, menu);
        return false;
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }



    public void clickBtnCallCammeo(View v)    {
        if(v.getId() == R.id.btn_cijena_cammeo_call) {
            spremanjeUBazu(polazisteGl, odredisteGl, distancaGl, vrijemeGl, "Cammeo Taxi", (double) cijenaCammeo);
        }
        call(getResources().getString(R.string.broj_cammeo));

    }

    public void clickBtnInfoCammeo(View v)    {
     //   Toast.makeText(this, "info cammeo",Toast.LENGTH_LONG).show();
        infoDialog(R.string.info_title_cammeo, R.string.info_nap_cammeo);
    }

    public void clickBtnCallRadio(View v)    {
        if(v.getId() == R.id.btn_cijena_radio_call) {
            spremanjeUBazu(polazisteGl, odredisteGl, distancaGl, vrijemeGl, "Radio Taxi", (double) cijenaRadio);
        }
        call(getResources().getString(R.string.broj_radio));
    }

    public void clickBtnInfoRadio(View v)    {
    //    Toast.makeText(this, "info radio",Toast.LENGTH_LONG).show();
        infoDialog(R.string.info_title_radio, R.string.info_nap_radio);
    }

    public void clickBtnCallEko(View v)    {
        if(v.getId() == R.id.btn_cijena_eko_call) {
            spremanjeUBazu(polazisteGl, odredisteGl, distancaGl, vrijemeGl, "Eko Taxi", (double) cijenaEko);
        }
        call(getResources().getString(R.string.broj_eko));
    }

    public void clickBtnInfoEko(View v)    {
     //   Toast.makeText(this, "info eko",Toast.LENGTH_LONG).show();
        infoDialog(R.string.info_title_eko, R.string.info_nap_eko);
    }

    public void clickBtnCallZebra(View v)    {
        if(v.getId() == R.id.btn_cijena_zebra_call) {
            spremanjeUBazu(polazisteGl, odredisteGl, distancaGl, vrijemeGl, "Zebra Taxi", (double) cijenaZebra);
        }
        call(getResources().getString(R.string.broj_zebra));
    }

    public void clickBtnInfoZebra(View v)    {
       infoDialog(R.string.info_title_zebra, R.string.info_nap_zebra);
    }

    public void clickBtnMap(View v)    {
        zastavica = "karta";
        Intent map = new Intent(this, GoogleMaps.class);
        map.putExtra("polLat",polLatLng.latitude);
        map.putExtra("polLng",polLatLng.longitude);
        map.putExtra("odrLat",odrLatLng.latitude);
        map.putExtra("odrLng",odrLatLng.longitude);
        map.putExtra("jsonString", jsonString);
        startActivity(map);
    }

    public void clickBtnGPS(View view)    {

        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                ((TextView) findViewById(R.id.txtAdresaPolazista)).setText("Moja lokacija");
                ((TextView) findViewById(R.id.txtAdresaPolazista)).setEnabled(false);
            //    Toast.makeText(this, "GPS lokacija utvrđena", Toast.LENGTH_LONG).show();

                GPSLat = location.getLatitude();
                GPSLng = location.getLongitude();
                Log.i("gps", String.valueOf(location.getLatitude()));
                Log.i("gps", String.valueOf(location.getLongitude()));
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {


            }
        };
       // manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 20, listener);
        manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null);
       // manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null);


        Toast.makeText(this, "Molimo pričekajte da GPS utvrdi lokaciju", Toast.LENGTH_LONG).show();
    }

    public void clickBtnIzracunaj(View view) throws IOException, JSONException {

        if (isNetworkAvailable()) {
            TextView txtPolaziste = (TextView) findViewById(R.id.txtAdresaPolazista);
            TextView txtOdrediste = (TextView) findViewById(R.id.txtAdresaOdredista);

            if(txtPolaziste.getText().toString().isEmpty() || txtOdrediste.getText().toString().isEmpty())
            {
                Toast.makeText(this, R.string.prazno_polje, Toast.LENGTH_SHORT).show();
                return;
            }

            String polazisteString = "Moja lokacija";
            //string s podcrtima = string bez podcrta
            if( GPSLat == 0) {
                polazisteString = checkStreetName(txtPolaziste);
            }
            String odredisteString = checkStreetName(txtOdrediste);

            polazisteGl = txtPolaziste.getText().toString();
            odredisteGl = txtOdrediste.getText().toString();

            Log.i("polasisteGL", polazisteGl);
            Log.i("polasisteGL", odredisteGl);
/**
 *          Ovdje ide sav kod za dohvacanje podataka sa Google Maps API V2, pa se sprema u bazu
 */

            String gradFilter = ",_zagreb";
            String url = "";
          //  String url = "http://maps.googleapis.com/maps/api/directions/json?origin=jarun_24_zagreb&destination=maksimirska_cesta_128&sensor=false";
            if(GPSLat != 0){
                url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + GPSLat + "," + GPSLng + "&destination=" + odredisteString + gradFilter + "&mode=driving&sensor=false";
            }else {
                url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + polazisteString + gradFilter + "&destination=" + odredisteString + gradFilter + "&mode=driving&sensor=false";
            }
            Log.i("JSONUrl ", url);

            downloadTask = new DownloadTask();
            downloadTask.execute(url);

        }else{
            Toast.makeText(this, R.string.dostupnost_veze, Toast.LENGTH_LONG).show();
        }
    }

  /*      SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date(2015-05-21);



*/


    public void clickReset(View v){

        resetTextViews(false);
        enableEnterTextviews(true);
        enableCallButtons(false);
        GPSLat = 0;
        GPSLng = 0;
    }

    public void infoDialog(int naslov, int poruka){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setMessage(getResources().getString(poruka));
        alertDialog.setTitle(getResources().getString(naslov));
        alertDialog.show();
    }

    public void call(String phone)    {
        /** Stvaranje objekta aktivnosti koja pokrece ugradenu mogusnost zvanja ( ACTION_CALL ) */
        Intent intent = new Intent("android.intent.action.CALL");

        /** Stvaranje uri objekta u koji se sprema telefonski broj */
        Uri telBroj = Uri.parse("tel:" + phone );

        /** Postavljanje stvorenog uri objekta u intent */
        intent.setData(telBroj);

        /**Pokretanje aktivnosti za zvanje */
        startActivity(intent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String checkStreetName(TextView ulicaTxt)    {

        String ulica = ulicaTxt.getText().toString();
        ulica = ulica.toLowerCase(Locale.getDefault());

        // izbaceno da ne pise ,zagreb u polju za unos
    /**    if( ! ulica.contains("zagreb"))        {
            ulica = ulica.concat(", zagreb");
        }

        ulicaTxt.setText(ulica);
*/
        for(int i=0; i<ulica.length(); i++){
            String temp = ulica;
            if(ulica.charAt(i) == ' ') {
                temp = ulica.substring(0,i) + '_' + ulica.substring(i+1);
            }
            ulica = temp;
        }
        return ulica;
    }

    public void spremanjeUBazu(String polaziste, String odrediste, String distanca, String vrijeme, String prijevoznik, Double cijena) {
        DbHelper db = new DbHelper(this);
        //                                                  distanca, vrijeme, prijevoznik, cijena
        DbModel model = new DbModel(polaziste, odrediste, distanca, vrijeme, prijevoznik, cijena);
        long flag = db.unosUBazu(model);
        if (flag < 0) {
            Toast.makeText(this, R.string.neuspio_unos, Toast.LENGTH_SHORT).show();
        }
    }

    public void izracunajCijenu(float kilometara){
        DecimalFormat decFormat = new DecimalFormat("##0.00");

        Log.i("CijenaCam",getResources().getString(R.string.cammeo_start));
        Log.i("CijenaCam2",Float.toString(Float.parseFloat(getResources().getString(R.string.cammeo_start))));

        cijenaCammeo = 0;
        cijenaCammeo += Float.parseFloat(getResources().getString(R.string.cammeo_start));
        if(kilometara <= 2)                {
            ((TextView)findViewById(R.id.txtCammeoCijena)).setText(decFormat.format(cijenaCammeo) + " kn");
        }else{
            cijenaCammeo += (kilometara - 2) * Float.parseFloat(getResources().getString(R.string.cammeo_ostalikm));
            ((TextView)findViewById(R.id.txtCammeoCijena)).setText(decFormat.format(cijenaCammeo) + " kn");
        }

        cijenaRadio = 0;
        cijenaRadio += Float.parseFloat(getResources().getString(R.string.radio_start));
        cijenaRadio += kilometara * Float.parseFloat(getResources().getString(R.string.radio_ostalikm));
        ((TextView)findViewById(R.id.txtRadioCijena)).setText(decFormat.format(cijenaRadio) + " kn");


        cijenaEko = 0;
        cijenaEko += Float.parseFloat(getResources().getString(R.string.eko_start));
        cijenaEko += kilometara * Float.parseFloat(getResources().getString(R.string.eko_ostalikm));
        ((TextView)findViewById(R.id.txtEkoCijena)).setText(decFormat.format(cijenaEko) + " kn");

        cijenaZebra = 0;
        cijenaZebra += Float.parseFloat(getResources().getString(R.string.zebra_start));
        cijenaZebra += kilometara * Float.parseFloat(getResources().getString(R.string.zebra_ostalikm));
        ((TextView)findViewById(R.id.txtZebraCijena)).setText(decFormat.format(cijenaZebra) + " kn");

    }



    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception", "Exception while downloading url");
        }finally{
     //       iStream.close();
     //       urlConnection.disconnect();
        }
        return data;
    }


    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            diag = new ProgressDialog(MainActivity.this);
            diag.setMessage(getResources().getString(R.string.dohvat_podataka));
            diag.setIndeterminate(false);
            diag.setCancelable(true);
            diag.setOnCancelListener(new DialogInterface.OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {
                    downloadTask.cancel(false);
                }
            });
            diag.show();
            super.onPreExecute();
        };

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                publishProgress(1);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            jsonString = result;
            try {
                JSONParserSimple(jsonString);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            diag.dismiss();
        }
    }

    private void JSONParserSimple (String url) throws IOException, JSONException
    {
        final JSONObject json = new JSONObject(url);

        Log.i("statusDohvata", json.getString("status"));

        if(json.getString("status").equals("OK")) {
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);

            JSONArray newTempARr = routes.getJSONArray("legs");
            JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

            JSONObject distOb = newDisTimeOb.getJSONObject("distance");
            JSONObject timeOb = newDisTimeOb.getJSONObject("duration");

            //ukoliko se koristi GPS, tada postavi naziv ulice na kojoj se nalazim
            if(GPSLat != 0) {
                String myLocation = newDisTimeOb.getString("start_address");
                String tempString;
                //izbacivanje viska podataka iz naziva ulice
                int location = 1;
                for(int i=0; i<myLocation.length(); i++){
                    if(myLocation.charAt(i) == ',') {
                     location = i;
                    break;
                    }
                }
                myLocation = myLocation.substring(0, location);

                ((TextView) findViewById(R.id.txtAdresaPolazista)).setText(myLocation);
                polazisteGl = myLocation;
            }

            /**za latlng*/

            JSONObject polOb = newDisTimeOb.getJSONObject("start_location");
            JSONObject odrOb = newDisTimeOb.getJSONObject("end_location");

            polLatLng = new LatLng(polOb.getDouble("lat"), polOb.getDouble("lng"));
            odrLatLng = new LatLng(odrOb.getDouble("lat"), odrOb.getDouble("lng"));

            Log.i("LatLngP", Double.toString(polOb.getDouble("lat")));
            Log.i("LatLngP", Double.toString(polOb.getDouble("lng")));
            Log.i("LatLngO", Double.toString(odrOb.getDouble("lat")));
            Log.i("LatLngO", Double.toString(odrOb.getDouble("lng")));

            /**za latlng*/

            Log.i("Distance :", distOb.getString("text"));
            Log.i("TimeDi :", timeOb.getString("text"));

            //Postavljanje vrijednosti u TextView za udaljenost i vrijeme
            ((TextView) findViewById(R.id.txtUdaljenost)).setText(distOb.getString("text"));
            ((TextView) findViewById(R.id.txtVrijemeVoznje)).setText(timeOb.getString("text"));

            //postavljanje vrijednosti u globalne varijable za spremanje u bazu
            distancaGl = distOb.getString("text");
            vrijemeGl = timeOb.getString("text");


            /**Omogucavanje call buttona na ekranu Cijena i navigacija, micanje Gray efekta*/
            enableCallButtons(true);

            hideSoftKeyboard();

            ((Button) findViewById(R.id.btn_cijena_map)).setClickable(true);

            float distanca = Float.parseFloat(distancaGl.substring(0, distancaGl.indexOf(" ")));
            izracunajCijenu(distanca);


            //zakljucaj polja za unos
            enableEnterTextviews(false);


        }else{
            //ciscenje polja i ispis da adresa nije pronadena
            resetTextViews(true);
        }
    }


    /**Metoda za sakrivanje tipkovnice*/
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    //ako je argument 1, tada adresa nije pronadena i ta poruka se dodaje, inace sve se postavlja na prazno
    public void resetTextViews(Boolean addressNotFound){
        //ako adresa nije pronadena, ne cisti polja da se unos moze editirati
        if(! addressNotFound){
            ((TextView)findViewById(R.id.txtAdresaOdredista)).setText("");
            ((TextView)findViewById(R.id.txtAdresaPolazista)).setText("");
        }

        ((TextView)findViewById(R.id.txtUdaljenost)).setText("");
        ((TextView)findViewById(R.id.txtVrijemeVoznje)).setText("");
        ((TextView)findViewById(R.id.txtCammeoCijena)).setText("");
        ((TextView)findViewById(R.id.txtRadioCijena)).setText("");
        ((TextView)findViewById(R.id.txtEkoCijena)).setText("");
        ((TextView)findViewById(R.id.txtZebraCijena)).setText("");

        if(addressNotFound)
            ((TextView)findViewById(R.id.txtUdaljenost)).setText(R.string.nepostojece_adrese);
    }

    public void enableEnterTextviews(Boolean enable){
        ((TextView)findViewById(R.id.txtAdresaPolazista)).setEnabled(enable);
        ((TextView)findViewById(R.id.txtAdresaOdredista)).setEnabled(enable);
    }

    public void enableCallButtons(Boolean enable){
        ((ImageButton) findViewById(R.id.btn_cijena_cammeo_call)).setClickable(enable);
        ((ImageButton) findViewById(R.id.btn_cijena_eko_call)).setClickable(enable);
        ((ImageButton) findViewById(R.id.btn_cijena_radio_call)).setClickable(enable);
        ((ImageButton) findViewById(R.id.btn_cijena_zebra_call)).setClickable(enable);
        ((Button) findViewById(R.id.btn_cijena_map)).setClickable(enable);

        if(enable) {
            ((ImageButton) findViewById(R.id.btn_cijena_cammeo_call)).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_call));
            ((ImageButton) findViewById(R.id.btn_cijena_eko_call)).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_call));
            ((ImageButton) findViewById(R.id.btn_cijena_radio_call)).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_call));
            ((ImageButton) findViewById(R.id.btn_cijena_zebra_call)).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_call));
        }else{
            ((ImageButton) findViewById(R.id.btn_cijena_cammeo_call)).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_call_gray));
            ((ImageButton) findViewById(R.id.btn_cijena_eko_call)).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_call_gray));
            ((ImageButton) findViewById(R.id.btn_cijena_radio_call)).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_call_gray));
            ((ImageButton) findViewById(R.id.btn_cijena_zebra_call)).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_call_gray));
        }
    }
}
