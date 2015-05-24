package hr.tvz.taxizagreb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import junit.framework.TestCase;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
                    FragmentImenik.OnFragmentInteractionListener,
                    FragmentCijenaINavigacija.OnFragmentInteractionListener, FragmentPovijest.OnFragmentInteractionListener {

    TextView polaziste;
    TextView odrediste;

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



    public void clickBtnCallCammeo(View v)
    {
        call(getResources().getInteger(R.integer.broj_cammeo));
    }

    public void clickBtnInfoCammeo(View v)
    {
        Toast.makeText(this, "info cammeo",Toast.LENGTH_LONG).show();
    }

    public void clickBtnCallRadio(View v)
    {
        call(getResources().getInteger(R.integer.broj_radio));
    }

    public void clickBtnInfoRadio(View v)
    {
        Toast.makeText(this, "info radio",Toast.LENGTH_LONG).show();
    }

    public void clickBtnCallEko(View v)
    {
        call(getResources().getInteger(R.integer.broj_eko));
    }

    public void clickBtnInfoEko(View v)
    {
        Toast.makeText(this, "info eko",Toast.LENGTH_LONG).show();
    }

    public void clickBtnCallZebra(View v)
    {
        call(getResources().getInteger(R.integer.broj_zebra));
    }

    public void clickBtnInfoZebra(View v)
    {
        DialogFragment infoDialogFragment = new InfoDialog();
        infoDialogFragment.show(getFragmentManager(), "helpProzor");

      //  ((TextView)infoDialogFragment.getView().findViewById(R.id.txtDialogNaslov)).setText(R.string.naslov_zebra);
        //((TextView)findViewById(R.id.txtDialogNaslov)).setText(R.string.naslov_zebra);
    }

    public void clickBtnMap(View v)
    {
        Intent map = new Intent(this, GoogleMaps.class);
        startActivity(map);
    }

    public void clickBtnGPS(View view)
    {

    }

    public void clickBtnIzracunaj(View view)    {

        polaziste = (TextView)findViewById(R.id.txtAdresaPolazista);
        odrediste = (TextView)findViewById(R.id.txtAdresaOdredista);

  /*      SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd", Locale.getDefault());
        Date date = new Date(2015-05-21);
*/
        DbHelper db = new DbHelper(this);
        DbModel model = new DbModel(polaziste.getText().toString(), odrediste.getText().toString(), 5, "5h","Eko", 23.4);
        long flag = db.unosUBazu(model);
        if (flag < 0){
            Toast.makeText(this, "Neuspio unos", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Uneseno", Toast.LENGTH_SHORT).show();
        }
        polaziste.setText("");
        odrediste.setText("");
    }

    public void clickBtnPovijest(View view)
    {

    }

    public void call(int phone)
    {
        /** Stvaranje objekta aktivnosti koja pokrece ugradenu mogusnost zvanja ( ACTION_CALL ) */
        Intent intent = new Intent("android.intent.action.CALL");

        /** Stvaranje uri objekta u koji se sprema telefonski broj */
        Uri telBroj = Uri.parse("tel:" + phone );

        /** Postavljanje stvorenog uri objekta u intent */
        intent.setData(telBroj);

        /**Pokretanje aktivnosti za zvanje */
        startActivity(intent);
    }
}
