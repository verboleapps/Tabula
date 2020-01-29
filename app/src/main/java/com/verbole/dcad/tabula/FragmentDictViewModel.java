package com.verbole.dcad.tabula;

import android.app.Application;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import java.util.ArrayList;
import java.util.List;

public class FragmentDictViewModel extends AndroidViewModel  {

    MiseEnForme MeF;

    int taillePolice = 0;
    float largeurFenetre = 0;
    static final String TAG = "FragDictViewModel";

    private MutableLiveData<EnregDico> entreeCourante;
    private MutableLiveData<ResultatFTS> entreeCouranteFTS;

    private MutableLiveData<String> searchText;
    private MutableLiveData<String> searchTextFTS;
    private MutableLiveData<String> searchTextAnalyse;

    private MutableLiveData<String> stringHtml;
    private MutableLiveData<String> stringFTSHtml;
    private MutableLiveData<String> stringHtmlRecherche;

    public FragmentDictViewModel(@NonNull Application application) {
        super(application);
        MeF = new MiseEnForme(application);
        Log.d(TAG,"Fragm Dict Model(application)");
    }

    @NonNull
    @Override
    public <T extends Application> T getApplication() {
        return super.getApplication();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    // ============== liste des entrees (BaseEntrees)
    private MutableLiveData<List<EnregDico>> entrees;



    /*
    public LiveData<List<EnregDico>> getEntrees() {

        if (entrees == null) {
            entrees = new MutableLiveData<List<EnregDico>>();
        }
        loadEntrees();
        Log.d(TAG,"fin get entrees");
        return entrees;
    }

    private void loadEntrees() {
        if (searchText == null) {
            searchText = new MutableLiveData<String>();
            searchText.setValue("");
        }
        // Do an asynchronous operation to fetch users.
        Cursor curs = MeF.listeDesEntrees(searchText.getValue());
        AddStringTask myTask = new AddStringTask();
        myTask.execute(curs);

    }
*/
    /*
    Explications :
    ds le ViewController (FragmentDictView) fonction OnTextQueryChange, on appelle setSearchText
    ds le ViewController on a declare un observer sur -getEntrees()- ds OnCreateView
    comme le MutableLiveData -searchText- a change, cela lance la fonction  listeEntrees = Transformations.switchMap...
    qui au bout renvoie la liste des entrees

    voir avec entreesFTS pour une variante

     */
    public void setSearchText(String texte) {
        searchText.setValue(texte);
    }

    public LiveData<String> getSearchQuery() {
        if (searchText == null) {
            searchText = new MutableLiveData<String>();
            searchText.setValue("");
        }
        return searchText;
    }

    public LiveData<List<EnregDico>> getEntrees() {
        return listeEntrees;
    }

    public LiveData<List<EnregDico>> listeEntrees = Transformations.switchMap(getSearchQuery(), entree -> {
        if (entrees == null) {
            entrees = new MutableLiveData<List<EnregDico>>();
        }
        Cursor curs = MeF.listeDesEntrees(entree);
        AddStringTask myTask = new AddStringTask();
        myTask.execute(curs);
        Log.d(ActivitePrincipale2.TAG, TAG + "fin get entrees");

        return entrees;
    });

    public EnregDico getEntree(int position) {
        return entrees.getValue().get(position);
    }

//==================== liste des entreesFTS
    /*
    Explications :
    ds le ViewController (FragmentDictView) fonction OnTextQueryChange, on appelle setSearchTextFTS
    ds le ViewController on a declare un observer sur -getEntreesFTS()- ds OnCreateView
    comme le MutableLiveData -searchTextFTS- a change, on lance la fonction getEntreesFTS()
    qui au bout renvoie la liste des entreesFTS

     */

    private MutableLiveData<List<ResultatFTS>> entreesFTS;

    public void setSearchTextFTS(String texte) {
        searchTextFTS.setValue(texte);
    }

    public LiveData<List<ResultatFTS>> getEntreesFTS() {

        if (entreesFTS == null) {
            entreesFTS = new MutableLiveData<List<ResultatFTS>>();
        }
        loadEntreesFTS();
        return entreesFTS;
    }

    private void loadEntreesFTS() {
        if (searchTextFTS == null) {
            searchTextFTS = new MutableLiveData<String>();
            searchTextFTS.setValue("");
        }

        if (!searchTextFTS.getValue().isEmpty()) {
            List<ResultatFTS> listeMotsFR = MeF.rechercheEnregParDefinition(searchTextFTS.getValue());
            AddStringFTSTask myTask = new AddStringFTSTask();
            myTask.execute(listeMotsFR);
        }
    }

    public ResultatFTS getEntreeFTS(int position) {
        ResultatFTS r = entreesFTS.getValue().get(position);
        EnregDico enrT = MeF.flex.db.rechercheEntreeDicoParMotOrigDico(r.mot,r.dico);
        r.POS = enrT.POS;
        return r;
    }


    // ================= liste resultats analyse forme

    public void setSearchTextAnalyse(String texte) {
        searchTextAnalyse.setValue(texte);
    }


    public LiveData<String> getSearchTextAnalyse() {
        Log.d(ActivitePrincipale2.TAG, TAG + "get st analyse ?");


        if (searchTextAnalyse == null) {
            searchTextAnalyse = new MutableLiveData<String>();
            searchTextAnalyse.setValue("");
        }
/*
        if (stringHtmlRecherche == null) {
            stringHtmlRecherche = new MutableLiveData<String>();
            stringHtmlRecherche.setValue("");
        }

        if (!searchTextAnalyse.getValue().isEmpty()) {
            analyseForme();
        }

        return stringHtmlRecherche;
*/
        return searchTextAnalyse;

    }

    void analyseForme() {

        AnalyseHtmlParams par = new AnalyseHtmlParams();
        par.forme = searchTextAnalyse.getValue();
        par.largeurFen = largeurFenetre;
        par.taillePolice = taillePolice;
        AnalyseHtmlTask task = new AnalyseHtmlTask();
        task.execute(par);
    }

    public LiveData<String> resultatHtmlAnalyse = Transformations.switchMap(getSearchTextAnalyse(), forme -> {
        if (stringHtmlRecherche == null) {
            stringHtmlRecherche = new MutableLiveData<String>();
            stringHtmlRecherche.setValue("");
        }
        if (!forme.isEmpty()) {
            AnalyseHtmlParams par = new AnalyseHtmlParams();
            par.forme = searchTextAnalyse.getValue();
            par.largeurFen = largeurFenetre;
            par.taillePolice = taillePolice;
            AnalyseHtmlTask task = new AnalyseHtmlTask();
            task.execute(par);
        }
        //Log.d(TAG,"transf entr cour -" + stringHtml.getValue() + "-");
        return stringHtmlRecherche;
    });

    public LiveData<String> getHtmlAnalyseStringResult() {
        return resultatHtmlAnalyse;
    }


    private class AnalyseHtmlTask extends AsyncTask<AnalyseHtmlParams,Void,String> {

        @Override
        protected String doInBackground(AnalyseHtmlParams... htmlParams) {

            ArrayList resultats = MeF.analyseForme("?" + htmlParams[0].forme);
            String resF = MeF.rechercheHtml(htmlParams[0].forme,  resultats, largeurFenetre, taillePolice);

            return resF;
        }
        @Override
        protected void onPostExecute(String resultats) {
            //  htmlTemp = resultats;
            stringHtmlRecherche.setValue(resultats);
        }
    }


    // ============ setEntreeCourante -> entraine chg valeur de resultatHtml -> appelle l'observer
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
        if (!entree.mot.isEmpty()) {
            HtmlParams par = new HtmlParams();
            par.enreg = entree;
            par.largeurFen = largeurFenetre;
            par.taillePolice = taillePolice;
            HtmlTask ht = new HtmlTask();
            ht.execute(par);
        }
        //Log.d(TAG,"transf entr cour -" + stringHtml.getValue() + "-");
        return stringHtml;
    });

    private class HtmlTask extends AsyncTask<HtmlParams,Void,String> {

        @Override
        protected String doInBackground(HtmlParams... htmlParams) {

            String resF = MeF.chargeDef_et_Flexions(htmlParams[0].enreg.mot,largeurFenetre,taillePolice);

            return resF;
        }
        @Override
        protected void onPostExecute(String resultats) {
            //  htmlTemp = resultats;
            stringHtml.setValue(resultats);
        }
    }


    public LiveData<String> getHtmlStringResult() {
        return resultatHtml;
    }

    /*
    public MutableLiveData<String> getSearchText() {
        if (searchText == null) {
            searchText = new MutableLiveData<String>();
            searchText.setValue("");
        }
        return searchText;
    }
*/

