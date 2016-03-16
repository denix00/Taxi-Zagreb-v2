package hr.tvz.taxizagreb;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragment koji predstavlja menu nazvan NavigationDrawer
 */
public class NavigationDrawerFragment extends Fragment {

    // Kljuc koji se koristi za pamcenje koji je item odabran
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    // Kljuc s kojim se sprema vrijednost da li je korisnik vidio menu dok je 1. put pokrenuo aplikaciju
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    // referenca na aktivnost
    private NavigationDrawerCallbacks mCallbacks;

    // ActionBarDrawerToggle sluzi za spajanje actionBara i NavigationDrawera
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // provjera da li je korisnik vec vidio menu ili nije
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        // dohvat odabira koji je bio kod zadnjeg prikaza menua
        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        // odabir pretpostavljenje stavke na poziciji 0 ili stavke koja je bila zadnje odabrana
        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ucitavanje dizajna i postavljanje listenera za odabir stavke iz menua
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectItem(position);
            }
        });

        // postavljanje izgleda i naziva stavki
        mDrawerListView.setAdapter(new ArrayAdapter<String>(
                getActionBar().getThemedContext(),
                android.R.layout.simple_list_item_activated_1,
                android.R.id.text1,
                new String[]{
                        getString(R.string.title_section1),
                        getString(R.string.title_section2),
                        getString(R.string.title_section3),
                }));
        mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

    // metoda za provjeru da li je menu otvoren
    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * metoda koju je potrebno pozvati kako bi se postavila interakcija s NavigationDrawerom
     * @param fragmentId   id od fragmenta iz layout datoteke
     * @param drawerLayout drawerLayout koji sadrzi korisnicko sucelje ovog fragmenta
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // postavljanje sjencanja koje prekrije ostatak ekrana dok se menu otvori
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        // postavljanje izgleda actionBara
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer);


        // ActionBarDrawerToggle povezuje interakciju koja se odvija izmedu NavigationDrawer ikone i
        // actionBar ikone.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* glavni activity */
                mDrawerLayout,                    /* DrawerLayout objekt */
                R.drawable.ic_drawer,             /* ikona koja ce predstavljati menu i zamijeniti ikonu za povratak na prethodni ekran */
                R.string.navigation_drawer_open,  /* opis dok je menu otvoren */
                R.string.navigation_drawer_close  /* opis dok je menu zatvoren */
        ) {
            // dok je menu zatvoren
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu();
            }

            //dok je menu otvoren
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // dok korisnik otvori menu, spremi zastavicu da se menu vise ne otvara automatski
                    // po pokretanju aplikacije
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu();
            }
        };

        // ako korisnik jos nije otvorio menu, otvori ga da korisnik zna da postoji
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        // listener za menu gumb
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    /**
     * metoda za selektiranje stavke u NavigationDraweru
     * @param position pozicija itema, 0 je prvi
     */
    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    // spremanje stanja kod pauziranja ili unistavanja
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    // promjena konfiguracije
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    // kod otvaranja NavigationDrawera
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // ako je menu otvoren, ucitaj global dizajn za actionBar
        // pozovi showGlobalContextActionBar() metodu koja ce promijeniti naslov
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    // odabir tipke iz actionBara
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // odabran je gumb za pomoc, prikazi alertDialog s pomoci i gumbom za zatvaranje
        if (item.getItemId() == R.id.action_help) {

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
            alertDialogBuilder.setMessage(Html.fromHtml(getResources().getString(R.string.help_dialog_poruka)));
            alertDialogBuilder.setTitle(getResources().getString(R.string.action_help));
            alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * metoda koja se poziva dok je menu otvoren - prikaz naziva aplikacije, a ne trenutnog ekrana
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    // dohvat actionBara
    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * interface kojeg moraju implementirati svi activityji koji koriste ovaj fragment
     * uloga mu je komunikacija izmedu fragemanta
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * poziva se kad je odabrana stavka iz menua
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
