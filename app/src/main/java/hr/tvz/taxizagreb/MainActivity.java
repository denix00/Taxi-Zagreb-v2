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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

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
                    FragmentCijenaINavigacija.OnFragmentInteractionListener,
                    FragmentPovijest.OnFragmentInteractionListener {

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

    // za spremanje podatka da li je korisnik prethodno otvorio kartu ili ne
    static String zastavica = "";

    static double GPSLat = 0;
    static double GPSLng = 0;

    // instanciranje navigationDrawera
    private NavigationDrawerFragment mNavigationDrawerFragment;


    // spremanje posljednje koristenog naslova ekrana
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up drawera
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }


    @Override
    protected void onResume()
    {
        super.onResume();

        // Po povratku iz karte postavi vrijednosti koje su bile u TextViewovima u fragmentu CijenaINavigacija
        if(zastavica.equals("karta")){
            // pakiranej podataka u bundle pri povratku iz karte i promjena fragmenta na CijenaINavigacija
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
            Log.i("podaci", "podaci primljeni u OnResume" );

            // dodavanje bundla podataka fragmentu i promjena na njega
            FragmentCijenaINavigacija navigacijaCijena = new FragmentCijenaINavigacija();
            navigacijaCijena.setArguments(bundlePodaci);
            getFragmentManager().beginTransaction().replace(R.id.container, navigacijaCijena, "navigacijaCijena").commit();
        }
        // pocisti zastavicu
        zastavica = "";
    }


    // handleanje onoga sto je odabrano u izborniku (navigationdrawer)
    // prikaz odabranog ekrana/fragmenta
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

        switch (position)
        {
            case 0:
                // Toast.makeText(this, "odabran je: 1 ", Toast.LENGTH_SHORT).show();
                getFragmentManager().beginTransaction().replace(R.id.container, new FragmentImenik(), "imenik").commit();
                break;
            case 1:
               // Toast.makeText(this, "odabran je: 2 ", Toast.LENGTH_SHORT).show();
                getFragmentManager().beginTransaction().replace(R.id.container, new FragmentCijenaINavigacija()).commit();
                break;
            case 2:
            //    Toast.makeText(this, "odabran je: 3 ", Toast.LENGTH_SHORT).show();
                getFragmentManager().beginTransaction().replace(R.id.container, new FragmentPovijest()).commit();
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

    // postavljanje actionbara i njegovih postavki
    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    // ucitavanje dizajna actionbara, dok navigationdrawer nije otvoren
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    //za FragmentImenik i FragmentCijenaINavigacija
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    //za FragmentPovijest
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


    // ovisno koji je button pritisnut za info, za njega prikazi infoDialog
    public void clickBtnInfoHandler(View v){
        switch (v.getId()){
            case R.id.btn_imenik_cammeo_info:

                infoDialog(R.string.info_title_cammeo, R.string.info_nap_cammeo);
                break;
            case R.id.btn_imenik_radio_info:

                infoDialog(R.string.info_title_radio, R.string.info_nap_radio);
                break;
            case R.id.btn_imenik_eko_info:

                infoDialog(R.string.info_title_eko, R.string.info_nap_eko);
                break;
            case R.id.btn_imenik_zebra_info:

                infoDialog(R.string.info_title_zebra, R.string.info_nap_zebra);
                break;
        }
    }

    // nazovi, a ako je button s fragmenta CijenaINavigacija, tada i spremi u bazu
    public void clickBtnCallCammeo(View v)    {
        if(v.getId() == R.id.btn_cijena_cammeo_call) {
            spremanjeUBazu(polazisteGl, odredisteGl, distancaGl, vrijemeGl, "Cammeo Taxi", (double) cijenaCammeo);
        }
        call(getResources().getString(R.string.broj_cammeo));

    }

    // nazovi, a ako je button s fragmenta CijenaINavigacija, tada i spremi u bazu
    public void clickBtnCallRadio(View v)    {
        if(v.getId() == R.id.btn_cijena_radio_call) {
            spremanjeUBazu(polazisteGl, odredisteGl, distancaGl, vrijemeGl, "Radio Taxi", (double) cijenaRadio);
        }
        call(getResources().getString(R.string.broj_radio));
    }

    // nazovi, a ako je button s fragmenta CijenaINavigacija, tada i spremi u bazu
    public void clickBtnCallEko(View v)    {
        if(v.getId() == R.id.btn_cijena_eko_call) {
            spremanjeUBazu(polazisteGl, odredisteGl, distancaGl, vrijemeGl, "Eko Taxi", (double) cijenaEko);
        }
        call(getResources().getString(R.string.broj_eko));
    }

    // nazovi, a ako je button s fragmenta CijenaINavigacija, tada i spremi u bazu
    public void clickBtnCallZebra(View v)    {
        if(v.getId() == R.id.btn_cijena_zebra_call) {
            spremanjeUBazu(polazisteGl, odredisteGl, distancaGl, vrijemeGl, "Zebra Taxi", (double) cijenaZebra);
        }
        call(getResources().getString(R.string.broj_zebra));
    }


    // handler za pritisak na button karta, postavi zastavicu da se zna da je aplikacija otvorila
    // kartu, uz activity dodaj i podatke o polazistu, odredistu i json koji je odgovor API-ja
    // pokreni activity
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

    // s LocationManagerom utvrdi lokaciju korisnika nakon klika na gumb
    public void clickBtnGPS(View view)    {

        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                // postavljanje obavijesti o utvrdenoj lokaciji
                ((EditText) findViewById(R.id.txtAdresaPolazista)).setText("Lokacija utvrđena");
                ((EditText) findViewById(R.id.txtAdresaPolazista)).setEnabled(false);
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

        // utvrdivanje lokacije prema lokaciji korisnika u mrezi
        manager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, listener, null);

        // utvrdivanje lokacije pomocu GPS-a
      //  manager.requestSingleUpdate(LocationManager.GPS_PROVIDER, listener, null);

        Toast.makeText(this, "Molimo pričekajte da GPS utvrdi lokaciju", Toast.LENGTH_LONG).show();
    }


    public void clickBtnIzracunaj(View view) throws IOException, JSONException {

        // provjera ako je Internet dostupan, ako nije baci toast
        if (isNetworkAvailable()) {
            EditText txtPolaziste = (EditText) findViewById(R.id.txtAdresaPolazista);
            EditText txtOdrediste = (EditText) findViewById(R.id.txtAdresaOdredista);

            // ako tocke nisu unesene baci toast
            if(txtPolaziste.getText().toString().isEmpty() || txtOdrediste.getText().toString().isEmpty())
            {
                Toast.makeText(this, R.string.prazno_polje, Toast.LENGTH_SHORT).show();
                return;
            }

            String polazisteString = getResources().getString(R.string.polaziste_string);
            //string s podcrtima = string bez podcrta

            // ako polaziste nije utvrdeno pomocu GPS-a, proslijedi ga metodi checkStreetName
            // odrediste samo proslijedi
            if( GPSLat == 0) {
                polazisteString = checkStreetName(txtPolaziste);
            }
            String odredisteString = checkStreetName(txtOdrediste);

            // vrijednosti stavi u globalne varijable
            polazisteGl = txtPolaziste.getText().toString();
            odredisteGl = txtOdrediste.getText().toString();

            Log.i("polasisteGL", polazisteGl);
            Log.i("polasisteGL", odredisteGl);

            // filter pomocu kojega se ulica smjesta u pojedini grad
            String gradFilter = ",_zagreb";
            // String koji ce sadrzavat API upit za Google Maps API
            String url = "";

            // ako je polaziste utvrdno GPS-om, njegove koordinate stavi u string
            if(GPSLat != 0){
                url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + GPSLat + ","
                        + GPSLng + "&destination=" + odredisteString + gradFilter + "&mode=driving&sensor=false";
            }else {
                url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + polazisteString
                        + gradFilter + "&destination=" + odredisteString + gradFilter + "&mode=driving&sensor=false";
            }
            Log.i("JSONUrl ", url);

            // proslijedivanje upita downloadTask klasi
            downloadTask = new DownloadTask();
            downloadTask.execute(url);

        }else{
            Toast.makeText(this, R.string.dostupnost_veze, Toast.LENGTH_LONG).show();
        }
    }

    // ocisti sva polja i podesi stanje na pocetno
    public void clickReset(View v){

        resetTextViews(false);
        enableEnterTextviews(true);
        enableCallButtons(false);
        GPSLat = 0;
        GPSLng = 0;
    }

    // alert dialog s naslovom, porukom i gumbom za zatvaranje ("OK")
    public void infoDialog(int naslov, int poruka){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(poruka));
        alertDialogBuilder.setTitle(getResources().getString(naslov));
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    /**
     * metoda za zvanje
     * @param phone telefonski broj kao String
     */
    public void call(String phone)    {
        // Stvaranje objekta aktivnosti koja pokrece ugradenu mogucnost zvanja ( ACTION_CALL )
        Intent intent = new Intent("android.intent.action.CALL");

        // Stvaranje uri objekta u koji se sprema telefonski broj s prefiksom "tel"
        Uri telBroj = Uri.parse("tel:" + phone );

        // Postavljanje stvorenog uri objekta u intent
        intent.setData(telBroj);

        // Pokretanje aktivnosti za zvanje
        startActivity(intent);
    }

    /**
     * provjera stanja mreznih adaptera i povezanosti
     * @return vraca 1 ako su adapteri ukljuceni i povezani
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * zamjena " " s "_" u nazivu ulice, sva velika slova u mala
     * @param ulicaTxt EditText koji sadrzi tekst za provjeru
     * @return vracen obraden String
     */
    public String checkStreetName(EditText ulicaTxt)    {

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

    /**
     * Spremanje podataka u bazu koji ce posluziti za prikaz povijesti voznji
     */
    public void spremanjeUBazu(String polaziste, String odrediste, String distanca, String vrijeme, String prijevoznik, Double cijena) {
        DbHelper db = new DbHelper(this);
        //                                                  distanca, vrijeme, prijevoznik, cijena
        DbModel model = new DbModel(polaziste, odrediste, distanca, vrijeme, prijevoznik, cijena);
        long flag = db.unosUBazu(model);
        if (flag < 0) {
            Toast.makeText(this, R.string.neuspio_unos, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * izracun cijene voznje za sve taxi prijevoznike na temelju duzine rute
     * postavljanje izracunatog iznosa u textview zaduzen za to
     *
     * @param kilometara duzina rute
     */
    public void izracunajCijenu(float kilometara){

        // format u kojem ce se prikazivati decimalan broj - dva decimalna mjesta
        DecimalFormat decFormat = new DecimalFormat("##0.00");


        // citanje cijene iz stringa i parsiranje kao float podatak

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


    /**
     * Metoda za dohvacanje JSON podataka s URL-a
     * @param strUrl URL kao string
     * @return JSON string
     * @throws IOException
     */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // htpp veza za komuniciranje s API-jem
            urlConnection = (HttpURLConnection) url.openConnection();

            // povezivanje na API
            urlConnection.connect();

            // citanje podataka s API-ja
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            // spremanje podataka iz buffera u string
            data = sb.toString();
            br.close();

        }catch(Exception e){
            Log.d("Exception", "Exception while downloading url");
        }finally{
            // zatvaranje veza
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    // Klasa za preuzimanje podataka u pozadinskoj dretvi
    private class DownloadTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {

            // postavljanje dialoga za cekanje
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

        //
        @Override
        protected String doInBackground(String... url) {

            // skidanje podataka i vracanje istih kao string
            String data = "";

            try{
                data = downloadUrl(url[0]);
                publishProgress(1);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // nakon sto se izvrsi metoda doInBackground, parsiraj podatke
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

    /**
     * parsiranje JSON-a, dohvacanje onog sto je potrebno iz njega
     * @param url JSON podaci (odgovor API-ja)
     * @throws IOException
     * @throws JSONException
     */
    private void JSONParserSimple (String url) throws IOException, JSONException
    {
        final JSONObject json = new JSONObject(url);

        Log.i("statusDohvata", json.getString("status"));

        //ako je API odgovorio s OK, znaci da je uspio pronaci rutu (nazivi ulica su ispravni)
        if(json.getString("status").equals("OK")) {

            // spustanje razinu po razinu u JSON-u i dohvacanje krajnih podataka koji su potrebni
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);

            JSONArray newTempARr = routes.getJSONArray("legs");
            JSONObject newDisTimeOb = newTempARr.getJSONObject(0);

            JSONObject distOb = newDisTimeOb.getJSONObject("distance");
            JSONObject timeOb = newDisTimeOb.getJSONObject("duration");

            //ukoliko se koristi GPS, tada postavi naziv ulice na kojoj se nalazim
            if(GPSLat != 0) {
                String myLocation = newDisTimeOb.getString("start_address");

                //izbacivanje viska podataka iz naziva ulice
                int location = 1;
                for(int i=0; i<myLocation.length(); i++){
                    if(myLocation.charAt(i) == ',') {
                     location = i;
                    break;
                    }
                }
                myLocation = myLocation.substring(0, location);

                // postavljanje polazista koje je utvrdio GPS u polje za unos i u globalnu varijablu
                ((EditText) findViewById(R.id.txtAdresaPolazista)).setText(myLocation);
                polazisteGl = myLocation;
            }

            /** latlng*/

            // parsiranje koordinata polazista i odredista i spremanje u globalne varijable
            JSONObject polOb = newDisTimeOb.getJSONObject("start_location");
            JSONObject odrOb = newDisTimeOb.getJSONObject("end_location");

            polLatLng = new LatLng(polOb.getDouble("lat"), polOb.getDouble("lng"));
            odrLatLng = new LatLng(odrOb.getDouble("lat"), odrOb.getDouble("lng"));

            Log.i("LatLngP", Double.toString(polOb.getDouble("lat")));
            Log.i("LatLngP", Double.toString(polOb.getDouble("lng")));
            Log.i("LatLngO", Double.toString(odrOb.getDouble("lat")));
            Log.i("LatLngO", Double.toString(odrOb.getDouble("lng")));

            Log.i("Distance :", distOb.getString("text"));
            Log.i("TimeDi :", timeOb.getString("text"));

            // postavljanje vrijednosti u TextView za udaljenost i vrijeme
            ((TextView) findViewById(R.id.txtUdaljenost)).setText(distOb.getString("text"));
            ((TextView) findViewById(R.id.txtVrijemeVoznje)).setText(timeOb.getString("text"));

            // postavljanje vrijednosti u globalne varijable za spremanje u bazu
            distancaGl = distOb.getString("text");
            vrijemeGl = timeOb.getString("text");


            // omogucavanje call buttona na ekranu CijenaINavigacija, micanje Gray efekta
            enableCallButtons(true);

            // sakrivanje tipkovnice
            hideSoftKeyboard();

            // omoguci button za kartu
            ((Button) findViewById(R.id.btn_cijena_map)).setClickable(true);

            // izrezi brojku za distancu iz stringa, pretvori ga u decimalan tip i proslijedi
            // metodi za izracun cijena
            float distanca = Float.parseFloat(distancaGl.substring(0, distancaGl.indexOf(" ")));
            izracunajCijenu(distanca);

            //zakljucaj polja za unos
            enableEnterTextviews(false);


        }else{
            //ciscenje polja i ispis da adresa nije pronadena
            resetTextViews(true);
        }
    }


    /** Metoda za sakrivanje tipkovnice */
    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
    }

    // ciscenje svih polja
    //ako je argument true, tada adresa nije pronadena i ta poruka se dodaje, inace se sve postavlja na prazno
    public void resetTextViews(Boolean addressNotFound){
        //ako adresa nije pronadena, ne cisti polja da se unos moze editirati
        if(! addressNotFound){
            ((EditText)findViewById(R.id.txtAdresaOdredista)).setText("");
            ((EditText)findViewById(R.id.txtAdresaPolazista)).setText("");
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

    // zakljucavanje/otkljucavanje polja za unos
    public void enableEnterTextviews(Boolean enable){
        ((EditText)findViewById(R.id.txtAdresaPolazista)).setEnabled(enable);
        ((EditText)findViewById(R.id.txtAdresaOdredista)).setEnabled(enable);
    }

    // zakljuvavanje/otkljucavanje tipki za zvanje na CijenaINavigacija
    // ako je argument true, makni sivi efekt s njih
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


    //restoranje imena fragmenta u actionbaru
    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }
}

