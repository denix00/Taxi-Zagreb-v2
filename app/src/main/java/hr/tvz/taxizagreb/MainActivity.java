package hr.tvz.taxizagreb;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                    FragmentImenik.OnFragmentInteractionListener,
                    FragmentCijenaINavigacija.OnFragmentInteractionListener, FragmentPovijest.OnFragmentInteractionListener {

    static String jsonString;

    ProgressDialog diag;

    DownloadTask downloadTask;

    String polazisteGl;
    String odredisteGl;
    String distancaGl;
    String vrijemeGl;
    Double cijenaGl;
    //android.support.v4.app.FragmentManager manager;

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

       // manager = getSupportFragmentManager();
    }

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

                getFragmentManager().beginTransaction()
                        .add(R.id.container, new FragmentImenik(), "imenik").commit();


               // Toast.makeText(this, "odabran je: 1 ", Toast.LENGTH_SHORT).show();
                break;
            case 1:
               // Toast.makeText(this, "odabran je: 2 ", Toast.LENGTH_SHORT).show();
                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new FragmentCijenaINavigacija()).commit();
                break;
            case 2:
            //    Toast.makeText(this, "odabran je: 3 ", Toast.LENGTH_SHORT).show();

                getFragmentManager().beginTransaction()
                        .replace(R.id.container, new FragmentPovijest()).commit();
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
        spremanjeUBazu(polazisteGl, odredisteGl, distancaGl, vrijemeGl, "Cammeo Taxi", 20.5);
        call(getResources().getInteger(R.integer.broj_cammeo));

    }

    public void clickBtnInfoCammeo(View v)    {
        Toast.makeText(this, "info cammeo",Toast.LENGTH_LONG).show();
    }

    public void clickBtnCallRadio(View v)    {
        spremanjeUBazu(polazisteGl, odredisteGl, distancaGl, vrijemeGl, "Radio Taxi", 20.5);
        call(getResources().getInteger(R.integer.broj_radio));
    }

    public void clickBtnInfoRadio(View v)    {
        Toast.makeText(this, "info radio",Toast.LENGTH_LONG).show();
    }

    public void clickBtnCallEko(View v)    {
        spremanjeUBazu(polazisteGl, odredisteGl,distancaGl, vrijemeGl, "Eko Taxi", 20.5);
        call(getResources().getInteger(R.integer.broj_eko));
    }

    public void clickBtnInfoEko(View v)    {
        Toast.makeText(this, "info eko",Toast.LENGTH_LONG).show();
    }

    public void clickBtnCallZebra(View v)    {
        spremanjeUBazu(polazisteGl, odredisteGl,distancaGl, vrijemeGl, "Zebra Taxi", 20.5);
        call(getResources().getInteger(R.integer.broj_zebra));
    }

    public void clickBtnInfoZebra(View v)    {
        DialogFragment infoDialogFragment = new InfoDialog();
        infoDialogFragment.show(getFragmentManager(), "helpProzor");

      //  ((TextView)infoDialogFragment.getView().findViewById(R.id.txtDialogNaslov)).setText(R.string.naslov_zebra);
        //((TextView)findViewById(R.id.txtDialogNaslov)).setText(R.string.naslov_zebra);
    }

    public void clickBtnMap(View v)    {
        Intent map = new Intent(this, GoogleMaps.class);
        startActivity(map);
    }

    public void clickBtnGPS(View view)    {

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

            //string s podcrtima = string bez podcrta
            String polazisteString = checkStreetName(txtPolaziste);
            String odredisteString = checkStreetName(txtOdrediste);

            polazisteGl = txtPolaziste.getText().toString();
            odredisteGl = txtOdrediste.getText().toString();
/**
 *          Ovdje ide sav kod za dohvacanje podataka sa Google Maps API V2, pa se sprema u bazu
 */
          //  String url = "http://maps.googleapis.com/maps/api/directions/json?origin=jarun_24_zagreb&destination=maksimirska_cesta_128&sensor=false";
            String url = "http://maps.googleapis.com/maps/api/directions/json?origin="+polazisteString+"&destination="+odredisteString+"&mode=driving&sensor=false";

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

    public void clickBtnPovijest(View view)    {

    }

    public void call(int phone)    {
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

        if( ! ulica.contains("zagreb"))        {
            ulica = ulica.concat(", zagreb");
        }

        ulicaTxt.setText(ulica);

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
        } else {
            //Toast.makeText(this, "Uneseno", Toast.LENGTH_SHORT).show();
        }
    }

    public void izracunajCijenu(float kilometara){
        float cijena = 0;
        DecimalFormat decFormat = new DecimalFormat("##0.00");

        Log.i("CijenaCam",getResources().getString(R.string.cammeo_start));
        Log.i("CijenaCam2",Float.toString(Float.parseFloat(getResources().getString(R.string.cammeo_start))));

        cijena += Float.parseFloat(getResources().getString(R.string.cammeo_start));
        if(kilometara <= 2)                {
            ((TextView)findViewById(R.id.txtCammeoCijena)).setText(decFormat.format(cijena) + " kn");
        }else{
            cijena += (kilometara - 2) * Float.parseFloat(getResources().getString(R.string.cammeo_ostalikm));
            ((TextView)findViewById(R.id.txtCammeoCijena)).setText(decFormat.format(cijena) + " kn");
        }

        cijena = 0;
        cijena += Float.parseFloat(getResources().getString(R.string.radio_start));
        cijena += kilometara * Float.parseFloat(getResources().getString(R.string.radio_ostalikm));
        ((TextView)findViewById(R.id.txtRadioCijena)).setText(decFormat.format(cijena) + " kn");


        cijena = 0;
        cijena += Float.parseFloat(getResources().getString(R.string.eko_start));
        cijena += kilometara * Float.parseFloat(getResources().getString(R.string.eko_ostalikm));
        ((TextView)findViewById(R.id.txtEkoCijena)).setText(decFormat.format(cijena) + " kn");

        cijena = 0;
        cijena += Float.parseFloat(getResources().getString(R.string.zebra_start));
        cijena += kilometara * Float.parseFloat(getResources().getString(R.string.zebra_ostalikm));
        ((TextView)findViewById(R.id.txtZebraCijena)).setText(decFormat.format(cijena) + " kn");

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

            Log.i("Distance :", distOb.getString("text"));
            Log.i("TimeDi :", timeOb.getString("text"));

            //Postavljanje vrijednosti u TextView za udaljenost i vrijeme
            ((TextView) findViewById(R.id.txtUdaljenost)).setText(distOb.getString("text"));
            ((TextView) findViewById(R.id.txtVrijemeVoznje)).setText(timeOb.getString("text"));

            //postavljanje vrijednosti u globalne varijable za spremanje u bazu
            distancaGl = distOb.getString("text");
            vrijemeGl = timeOb.getString("text");

            ((ImageButton) findViewById(R.id.btn_cijena_cammeo_call)).setClickable(true);
            ((ImageButton) findViewById(R.id.btn_cijena_eko_call)).setClickable(true);
            ((ImageButton) findViewById(R.id.btn_cijena_radio_call)).setClickable(true);
            ((ImageButton) findViewById(R.id.btn_cijena_zebra_call)).setClickable(true);

            ((Button) findViewById(R.id.btn_cijena_map)).setClickable(true);

            float distanca = Float.parseFloat(distancaGl.substring(0, distancaGl.indexOf(" ")));
            izracunajCijenu(distanca);
        }else{
            ((TextView)findViewById(R.id.txtUdaljenost)).setText(R.string.nepostojece_adrese);
            ((TextView)findViewById(R.id.txtVrijemeVoznje)).setText("");
            ((TextView)findViewById(R.id.txtCammeoCijena)).setText("");
            ((TextView)findViewById(R.id.txtRadioCijena)).setText("");
            ((TextView)findViewById(R.id.txtEkoCijena)).setText("");
            ((TextView)findViewById(R.id.txtZebraCijena)).setText("");
        }
    }
}