// ============ setEntreeCouranteFTS -> entraine chg valeur de resultatFTSHtml -> appelle l'observer
    public void setEntreeCouranteFTS(ResultatFTS entree) {
        entreeCouranteFTS.setValue(entree);
    }

    public LiveData<ResultatFTS> getEntreeFTSCourante() {
        if (entreeCouranteFTS == null) {
            entreeCouranteFTS = new MutableLiveData<ResultatFTS>();
        }
        return entreeCouranteFTS;
    }


    public LiveData<String> resultatFTSHtml = Transformations.switchMap(getEntreeFTSCourante(), entree -> {
        if (stringFTSHtml == null) {
            stringFTSHtml = new MutableLiveData<String>();
            stringFTSHtml.setValue("");
        }
        if (!entree.mot.isEmpty()) {
            FTSHtmlParams par = new FTSHtmlParams();
            par.enreg = entree;
            par.largeurFen = largeurFenetre;
            par.taillePolice = taillePolice;
            FTSHtmlTask ht = new FTSHtmlTask();
            ht.execute(par);
        }
        return stringFTSHtml;
    });

    public LiveData<String> getFTSHtmlStringResult() {
        return resultatFTSHtml;
    }

    private class FTSHtmlTask extends AsyncTask<FTSHtmlParams,Void,String> {

        @Override
        protected String doInBackground(FTSHtmlParams... htmlParams) {
            String r = MeF.chargeMot_Definition(htmlParams[0].enreg,largeurFenetre,taillePolice);

            return r;
        }
        @Override
        protected void onPostExecute(String resultats) {
            //  htmlTemp = resultats;
            stringFTSHtml.setValue(resultats);
        }
    }



    // =============== asyncTask pour recuperer les listes entrees (BaseEntrees)
    private List<EnregDico> listProv;

    private class AddStringTask extends AsyncTask<Cursor, EnregDico, List<EnregDico>> {

        @Override
        protected void onPreExecute() {
            listProv = new ArrayList<EnregDico>();
        }

        @Override
        protected List<EnregDico> doInBackground(Cursor... params) {
            List<EnregDico> results = new ArrayList<>();
            int compte = 0;
            while (params[0].moveToNext()) {
                compte += 1;
                EnregDico enr = MeF.flex.db.curseur2Entree(params[0]);
                results.add(enr);

            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onProgressUpdate(EnregDico... values) {

            for (EnregDico v : values) {
                listProv.add(v);
            }
            entrees.setValue(listProv);
        }

        @Override
        protected void onPostExecute(List<EnregDico> resultats) {
            entrees.setValue(resultats);
        }


        @Override
        protected void onCancelled() {

        }
    }


    // =============== asyncTask pour recuperer les listes entreesFTS
    private List<ResultatFTS> listProvFTS;

    private class AddStringFTSTask extends AsyncTask<List<ResultatFTS>, ResultatFTS, List<ResultatFTS>> {

        @Override
        protected void onPreExecute() {
            listProvFTS = new ArrayList<ResultatFTS>();
        }

        @Override
        protected List<ResultatFTS> doInBackground(List<ResultatFTS>... params) {
            List<ResultatFTS> results = new ArrayList<>();
            if (isCancelled()) {

            }
            else {
                List<ResultatFTS> resG = new ArrayList<>();
                List<ResultatFTS> resP = new ArrayList<>();

                ParseGaffiot pg = new ParseGaffiot();
                StyleHtml style = new StyleHtml();
                String dics = "";
                int compte = 0;

                boolean limite = false;
                if (params[0].size() > 200) {
                    limite = true;
                }

                for (int ii = 0; ii < params[0].size(); ii++) {
                    if (isCancelled()) {
                        // progBar.setVisibility(View.INVISIBLE);
                        return results;
                    }
                    if (ii >= params[0].size()) {
                        // ou bien passer une copie de la listeMotsFR en parametre ????
                        Log.d(TAG," break - params[0].size : " + params[0].size());
                        break;
                    }
                    ResultatFTS item = (ResultatFTS) params[0].get(ii);
                    boolean test1 = true;
                    //Log.d(TAG,"as task : " + item.motRecherche  + " - " + item.mot);

                    /*
                    if (item.motRecherche.length() <= 3) {
                        int pos = item.positionMotRechercheDsDefAvecPoint();
                        if (pos > 0) {
                            String s1 = item.def.substring(pos,pos + 1);
                            if (s1.equals(s1.toUpperCase())) {
                                test1 = false;
                            }
                        }
                    }
                    */

                    if (test1) {
                        if (item.dico.equals("G")) {
                            String def2 = "";
                            if (item.def.length() > 500) {
                                def2 = pg.parseDefEpure(item.def,limite);
                            }
                            else {
                                def2 = item.def; //.replaceAll("\\\\[a-z]+\\{[\\w\\W-\\}]+\\}","");
                            }

                            //def2 = pg.parseDefEpure(item.def,limite);
                            boolean test = false;
                            int pos = item.positionMotRechercheDsDefCourte(def2); //item.mot + " " + def2
                            if (pos >= 0) {
                                test = true;
                            }
                            if (test) {
                                if (item.def.length() > 100) {
                                    int psmot = Math.max(pos,100); // + 20;
                                    item.defCourte = item.def.substring(0,psmot);
                                }
                                else {
                                    item.defCourte = item.def;
                                }

                                item.def = item.def.replaceAll("([ ,.;->])" + item.motRecherche + "([ ,.;:?!-<])","$1<span class=\"highlight\">" + item.motRecherche + "</span>$2");


                                item.def = "<BR/>" + style.enteteDict(item.dico) + "<BR/>" + item.def;
                                item.classement = def2;
                                resG.add(item);
                                results.add(item);
                                if (dics.isEmpty() || dics.equals("P")) {
                                    dics += "G";
                                }
                                compte += 1;
                            }
                        }
                        else {
                            if (item.def.length() > 100) {
                                item.defCourte = item.def.substring(0,100);
                            }
                            else {
                                item.defCourte = item.def;
                            }
                            item.classement = item.def;

                            item.def = item.def.replaceAll("([ ,.;->])" + item.motRecherche + "([ ,.;:?!-<])","$1<span class=\"highlight\">" + item.motRecherche + "</span>$2");

                            String motR2 = item.motRecherche.substring(0,1).toUpperCase() + item.motRecherche.substring(1);
                            item.def = item.def.replaceAll("([ ,.;->])" + motR2 + "([ ,.;:?!-<])","$1<span class=\"highlight\">" + motR2 + "</span>$2");

                            if (item.dico.equals("S")) {
                                item.def = "<BR/>" + style.enteteDict(item.dico) + "<BR/>" + "<b>" + item.mot + "</b>" + item.def;
                            }
                            else {
                                item.def = "<BR/>" + style.enteteDict(item.dico) + "<BR/>" + "<b>" + item.mot + "</b>, " + item.def;
                            }

                            resP.add(item);
                            results.add(item);
                            if (dics.isEmpty() || dics.equals("G")) {
                                dics += "P";
                            }
                            compte += 1;
                        }

                        if (params[0].size() > 40) {
                            //Log.d("TABULA ?????","0");
                            if (compte % 40 == 0 && compte >= 40) {

                                for (int i = 0; i < 40; i++) {
                                    int ind = compte - 40 + i;
                                    //Log.d("TABULA ?????",results.get(ind).mot);
                                    publishProgress(results.get(ind));
                                }

                            }
                        }
                    }
                }

                // for (ResultatFTS item : params[0]) {}

                results.clear();
                if (dics.equals("P")) {
                    //if (resP.size() < 40) {
                    ResultatFTS.trieResultatsFullTextSearch(resP);
                    //}
                    results.addAll(resP);
                }
                if (dics.equals("G")) {
                    //if (resP.size() < 50) {
                    ResultatFTS.trieResultatsFullTextSearch(resG);
                    //}
                    results.addAll(resG);
                }
                if (dics.equals("GP")) {
                    if (resP.size() + resG.size() < 10000) {
                        ResultatFTS.trieResultatsFullTextSearch(resG);
                        results.addAll(resG);
                        ResultatFTS.trieResultatsFullTextSearch(resP);
                        results.addAll(resP);
                    }
                    else {
                        results.addAll(resG);
                        results.addAll(resP);
                    }
                }
                if (dics.equals("PG")) {
                    if (resP.size() + resG.size() < 10000) {
                        ResultatFTS.trieResultatsFullTextSearch(resP);
                        results.addAll(resP);
                        ResultatFTS.trieResultatsFullTextSearch(resG);
                        results.addAll(resG);
                    /*
                    for (ResultatFTS rr : resG) {
                        Log.d(Dictionnaire.TAG,rr.mot);
                    }
                    */
                    }
                    else {
                        results.addAll(resP);
                        results.addAll(resG);
                    }
                }
            }


            return results;
            //return(null);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void onProgressUpdate(ResultatFTS... values) {

            for (ResultatFTS v : values) {
                listProvFTS.add(v);

            }
            entreesFTS.setValue(listProvFTS);
        }

        @Override
        protected void onPostExecute(List<ResultatFTS> resultats) {

            entreesFTS.setValue(resultats);
        }

        @Override
        protected void onCancelled(List<ResultatFTS> resultats) {
            super.onCancelled(resultats);
        }
        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }


    private class HtmlParams {
        EnregDico enreg = new EnregDico();
        float largeurFen = 0;
        int taillePolice = 0;

    }

    private class FTSHtmlParams {
        ResultatFTS enreg = new ResultatFTS();
        float largeurFen = 0;
        int taillePolice = 0;

    }

    private class AnalyseHtmlParams {
        String forme = "";
        float largeurFen = 0;
        int taillePolice = 0;

    }
}
