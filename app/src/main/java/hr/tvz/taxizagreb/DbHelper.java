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
 *
 * Klasa koja unosi podatke u bazu i cita iz nje prema modelu definiranom u DbModel.
 */
public class DbHelper extends SQLiteOpenHelper {

    //nazivi tablica i atributa
    private static final String DATABASE_NAME = "PovijestVoznji";
    private static final String TABLE_NAME = "povijest";
    private static final String KEY_ID = "id";
    private static final String POLAZISTE = "polaziste";
    private static final String ODREDISTE = "odrediste";
    private static final String DISTANCA = "distanca";
    private static final String TRAJANJE_PUTOVANJA = "trajanjePutovanja";
    private static final String CIJENA = "cijena";
    private static final String PRIJEVOZNIK = "prijevoznik";


    //ukoliko se napravi promjena u modelu baze, DATABASE_VERSION je potrebno povisiti kako
    // bi promjena bila prihvacena
    private static final int DATABASE_VERSION = 4;

    //upit za kreiranje tabliceu u koju ce se spremati povijest voznji
    private static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +"("+KEY_ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            POLAZISTE+" VARCHAR(255),"+ODREDISTE+" VARCHAR(255),"+
            DISTANCA+" VARCHAR(10),"+TRAJANJE_PUTOVANJA+" VARCHAR(20),"+
            PRIJEVOZNIK+" VARCHAR(20),"+CIJENA+" DECIMAL(6,2)"+");";

    /**
     * kontruktor
     * @param context
     */
    public DbHelper (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * kreiranje tablice pri instanciranju klase, ukoliko tablica postoji, nece se ponovo kreirati.
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE);
    }

    /**
     * metoda za nadogradnju baze, prvo se DROPa baza, potom pokrece onCreate metoda koja ce ju ponovo kreirati.
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /**
     * Unos u bazu pomocu modela baze
     * @param model objekt sadrzi podatke koji ce se spremiti u tablicu
     * @return id reda u koji je unesen podatak, -1 ako unos nije uspio
     */
    public long unosUBazu(DbModel model){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(POLAZISTE, model.getPolaziste());
        values.put(ODREDISTE, model.getOdrediste());
        values.put(DISTANCA, model.getDistanca());
        values.put(TRAJANJE_PUTOVANJA, model.getTrajanjePutovanja());
        values.put(PRIJEVOZNIK, model.getPrijevoznik());
        values.put(CIJENA, model.getCijena());

        long unosUBazu_id = db.insert(TABLE_NAME, null, values);
        return unosUBazu_id;
    }

    /**
     * Ispis svih redaka iz baze
     * @return lista objekata, svaki sadrzi podatke iz jednog retka
     */
    public List<DbModel> ispisiSve() {
        List<DbModel> povijest = new ArrayList<DbModel>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;

        Log.e("LOG", selectQuery);

        //dohvacanje baze i izvrsavanje upita koji ce vratiti N redaka i spremiti u kursor
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // pomocu kursora se prolazi redak po redak kroz sve retke, podaci se spremaju u listu
        if (c.moveToFirst()) {
            do {
                DbModel td = new DbModel();
                td.setPolaziste(c.getString(c.getColumnIndex(POLAZISTE)));
                td.setOdrediste(c.getString(c.getColumnIndex(ODREDISTE)));
                td.setPrijevoznik(c.getString(c.getColumnIndex(PRIJEVOZNIK)));
                td.setTrajanjePutovanja(c.getString(c.getColumnIndex(TRAJANJE_PUTOVANJA)));
                td.setDistanca(c.getString(c.getColumnIndex(DISTANCA)));
                td.setCijena(c.getDouble(c.getColumnIndex(CIJENA)));
                // dodavanje podataka u obliku objekta DbModela na kraj liste
                povijest.add(td);
            } while (c.moveToNext());
        }

        return povijest;
    }

    /**
     * Dohvat polazista i odredista na temelju rednog broja u bazi
     * @param location redak koji se zeli dohvatiti
     * @return lista s prvim podatkom koji je polaziste, a drugi je odrediste
     */
    public List<String> getStartPoints(int location){
        //
        DbModel dbModel = new DbModel();
        //lista stringova, na prvo mjesto ce ic polaziste, na drugo odrediste
        List<String> polOdr = new ArrayList<String>();
        // upit za bazu
        String selectQuery = "SELECT " + POLAZISTE + "," + ODREDISTE + " FROM " + TABLE_NAME + " LIMIT 1 OFFSET " + location + ";";
        Log.i("selectQuery", selectQuery);

        // dohvat dostupne baze
        SQLiteDatabase db = this.getReadableDatabase();
        // izvrsavanje upita i spremanje svih redaka u kursor
        Cursor c = db.rawQuery(selectQuery, null);
        // odabir prvog retka
        c.moveToFirst();
        // spremanje naziva u listu
        polOdr.add(c.getString(c.getColumnIndex(POLAZISTE)));
        polOdr.add(c.getString(c.getColumnIndex(ODREDISTE)));

        return polOdr;
    }
}
