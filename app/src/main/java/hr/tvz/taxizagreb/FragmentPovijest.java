package hr.tvz.taxizagreb;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.wearable.DataEventBuffer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TooManyListenersException;

import hr.tvz.taxizagreb.dummy.DummyContent;

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

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

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

    // TODO: Rename and change types of parameters
    public static FragmentPovijest newInstance(String param1, String param2) {
        FragmentPovijest fragment = new FragmentPovijest();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FragmentPovijest() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DecimalFormat decFormat = new DecimalFormat("##0.00");

        DbHelper db = new DbHelper(getActivity());
        List<DbModel> td = db.ispisiSve();

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> item;
        for (DbModel t : td) {
            item = new HashMap<String, String>();
            item.put("polaziste_i_odrediste", t.getPolaziste() + " -> " + t.getOdrediste());
            item.put("prijevoznik_cijena_distanca", t.getPrijevoznik() + " - " + decFormat.format(t.getCijena()) + " kn - " + t.getDistanca());
            list.add(item);
        }

        //ako nema podataka u bazi ispisi da nema podataka
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
            SimpleAdapter adapter = new SimpleAdapter(getActivity(), list, android.R.layout.simple_list_item_2, new String[]{"polaziste_i_odrediste",
                    "prijevoznik_cijena_distanca"}, new int[]{android.R.id.text1,
                    android.R.id.text2});
            mAdapter = adapter;
            //  setListAdapter(adapter);
        }

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            Log.i("povijest", "odabran je " + position + " po redu");
           // mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);

            if(prazno){
                Log.i("povijest", "povijest je prazna");
                FragmentCijenaINavigacija navigacijaCijena = new FragmentCijenaINavigacija();
                getFragmentManager().beginTransaction().replace(R.id.container, navigacijaCijena, "navigacijaCijena").commit();
            }else {

                Log.i("povijest", "povijest nije prazna");
                FragmentCijenaINavigacija navigacijaCijena = new FragmentCijenaINavigacija();

                DbHelper db = new DbHelper(getActivity());
                List<String> podaciBaza = new ArrayList<String>();

                podaciBaza = db.getPoint(position);

                Bundle podaciBundle = new Bundle();
                podaciBundle.putString("tip", "povijest");
                podaciBundle.putString("polaziste", podaciBaza.get(0));
                podaciBundle.putString("odrediste", podaciBaza.get(1));

                navigacijaCijena.setArguments(podaciBundle);

                //  navigacijaCijena.setArguments(bundlePodaci);
                getFragmentManager().beginTransaction().replace(R.id.container, navigacijaCijena, "navigacijaCijena").commit();

/** neuspio pokusaj za mijenjanje selecta na itemu
                ((MainActivity) getActivity()).changeItemSelectedNavigationDrawer(2);
 */
            }
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }
}
