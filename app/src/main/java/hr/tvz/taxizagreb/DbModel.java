package hr.tvz.taxizagreb;

import java.sql.Time;
import java.util.Date;

/**
 * Created by Dennis on 23.5.2015..
 *
 * Klasa sluzi kao model podataka koji ce se upisivati u bazu i citati iz nje.
 */
public class DbModel {

    String polaziste;
    String odrediste;
    String distanca;
    String trajanjePutovanja;
    double cijena;
    String prijevoznik;

    public DbModel(String polaziste, String odrediste, String distanca, String trajanjePutovanja, String prijevoznik, double cijena) {
        this.polaziste = polaziste;
        this.odrediste = odrediste;
        this.distanca = distanca;
        this.trajanjePutovanja = trajanjePutovanja;
        this.cijena = cijena;
        this.prijevoznik = prijevoznik;
    }

    public DbModel() {

    }

    public String getPolaziste() {
        return polaziste;
    }

    public void setPolaziste(String polaziste) {
        this.polaziste = polaziste;
    }


    public String getOdrediste() {
        return odrediste;
    }

    public void setOdrediste(String odrediste) {
        this.odrediste = odrediste;
    }


    public String getDistanca() {
        return distanca;
    }

    public void setDistanca(String distanca) {
        this.distanca = distanca;
    }


    public String getTrajanjePutovanja() {
        return trajanjePutovanja;
    }

    public void setTrajanjePutovanja(String trajanjePutovanja) {
        this.trajanjePutovanja = trajanjePutovanja;
    }


    public double getCijena() {
        return cijena;
    }

    public void setCijena(double cijena) {
        this.cijena = cijena;
    }


    public String getPrijevoznik() {
        return prijevoznik;
    }

    public void setPrijevoznik(String prijevoznik) {
        this.prijevoznik = prijevoznik;
    }
}
