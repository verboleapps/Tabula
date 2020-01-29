package com.verbole.dcad.tabula;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.verbole.dcad.tabula.RoomDatabase.BaseLatinRepository;
import com.verbole.dcad.tabula.RoomDatabase.Baseentrees;

import java.util.ArrayList;
import java.util.List;

// ======= avec ROOM DB
public class FragmentDictViewModel2 extends AndroidViewModel {

    private BaseLatinRepository repository;
    private LiveData<List<Baseentrees>> allEntrees;
    private MutableLiveData<List<Baseentrees>> searchResults;


    MiseEnForme MeF;
    String defDictTemp = "";
    String flexionsTemp = "";
    String defBaseTemp = "";
    String htmlTemp = "";
    int compteThreads = 0;
    int taillePolice = 0;
    float largeurFenetre = 0;
    static final String TAG = "VueModelDict";




    public FragmentDictViewModel2(@NonNull Application application) {
        super(application);
        repository = new BaseLatinRepository(application);
        allEntrees = repository.getAllEntrees();
        searchResults = repository.getSearchResults();



//        MeF = new MiseEnForme(application);
    }

    private MutableLiveData<List<Baseentrees>> entrees;


    private MutableLiveData<String> searchText;
    private MutableLiveData<String> searchTextAnalyse; // les distinguer car sinon va lancer une analyse a chaque fois
    // que le searchtext est modifie
    private MutableLiveData<String> stringHtml;
    private MutableLiveData<String> stringHtmlRecherche;

    MutableLiveData<EnregDico> entreeCourante;


    public LiveData<List<Baseentrees>> getEntrees() {

        return allEntrees;
    }

    MutableLiveData<List<Baseentrees>> getSearchResults() {
        return searchResults;
    }

    public void getEntree(int position) {

        repository.findEntree(position);
    }

    private void loadEntrees() {
        if (searchText == null) {
            searchText = new MutableLiveData<String>();
            searchText.setValue("");
        }
        // Do an asynchronous operation to fetch users.


    }


    public void setEntreeCourante(EnregDico entree) {
        entreeCourante.setValue(entree);
    }
    public LiveData<EnregDico> getEntreeCourante() {
        if (entreeCourante == null) {
            entreeCourante = new MutableLiveData<EnregDico>();
        }
        return entreeCourante;
    }


    public LiveData<String> resultatHtml = Transformations.switchMap(getEntreeCourante(), entree -> {
        if (stringHtml == null) {
            stringHtml = new MutableLiveData<String>();
            stringHtml.setValue("");
        }

        return stringHtml;
    });


    public LiveData<String> getHtmlStringResult() {
        return resultatHtml;
    }


    public MutableLiveData<String> getSearchText() {
        if (searchText == null) {
            searchText = new MutableLiveData<String>();
            searchText.setValue("");
        }
        return searchText;
    }
    public void setSearchText(String texte) {
        searchText.setValue(texte);
    }

    public LiveData<String> getHtmlResultatRecherche() {
        return resultatHtmlRecherche;
    }

    public void setSearchTextAnalyse(String texte) {
        searchTextAnalyse.setValue(texte);
    }

    public MutableLiveData<String> getSearchTextAnalyse() {
        if (searchTextAnalyse == null) {
            searchTextAnalyse = new MutableLiveData<String>();
            searchTextAnalyse.setValue("");
        }
        return searchTextAnalyse;
    }

    public LiveData<String> resultatHtmlRecherche = Transformations.map(getSearchTextAnalyse(),leTexte -> {
//Log.d(TAG,"result rech");
        if (leTexte.isEmpty()) {
            return "";
        }
        else {
            ArrayList resultats = MeF.analyseForme(leTexte);
            String texte = MeF.rechercheHtml(leTexte,  resultats, largeurFenetre, taillePolice);
            return texte;
        }

    });





    private class DefinitionDictsTask extends AsyncTask<HtmlParams,Void,String> {

        @Override
        protected String doInBackground(HtmlParams... htmlParams) {
            Log.d("====","deb task dd" + compteThreads + " police : " + htmlParams[0].taillePolice);
            defDictTemp = "";// MeF.chargeDefDicts(htmlParams[0].enreg);
            String resF = ""; //MeF.chargeDef_et_Flexion_Entree(htmlParams[0].enreg,defBaseTemp,defDictTemp,flexionsTemp,htmlParams[0].largeurFen,htmlParams[0].taillePolice);

            return resF;
        }
        @Override
        protected void onPostExecute(String resultats) {
            compteThreads += 1;
            Log.d("====","fintaskdd" + compteThreads);

            if (compteThreads == 2) {
                stringHtml.setValue(resultats);
            }

            //stringHtml.setValue(resultats);
        }
    }

    private class FlexionTask extends AsyncTask<HtmlParams,Void,String> {

        @Override
        protected String doInBackground(HtmlParams... htmlParams) {
            //flexionsTemp = MeF.chargeFlexionEntree(htmlParams[0].enreg);
            //defBaseTemp = MeF.chargeDefBase(htmlParams[0].enreg);
            String resF = ""; //MeF.chargeDef_et_Flexion_Entree(htmlParams[0].enreg,defBaseTemp,defDictTemp,flexionsTemp,htmlParams[0].largeurFen,htmlParams[0].taillePolice);

            return resF;
        }
        @Override
        protected void onPostExecute(String resultats) {
            compteThreads += 1;
            //Log.d("====","fintaskfl " + compteThreads);

            if (compteThreads == 2) {
                stringHtml.setValue(resultats);
            }
        }
    }

    private class HtmlTask extends AsyncTask<HtmlParams,Void,String> {

        @Override
        protected String doInBackground(HtmlParams... htmlParams) {

           // defDictTemp = MeF.chargeDefDicts(htmlParams[0].enreg);
           // flexionsTemp = MeF.chargeFlexionEntree(htmlParams[0].enreg);
           // defBaseTemp = MeF.chargeDefBase(htmlParams[0].enreg);
            String resF = ""; //MeF.chargeDef_et_Flexion_Entree(htmlParams[0].enreg,defBaseTemp,defDictTemp,flexionsTemp,htmlParams[0].largeurFen,htmlParams[0].taillePolice);

            return resF;
        }
        @Override
        protected void onPostExecute(String resultats) {
            //  htmlTemp = resultats;
            stringHtml.setValue(resultats);
        }
    }


    private class HtmlParams {
        EnregDico enreg = new EnregDico();
        float largeurFen = 0;
        int taillePolice = 0;

    }

}
