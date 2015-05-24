package hr.tvz.taxizagreb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Dennis on 23.5.2015..
 */
public class DbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "PovijestVoznji";
    private static final String TABLE_NAME = "povijest";
    private static final String KEY_ID = "id";
    private static final String POLAZISTE = "polaziste";
    private static final String ODREDISTE = "odrediste";
    private static final String DISTANCA = "distanca";
    private static final String TRAJANJE_PUTOVANJA = "trajanjePutovanja";
    private static final String CIJENA = "cijena";
  //  private static final String DATUM = "datum";
    private static final String PRIJEVOZNIK = "prijevoznik";

    private static final int DATABASE_VERSION = 3;

    private static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            POLAZISTE+" VARCHAR(255),"+ODREDISTE+" VARCHAR(255),"+
            DISTANCA+" DECIMAL(5,2),"+TRAJANJE_PUTOVANJA+" VARCHAR(50),"+
            PRIJEVOZNIK+" VARCHAR(20),"+CIJENA+" DECIMAL(5,2)"+");";

    public DbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long unosUBazu(DbModel model){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(POLAZISTE, model.getPolaziste());
        values.put(ODREDISTE, model.getOdrediste());
        values.put(DISTANCA, model.getDistanca());
        values.put(TRAJANJE_PUTOVANJA, model.getTrajanjePutovanja());
        //values.put(DATUM, );
        values.put(PRIJEVOZNIK, model.getPrijevoznik());
        values.put(CIJENA, model.getCijena());

        long unosUBazu_id = db.insert(TABLE_NAME, null, values);
        return unosUBazu_id;
    }

    public List<DbModel> ispisiSve() {
        List<DbModel> povijest = new ArrayList<DbModel>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        Log.e("LOG", selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                DbModel td = new DbModel();
                td.setPolaziste(c.getString(c.getColumnIndex(POLAZISTE)));
                td.setOdrediste(c.getString(c.getColumnIndex(ODREDISTE)));
                td.setPrijevoznik(c.getString(c.getColumnIndex(PRIJEVOZNIK)));
                td.setTrajanjePutovanja(c.getString(c.getColumnIndex(TRAJANJE_PUTOVANJA)));
                td.setDistanca(c.getDouble(c.getColumnIndex(DISTANCA)));
                td.setCijena(c.getDouble(c.getColumnIndex(CIJENA)));
                // adding to DbModel list
                povijest.add(td);
            } while (c.moveToNext());
        }

        return povijest;
    }
}
