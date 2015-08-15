package hr.tvz.taxizagreb;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class FragmentPovijest extends Fragment implements AbsListView.OnItemClickListener {

    private boolean prazno = false;
/*
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
*/
    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;
/*
    // TODO: Rename and change types of parameters
    public static FragmentPovijest newInstance(String param1, String param2) {
        FragmentPovijest fragment = new FragmentPovijest();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
*/
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentPovijest() {
    }

    /**
     * Popunjavanje liste s podacima (li s napomenom da nema podataka u bazi, odnosno povijesti) kod kreiranja fragmenta.
     * @param savedInstanceState Bundle s podacima od prethodnog izvodenja fragmenta
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //postavljanje formata za decimalne brojeve radi citljivijeg prikaza korisniku
        DecimalFormat decFormat = new DecimalFormat("##0.00");

        //instanciranje DbHelpera i dohvacanje svih redaka iz baze kao lista
        DbHelper dbHelper = new DbHelper(getActivity());
        List<DbModel> tableData = dbHelper.ispisiSve();

        //prolazak kroz sve podatke iz liste koji su u obliku DbModela i spremanje u listu za prikaz korisniku
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> item;
        for (DbModel t : tableData) {
            item = new HashMap<String, String>();
            item.put("polaziste_i_odrediste", t.getPolaziste() + " -> " + t.getOdrediste());
            item.put("prijevoznik_cijena_distanca", t.getPrijevoznik() + " - " + decFormat.format(t.getCijena()) + " kn - " + t.getDistanca());
            list.add(item);
        }

        //ako nema podataka u bazi, lista ce biti prazna, tada u listu za ispisivanje postavi obavijest da nema podataka i prikazi
        if(list.isEmpty())
        {
            item = new HashMap<String, String>();
            item.put("nema_podataka", getResources().getString(R.string.povijest_nema_podataka));
            item.put("prazno", "");
            list.add(item);

            SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, android.R.layout.simple_list_item_2, new String[] { "nema_podataka",
                    "prazno" }, new int[] { android.R.id.text1,
                    android.R.id.text2 });
            mAdapter = adapter;
            //ako je lista prazna, postavi zastavicu
            prazno = true;

        }else {
            //prikazi spremljene podatke iz liste za prikaz koja ima 2 reda
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, android.R.layout.simple_list_item_2, new String[]{"polaziste_i_odrediste",
                    "prijevoznik_cijena_distanca"}, new int[]{android.R.id.text1,
                    android.R.id.text2});
            //vrati adapter koji ce prikazati podatke
            mAdapter = adapter;
        }
/*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
*/
        // TODO: Change Adapter to display your content
        //mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);
        //mAdapter = adapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_povijest, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Metoda za reakciju na pritisak podatka iz prikazane liste te prebacivanje na fragment Navigacija i Cijena, i proslijedivanje podataka ukoliko ih ima
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            Log.i("povijest", "odabran je " + position + " po redu");
           // mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);

            //ako nema podataka u listi (zastavica je postavljena pri citanju podataka iz baze i prikazu na ekran kod kreiranja fragmenta)
            // korisnika samo prebaci na fragment Cijena i navigacija
            if(prazno){
                Log.i("povijest", "povijest je prazna");
                FragmentCijenaINavigacija navigacijaCijena = new FragmentCijenaINavigacija();
                getFragmentManager().beginTransaction().replace(R.id.container, navigacijaCijena, "navigacijaCijena").commit();

            //inace uz prebacivanje korisnika popuni polaziste i odrediste s podacima iz povijesti
            }else {

                Log.i("povijest", "povijest nije prazna");
                FragmentCijenaINavigacija navigacijaCijena = new FragmentCijenaINavigacija();

                DbHelper dbHelper = new DbHelper(getActivity());
                List<String> dataList = new ArrayList<String>();

                //dohvacanje polazista i odredista iz retka iz baze koji je identican retku koji je korisnik odabrao, spremanje u Bundle
                dataList = dbHelper.getStartPoints(position);

                Bundle dataBundle = new Bundle();
                dataBundle.putString("tip", "povijest");
                dataBundle.putString("polaziste", dataList.get(0));
                dataBundle.putString("odrediste", dataList.get(1));

                //fragmentu dodaj i podatke koje ce koristiti pri kreiranju
                navigacijaCijena.setArguments(dataBundle);

                getFragmentManager().beginTransaction().replace(R.id.container, navigacijaCijena, "navigacijaCijena").commit();
            }
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
/*    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }
*/
    /**
     *  interface kojeg moraju implementirati svi activityji koji koriste ovaj fragment
     * uloga mu je komunikacija izmedu fragemanta
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }
}
