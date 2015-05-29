package hr.tvz.taxizagreb;

/**
 * Created by Dennis on 18.5.2015..
 */
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;


//klasa za ispis informacija o aplikaciji i timu, u obliku Dialoga

public class InfoDialog extends DialogFragment {

         TextView naslov;

         TextView info;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        return builder.create();
/*
        LayoutInflater inflater = getActivity().getLayoutInflater();

        //ucitaj dizajn za dialog
        builder.setView(inflater.inflate(R.layout.info_dialog, null))

                //postavi gumb za zatvaranje dialoga
                .setNegativeButton(R.string.dialogGumbZatvori, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InfoDialog.this.getDialog().cancel();
                    }
                });

        naslov = (TextView)getDialog().findViewById(R.id.txtDialogNaslov);
       // info = (TextView)getActivity().findViewById(R.id.txtDialogInfo);


        naslov.setText("Naslov dialoga");

        //prikazi dialog
        return builder.create();*/

    }

    public void setNaslovIOpis()
    {
        naslov.setText("Naslov dialoga");
        info.setText("Neki info sadrzaj");
    }

}
