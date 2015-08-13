package hr.tvz.taxizagreb;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.DecimalFormat;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCijenaINavigacija.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCijenaINavigacija#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCijenaINavigacija extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private String odrediste;
    private String polaziste;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCijenaINavigacija.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCijenaINavigacija newInstance(String param1, String param2) {
        FragmentCijenaINavigacija fragment = new FragmentCijenaINavigacija();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentCijenaINavigacija() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // poziv metode za restoranje imena fragmenta u actionBaru. Rijesilo problem kada se klikom na stavku u povijesti i
        // prelaskom na fragment Cijena i Navigacija nije promijenilo ime fragmenta u actionBaru.
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.title_section2));
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cijena_inavigacija, container, false);

/*
        //postavljanje focus listenera na polja za unos
        EditText etOdr = (EditText) v.findViewById(R.id.txtAdresaOdredista);
        EditText etPol = (EditText) v.findViewById(R.id.txtAdresaPolazista);
        View.OnFocusChangeListener ofcListener = new MyFocusChangeListener();
        etPol.setOnFocusChangeListener(ofcListener);
        etOdr.setOnFocusChangeListener(ofcListener);
*/

        //ako sam primio podatke, postavi ih
        Log.i("podaci", "OnCreateView");
        Bundle bundlePodaci = getArguments();

        ((Button) v.findViewById(R.id.btn_cijena_map)).setClickable(false);
        ((ImageButton) v.findViewById(R.id.btn_cijena_cammeo_call)).setClickable(false);
        ((ImageButton) v.findViewById(R.id.btn_cijena_radio_call)).setClickable(false);
        ((ImageButton) v.findViewById(R.id.btn_cijena_eko_call)).setClickable(false);
        ((ImageButton) v.findViewById(R.id.btn_cijena_zebra_call)).setClickable(false);

        //ako je pokrenutaKarta 1, ne onemogucuje tipke za pozivanje i kartu, jer su podaci vec skinuti
        if(bundlePodaci != null) {
            if(bundlePodaci.getString("tip").equals("cijena")) {
                if (bundlePodaci.getBoolean("pokrenutaKarta")) {
                    ((Button) v.findViewById(R.id.btn_cijena_map)).setClickable(true);
                    ((ImageButton) v.findViewById(R.id.btn_cijena_cammeo_call)).setClickable(true);
                    ((ImageButton) v.findViewById(R.id.btn_cijena_radio_call)).setClickable(true);
                    ((ImageButton) v.findViewById(R.id.btn_cijena_eko_call)).setClickable(true);
                    ((ImageButton) v.findViewById(R.id.btn_cijena_zebra_call)).setClickable(true);

                    ((ImageButton) v.findViewById(R.id.btn_cijena_cammeo_call)).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_call));
                    ((ImageButton) v.findViewById(R.id.btn_cijena_eko_call)).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_call));
                    ((ImageButton) v.findViewById(R.id.btn_cijena_radio_call)).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_call));
                    ((ImageButton) v.findViewById(R.id.btn_cijena_zebra_call)).setImageDrawable(getResources().getDrawable(R.drawable.ic_action_call));
                }
            }
        }

        if(bundlePodaci != null) {
            if(bundlePodaci.getString("tip").equals("cijena")) {
                Log.i("podaci", "primio sam podatke, getArguments() nije null");
                String polTemp = bundlePodaci.getString("polaziste");
                String odrTemp = bundlePodaci.getString("odrediste");
                float cijenaCammeoTemp = bundlePodaci.getFloat("cijenaCammeo");
                float cijenaRadioTemp = bundlePodaci.getFloat("cijenaRadio");
                float cijenaEkoTemp = bundlePodaci.getFloat("cijenaEko");
                float cijenaZebraTemp = bundlePodaci.getFloat("cijenaZebra");
                String distancaTemp = bundlePodaci.getString("distanca");
                String vrijemeTemp = bundlePodaci.getString("vrijeme");

                DecimalFormat decFormat = new DecimalFormat("##0.00");

                ((EditText) v.findViewById(R.id.txtAdresaPolazista)).setText(polTemp);
                ((EditText) v.findViewById(R.id.txtAdresaOdredista)).setText(odrTemp);
                ((TextView) v.findViewById(R.id.txtUdaljenost)).setText(distancaTemp);
                ((TextView) v.findViewById(R.id.txtVrijemeVoznje)).setText(vrijemeTemp);
                ((TextView) v.findViewById(R.id.txtCammeoCijena)).setText(decFormat.format(cijenaCammeoTemp) + " kn");
                ((TextView) v.findViewById(R.id.txtRadioCijena)).setText(decFormat.format(cijenaRadioTemp) + " kn");
                ((TextView) v.findViewById(R.id.txtEkoCijena)).setText(decFormat.format(cijenaEkoTemp) + " kn");
                ((TextView) v.findViewById(R.id.txtZebraCijena)).setText(decFormat.format(cijenaZebraTemp) + " kn");

                ((EditText) v.findViewById(R.id.txtAdresaPolazista)).setEnabled(false);
                ((EditText) v.findViewById(R.id.txtAdresaOdredista)).setEnabled(false);
            }

            else if(bundlePodaci.getString("tip").equals("povijest")){
                String polTemp = bundlePodaci.getString("polaziste");
                String odrTemp = bundlePodaci.getString("odrediste");
                ((EditText) v.findViewById(R.id.txtAdresaPolazista)).setText(polTemp);
                ((EditText) v.findViewById(R.id.txtAdresaOdredista)).setText(odrTemp);
            }
        }
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        public void onFragmentInteraction(Uri uri);
    }
}
