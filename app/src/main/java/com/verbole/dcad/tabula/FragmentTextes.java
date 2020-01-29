package com.verbole.dcad.tabula;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dcad on 1/16/18.
 */

public class FragmentTextes extends Fragment implements View.OnTouchListener, WebAppInterfaceCallback,
        ExpandableListView.OnChildClickListener, AdapterView.OnItemClickListener,
        View.OnLongClickListener,View.OnClickListener, SelectDocuments {

    static final String TAG = "VueDocuments ";
    ExpandableListView listeFichiers;
    FrameLayout frameLayListeFichiers; // pour separation avec webvue
    ListView listViewChapitres;
    List<String> listeChapitres;

    ArrayAdapter aradChapitres;
    CustomExpandableListAdapter mArrayAdapterTextes;

    WebViewPDF vueWebTxt;
    FrameLayout frameLayWebView;
    RelativeLayout relLay;
    MenuItem menuitemTxt;
    Context mContext;


    String formeAChercher;
    MiseEnForme MeF;
    Boolean popUpVisible = false;
    boolean toucheBouge = false;
    String nomFichierCourant = "";
    String pathFichierCourant = "";

    boolean utiliseFileExplorer = false;

    //WebAppInterface wAppInt;

    Point pointTouche = new Point(0,0);
    int taillePolice = 14;
    Menu monMenu;
    String langueAffiche = "Latin";
    String nbPagePDF = "0/0";

    final int TYPE_LISTE_FICHIERS = 0;
    final int TYPE_TEXTEAUTEUR = 1;
    final int TYPE_TEXTEAUTEUR_CHAPITRES = 2;
    final int TYPE_HTML = 3;
    final int TYPE_PDF = 4;

    int typeFichier = TYPE_LISTE_FICHIERS;
    // si phone
    int sectionCourante = 0;
    int indiceCourant = 0;
    int positionCourante = 0;

    int pageACharger = -1;

    final android.os.Handler myHandler = new android.os.Handler();
    private static int counter = 0;
    float currentScale = 1;
    float initialScale = -1;

    Toolbar mToolBar;
    Button bdroit;
    Button bgauche;
    Button blangue;
    TextView titreTexte;
    ImageView boutonRevient;

    Button boutonNewCarte;
    long dureeAnimations = 600;

    ArrayList resultatsCourants;
    private boolean mTwoPane;

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_vue_documents, container, false);
        mContext = getContext();
        mTwoPane = StyleHtml.verifieEcranEstTablette(this.getActivity());

        MeF = new MiseEnForme(mContext);

        listeFichiers = (ExpandableListView) view.findViewById(R.id.listeFichiers);
        if (mArrayAdapterTextes == null) {
            mArrayAdapterTextes = new CustomExpandableListAdapter(this.getContext());
        }
         //new ArrayAdapter(this,android.R.layout.simple_list_item_1,listNomFich);
        listeFichiers.setAdapter(mArrayAdapterTextes);
        //listeFichiers.setOnItemClickListener(this);
        listeFichiers.setOnChildClickListener(this);

        if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.KITKAT) {
            listeFichiers.expandGroup(mArrayAdapterTextes.gestionTextes.listeAuteurs.size());
        } else {
            utiliseFileExplorer = true;
            mArrayAdapterTextes.interfSelectDocs = this;
            //listeFichiers.expandGroup(mArrayAdapterTextes.gestionTextes.listeAuteurs.size());
        }

        //Convert pixel to dip
        //public int GetDipsFromPixel(float pixels) {
        // Get the screen's density scale
        // final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        // return (int) (pixels * scale + 0.5f); }
        float scale = getResources().getDisplayMetrics().density;
        int largInd = (int) (40 * scale + 0.5f);

        // bouton expandable

        if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.JELLY_BEAN_MR2) {
            listeFichiers.setIndicatorBounds(0, largInd);
        } else {
            listeFichiers.setIndicatorBoundsRelative(0, largInd);
        }


        vueWebTxt = view.findViewById(R.id.webVueTextes); //new WebViewPDF(mContext);
        initWebVue();

        frameLayWebView = view.findViewById(R.id.frameLayWebView);

        //GestionSettings gs = new GestionSettings(getContext());
        //taillePolice = gs.getTaillePolice();
        taillePolice = getResources().getInteger(R.integer.taille_police_webvues) + 2;

        if (mTwoPane) {
            vueWebTxt.setVisibility(View.VISIBLE);
            listeChapitres = new ArrayList<>();
        }

        if (!mTwoPane) {
            listViewChapitres = view.findViewById(R.id.listeChapitres);
            if (listeChapitres == null) {
                listeChapitres = new ArrayList<>();
            }

            if (aradChapitres == null) {
                aradChapitres = new ArrayAdapter(this.getContext(),R.layout.list_row_texte,listeChapitres);
            }

            listViewChapitres.setAdapter(aradChapitres);
            //listViewChapitres.setVisibility(View.INVISIBLE);
            listViewChapitres.setOnItemClickListener(this);
        }

        mToolBar = view.findViewById(R.id.toolbar_fragment_documents);


        bdroit = view.findViewById(R.id.action_droite);
        bdroit.setOnClickListener(this);
        bgauche = view.findViewById(R.id.action_gauche);
        bgauche.setOnClickListener(this);
        blangue = view.findViewById(R.id.action_langue);
        blangue.setOnClickListener(this);
        boutonRevient = view.findViewById(R.id.action_revient);
        boutonRevient.setOnClickListener(this);
        titreTexte = view.findViewById(R.id.titreTexte);

        mToolBar.setVisibility(View.GONE);

        ViewGroup.LayoutParams lp = mToolBar.getLayoutParams();
        lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
        mToolBar.setLayoutParams(lp);


        frameLayListeFichiers = view.findViewById(R.id.frameLayListeFichiers);
        relLay = view.findViewById(R.id.relLay);


        boutonNewCarte = view.findViewById(R.id.boutonNewCarte);
        boutonNewCarte.setOnClickListener(this);
        boutonNewCarte.getBackground().setColorFilter(getResources().getColor(R.color.bleu), PorterDuff.Mode.SRC_ATOP);
        boutonNewCarte.setVisibility(View.GONE);

        return view;
    }

    void initWebVue() {
        vueWebTxt.getSettings().setBuiltInZoomControls(true);
        vueWebTxt.getSettings().setDisplayZoomControls(false);
        vueWebTxt.getSettings().setJavaScriptEnabled(true);
        vueWebTxt.getSettings().setDomStorageEnabled(true);
        // vueWebTxt.getSettings().setPluginState(WebSettings.PluginState.ON);
        vueWebTxt.getSettings().setAllowFileAccess(true);
        vueWebTxt.getSettings().setAllowContentAccess(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            vueWebTxt.getSettings().setAllowFileAccessFromFileURLs(true);
            vueWebTxt.getSettings().setAllowUniversalAccessFromFileURLs(true);
        }

        vueWebTxt.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        vueWebTxt.setWebViewClient(setWbC());
      //  vueWebTxt.setWebChromeClient(setWCClient());

        vueWebTxt.loadDataWithBaseURL("", htmlbidon, "text/html", "utf-8", "");
        vueWebTxt.setVisibility(View.INVISIBLE);

        vueWebTxt.wAppInt.callback = this;
        vueWebTxt.setOnTouchListener(this);

        vueWebTxt.setOnLongClickListener(this);
        vueWebTxt.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(ActivitePrincipale2.TAG, TAG + "on Destroy ");
      //  MeF.flex.fermeDBs();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(ActivitePrincipale2.TAG, TAG + "on start ");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(ActivitePrincipale2.TAG, TAG + "on stop " + positionCourante);

    }

    @Override
    public void onPause() {
        super.onPause();
      //  MeF.flex.fermeDBs();
        Log.d(ActivitePrincipale2.TAG, TAG + "on pause ");
        if (mTwoPane) {
            String js = "(function() {getChapitreCourant()})()";
            webViewEvalJS(js);
        }

    }

    @Override
    public void onResume() {
        super.onResume();


        Log.d(ActivitePrincipale2.TAG, TAG + "on resume " + typeFichier + " posCourante : " + positionCourante + " langue : " + langueAffiche);
      //  MeF.flex.ouvreDBS();
        mArrayAdapterTextes.initListeFichiers();

        optionsMenu(typeFichier,nomFichierCourant);

        if (typeFichier == TYPE_HTML || typeFichier == TYPE_PDF) {
            chargeWebVue(nomFichierCourant,pathFichierCourant,false);
            if (mTwoPane) {
                frameLayListeFichiers.setVisibility(View.INVISIBLE);
            }
            else {
                listeFichiers.setVisibility(View.INVISIBLE);
                listViewChapitres.setVisibility(View.INVISIBLE);
            }
            vueWebTxt.setVisibility(View.VISIBLE);
        }
        if (typeFichier == TYPE_TEXTEAUTEUR) {

            if (mTwoPane) {

                chargeTexteAuteurTablet(sectionCourante, indiceCourant, false);
                frameLayListeFichiers.setVisibility(View.INVISIBLE);
            }
            else {
                chargeTexteAuteurPhone(sectionCourante, indiceCourant, positionCourante, false);
                listeFichiers.setVisibility(View.INVISIBLE);
                listViewChapitres.setVisibility(View.INVISIBLE);
            }

        }
        if (typeFichier == TYPE_LISTE_FICHIERS) {

        }
        if (typeFichier == TYPE_TEXTEAUTEUR_CHAPITRES) {
            listViewChapitres.setVisibility(View.VISIBLE);
            listeFichiers.setVisibility(View.INVISIBLE);

            // utilite ??
            listeChapitres.clear();
            List<String> lc = mArrayAdapterTextes.gestionTextes.listeChapitresOeuvreParSectionIndice(sectionCourante,indiceCourant);
            for (String c : lc) {
                listeChapitres.add(c);
            }
            aradChapitres.notifyDataSetChanged();


            String nomFich = mArrayAdapterTextes.getChild(sectionCourante,indiceCourant);
            optionsMenu(typeFichier,nomFich);
        }
     //   positionCourante = 0;

    }

    @Override
    public void onLowMemory() {
        Log.d(ActivitePrincipale2.TAG,TAG + "on low memory");
        destroyWebVue();
        super.onLowMemory();
    }

    void setUpWebView() {

    }



    void optionsMenu(int option, String titre) {
        if (option == TYPE_TEXTEAUTEUR) { // texte latin
            mToolBar.setVisibility(View.VISIBLE);
            if (StyleHtml.largeurFenetre(this.getActivity()) < 400) {
                titreTexte.setText(titre);
                titreTexte.setVisibility(View.GONE);
                //SpannableString spt = new SpannableString(titre);
                //spt.setSpan(new RelativeSizeSpan(0.75f),0,titre.length(),0);
                //titreTexte.setVisibility(View.VISIBLE);
                //titreTexte.setText(spt);
            }
            else {
                titreTexte.setText(titre);
                titreTexte.setVisibility(View.VISIBLE);
            }
            //MenuItem g = monMenu.getItem(1);
            //g.setVisible(true);
            //g.getIcon().setColorFilter(getResources().getColor(R.color.blanc), PorterDuff.Mode.SRC_ATOP);
            //MenuItem d = monMenu.getItem(3);
            //d.setVisible(true);
            //d.getIcon().setColorFilter(getResources().getColor(R.color.blanc), PorterDuff.Mode.SRC_ATOP);
            bdroit.setVisibility(View.VISIBLE);
            bdroit.setEnabled(true);
            bgauche.setVisibility(View.VISIBLE);
            bgauche.setEnabled(true);

            blangue.setVisibility(View.VISIBLE);
          //  blangue.setText("Latin");
          //  langueAffiche = "Latin";
            blangue.setText(langueAffiche);
            boutonRevient.setVisibility(View.VISIBLE);

        }

        if (option == TYPE_HTML) { // autre
            mToolBar.setVisibility(View.VISIBLE);
            //titreTexte.setText(titre);
            if (StyleHtml.largeurFenetre(this.getActivity()) < 400) {
                titreTexte.setText("");
                titreTexte.setVisibility(View.GONE);
            }
            else {
                titreTexte.setText("");
                titreTexte.setVisibility(View.VISIBLE);
            }
            bgauche.setVisibility(View.INVISIBLE);
            bdroit.setVisibility(View.INVISIBLE);
            blangue.setVisibility(View.INVISIBLE);
            boutonRevient.setVisibility(View.VISIBLE);

        }
        if (option == TYPE_PDF) { // autre
            mToolBar.setVisibility(View.VISIBLE);
            //titreTexte.setText(titre);
            if (StyleHtml.largeurFenetre(this.getActivity()) < 400) {
                titreTexte.setText("");
                titreTexte.setVisibility(View.GONE);
            }
            else {
                titreTexte.setText("");
                titreTexte.setVisibility(View.GONE);
            }
            bgauche.setVisibility(View.VISIBLE);
            bdroit.setVisibility(View.VISIBLE);
            blangue.setVisibility(View.VISIBLE);
            blangue.setText(nbPagePDF);
            boutonRevient.setVisibility(View.VISIBLE);

        }
        if (option == TYPE_TEXTEAUTEUR_CHAPITRES) { // autre
            mToolBar.setVisibility(View.VISIBLE);
            //titreTexte.setText(titre);
            if (StyleHtml.largeurFenetre(this.getActivity()) < 400) {

                titreTexte.setText(titre);
                //titreTexte.setVisibility(View.GONE);
                titreTexte.setVisibility(View.VISIBLE);
            }
            else {
                titreTexte.setText(titre);
                titreTexte.setVisibility(View.VISIBLE);
            }
            bgauche.setVisibility(View.GONE);
            bdroit.setVisibility(View.GONE);
            blangue.setVisibility(View.INVISIBLE);
            boutonRevient.setVisibility(View.VISIBLE);
        }
        if (option == TYPE_LISTE_FICHIERS) {
            bgauche.setVisibility(View.INVISIBLE);
            bdroit.setVisibility(View.INVISIBLE);
            blangue.setVisibility(View.INVISIBLE);
            mToolBar.setVisibility(View.GONE);
        }
    }

    boolean revient() {
        popUpVisible = false;
        if (vueWebTxt != null) {
            vueWebTxt.stopLoading();
            vueWebTxt.clearCache(true);
        }

        //vueWebTxt.loadDataWithBaseURL("", htmlbidon, "text/html", "utf-8", "");
        if (typeFichier != TYPE_LISTE_FICHIERS) {
            if (mTwoPane) {
                lanceAnimationRevientTablet();
            }
            else {
                lanceAnimationRevientPhone();
            }
            //lanceAnimationRevient();
            return false;
        }
        positionCourante = 0;
        return true;
    }



/*
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menutextes,menu);
        monMenu = menu;
        optionsMenu(2,"");


        super.onCreateOptionsMenu(menu,inflater);

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
       // popUpVisible = false;

        if (id == android.R.id.home) {
            Log.d(TAG,"home ????");
            revient();
        }
        /*
        if (id == R.id.homeAsUp ) {
            Log.d(TAG,"home as up");
        }

        if (id == R.id.action_langue) {

            if (item.getTitle().equals("Latin")) {
                item.setTitle("Français");
                langueAffiche = "Français";
            }
            else {
                item.setTitle("Latin");
                langueAffiche = "Latin";
            }
            String js = "(function() {changeLangue()})()";
            webViewEvalJS(js);
            //return true;
        }
        if (id == R.id.action_gauche) {
            String js = "(function() {enArriere()})()";
            webViewEvalJS(js);
        }
        if (id == R.id.action_droite) {
            String js = "(function() {enAvant()})()";
            webViewEvalJS(js);
        }

        return super.onOptionsItemSelected(item);
    }

*/



    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
        String nomFich = mArrayAdapterTextes.getChild(i,i1);
        String path = "";
        int nbauteurs = mArrayAdapterTextes.gestionTextes.listeAuteurs.size();
        sectionCourante = i;
        indiceCourant = i1;
        if (i < nbauteurs) {
            positionCourante = 0;
            if (!mTwoPane) {
                if (typeFichier == TYPE_LISTE_FICHIERS) {
                    listeChapitres.clear();
                    List<String> lc = mArrayAdapterTextes.gestionTextes.listeChapitresOeuvreParSectionIndice(i,i1);
                    for (String c : lc) {
                        listeChapitres.add(c);
                    }
                    aradChapitres.notifyDataSetChanged();

                    if (listeChapitres.size() == 1) {
                        typeFichier = TYPE_TEXTEAUTEUR;
                        optionsMenu(typeFichier,nomFich);
                        lanceAnimationMontreListeChapitres();
                        chargeTexteAuteurPhone(sectionCourante, indiceCourant, positionCourante, true);

                    }
                    else {
                        typeFichier = TYPE_TEXTEAUTEUR_CHAPITRES;
                        optionsMenu(typeFichier,nomFich);
                        lanceAnimationMontreListeChapitres();
                    }


                    return false;
                }
            }

            //tablet

            chargeTexteAuteurTablet(sectionCourante, indiceCourant, true);
            /*
            path = mArrayAdapterTextes.gestionTextes.fichierPartieOeuvreParSectionIndice(i,i1);
            String texte = mArrayAdapterTextes.gestionTextes.loadTexteAuteur(path,vueWebTxt.getWidth(),taillePolice);
            vueWebTxt.loadDataWithBaseURL(null, texte, "text/html", "utf-8", null);
            typeFichier = TYPE_TEXTEAUTEUR;
            lanceAnimationMontreWebVueTablet(typeFichier,nomFich);
           */
        }
        else {
            if (!utiliseFileExplorer) {
                List<String> listf = mArrayAdapterTextes.gestionTextes.getListeNomsFichiers();
                if (listf.size() > i1) {
                    nomFich = listf.get(i1);
                    List<String> lp = mArrayAdapterTextes.gestionTextes.getListePaths();
                    final String leChemin = lp.get(i1);
                    pathFichierCourant = leChemin;
                    nomFichierCourant = nomFich;

                    return traiteFichier(leChemin);

                }
            }


        }
        popUpVisible = false;
     //   menuitemTxt.setVisible(true);
        return false;
    }

    boolean traiteFichier(String chemin) {
        String nomFich = mArrayAdapterTextes.gestionTextes.getNomFichierFromPath(chemin);
        if (nomFich.endsWith(".tex")) {

            installeGaffiot(chemin);
            //new AsyncTaskInstalleDict(this).execute(leChemin);
            return false;
        }
        chargeWebVue(nomFich,chemin,true);
        popUpVisible = false;
        return false;
    }

    void installeGaffiot(String chemin) {
        ParseGaffiot pg = new ParseGaffiot();
        String version = pg.parseDebutFichier(chemin);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        String titre = "voulez-vous installer le dictionnaire Gaffiot " + version + " ?";
        if (!mTwoPane) {
            titre = "voulez-vous installer le dictionnaire Gaffiot ?";
        }
        builder.setTitle(titre);

        builder.setCancelable(false);
        builder.setPositiveButton("non",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("oui",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                lanceAsync(chemin);
                dialog.cancel();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    void chargePDFfile(String nomFichier, String pathFichier, boolean avecAnimation) {
        vueWebTxt.loadPDF(pathFichier);
        typeFichier = TYPE_PDF;
        if (avecAnimation) {
            if (mTwoPane) {
                lanceAnimationMontreWebVueTablet(typeFichier,nomFichier);
            }
            else {
                lanceAnimationMontreWebVuePhone(typeFichier,nomFichier);
            }
        }
    }

    void chargeHtmlString(String htmlString, String nomFichier, boolean avecAnimation) {

        String cf = htmlString;
        String remp = "";
        if (htmlString.contains("</body>")) {
            remp = "</body>";
        }
        if (htmlString.contains("< /body>")) {
            remp = "< /body>";
        }

        String scr1 = mArrayAdapterTextes.gestionTextes.scriptJS("jquery341.min.js");
        String scr2 = mArrayAdapterTextes.gestionTextes.scriptJS("SelectTextScriptINSIDE.js");

        String scriptRemp = "<script src=\"" + scr1 + "\"></script>" + "<script src=\"" + scr2 + "\"></script>";
        cf = cf.replace(remp,scriptRemp + remp);

        vueWebTxt.loadDataWithBaseURL(null, cf, "text/html", "utf-8", null);

        typeFichier = TYPE_HTML;
        if (avecAnimation) {
            if (mTwoPane) {
                lanceAnimationMontreWebVueTablet(typeFichier,nomFichier);
            }
            else {
                lanceAnimationMontreWebVuePhone(typeFichier,nomFichier);
            }
        }
    }

    void chargeWebVue(String nomFichier, String pathFichier, boolean avecAnimation) {


        if (nomFichier.endsWith(".pdf") || nomFichier.endsWith(".html")) { // || nomFich.endsWith(".rtf") marche pas
            if (nomFichier.endsWith("pdf")) {

                chargePDFfile(nomFichier,pathFichier,avecAnimation);
            }
            else {
                GestionFichiers GF = new GestionFichiers(mContext);
                String cf = GF.readStringFromPath(pathFichier); // "";
                /*
                try {
                    FileInputStream fis = new FileInputStream (new File(pathFichier));
                    int size = fis.available();
                    byte[] buffer = new byte[size];
                    fis.read(buffer);
                    fis.close();
                    cf = new String(buffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */
                chargeHtmlString(cf,nomFichier,avecAnimation);

            }

        }
    }



    void lanceAsync(String cheminFichier) {
        new AsyncTaskInstalleDict(this).execute(cheminFichier);
    }



    // listeChapitres si phone
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        positionCourante = position;
        typeFichier = TYPE_TEXTEAUTEUR;
        chargeTexteAuteurPhone(sectionCourante, indiceCourant, positionCourante, true);
    }

    void chargeTexteAuteurPhone(int section, int indice, int position, boolean animation) {
        String nomFich = mArrayAdapterTextes.getChild(section,indice);
        String path = mArrayAdapterTextes.gestionTextes.fichierPartieOeuvreParSectionIndice(section,indice);
        String texte = mArrayAdapterTextes.gestionTextes.loadTexteAuteur(path,vueWebTxt.getWidth(),taillePolice + 4);
        texte = texte.replace("<p class = \"marge\">", "<p class = \"cache\"");
        texte = texte.replace(".corpsTexte {margin-top:30px;}", ".corpsTexte {margin-top:10px;}");


        if (!langueAffiche.equals("Latin")) {
            texte = texte.replace("var langue1 = 'LAT';", "var langue1 = 'FR';");
            texte = texte.replace("var langue2 = 'FR';", "var langue2 = 'LAT';");
            texte = texte.replace("var texteLangue1 = 'Latin';", "var texteLangue1 = 'Français';");
            texte = texte.replace("var texteLangue2 = 'Français';", "var texteLangue2 = 'Latin';");
        }


        pageACharger = mArrayAdapterTextes.gestionTextes.pagePourChapitreOeuvreParSectionIndice(section, indice, position);
        vueWebTxt.loadDataWithBaseURL(null, texte, "text/html", "utf-8", null);
        if (animation) {
            lanceAnimationMontreWebVuePhone(typeFichier,nomFich);
        }
        else {
            float delta = listViewChapitres.getWidth();
            listViewChapitres.setTranslationX(delta);
            listViewChapitres.setVisibility(View.INVISIBLE);
            listeFichiers.setVisibility(View.INVISIBLE);
            vueWebTxt.setVisibility(View.VISIBLE);
            optionsMenu(typeFichier,nomFich);
        }

    }

    void chargeTexteAuteurTablet(int section, int indice, boolean animation) {
        String nomFich = mArrayAdapterTextes.getChild(section,indice);
        String path = mArrayAdapterTextes.gestionTextes.fichierPartieOeuvreParSectionIndice(section,indice);
        String texte = mArrayAdapterTextes.gestionTextes.loadTexteAuteur(path,vueWebTxt.getWidth(),taillePolice + 4);
        if (positionCourante > 0) {
            texte = texte.replace("cacheLangue(1);","cacheLangue(" + positionCourante + ");");
            //String js = "(function() {cacheLangue(" + positionCourante + ")})()";
            //webViewEvalJS(js);
        }
        if (!langueAffiche.equals("Latin")) {
            texte = texte.replace("var langue1 = 'LAT';", "var langue1 = 'FR';");
            texte = texte.replace("var langue2 = 'FR';", "var langue2 = 'LAT';");
            texte = texte.replace("var texteLangue1 = 'Latin';", "var texteLangue1 = 'Français';");
            texte = texte.replace("var texteLangue2 = 'Français';", "var texteLangue2 = 'Latin';");
        }
        vueWebTxt.loadDataWithBaseURL(null, texte, "text/html", "utf-8", null);


        typeFichier = TYPE_TEXTEAUTEUR;
        //optionsMenu(typeFichier,nomFich);
        if (animation) {
            lanceAnimationMontreWebVueTablet(typeFichier,nomFich);
        }
        else {
            float delta = -frameLayListeFichiers.getWidth();
            frameLayListeFichiers.setTranslationX(delta);
            frameLayListeFichiers.setVisibility(View.INVISIBLE);
            optionsMenu(typeFichier,nomFich);
        }


    }

    void lanceAnimationRevientPhone() {

        if (typeFichier == TYPE_TEXTEAUTEUR_CHAPITRES) {
            listeFichiers.setVisibility(View.VISIBLE);
            if (listeFichiers.getTranslationX() == 0) {
                listeFichiers.setTranslationX(listeFichiers.getWidth());
            }
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(listeFichiers,"translationX",0);
            anim1.setDuration(dureeAnimations);
            anim1.start();

            typeFichier = TYPE_LISTE_FICHIERS;
            optionsMenu(TYPE_LISTE_FICHIERS,"");
        }
        if (typeFichier == TYPE_PDF || typeFichier == TYPE_HTML) {
            listeFichiers.setVisibility(View.VISIBLE);
            listViewChapitres.setVisibility(View.VISIBLE);
            if (listeFichiers.getTranslationX() == 0) {
                listeFichiers.setTranslationX(listeFichiers.getWidth());
            }
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(listeFichiers,"translationX",0);
            anim1.setDuration(dureeAnimations);
            anim1.start();

            typeFichier = TYPE_LISTE_FICHIERS;
            optionsMenu(TYPE_LISTE_FICHIERS,"");
        }
        if (typeFichier == TYPE_TEXTEAUTEUR) {


            if (listeChapitres.size() == 1) {
                listViewChapitres.setTranslationX(0);

                listeFichiers.setVisibility(View.VISIBLE);

                if (listeFichiers.getTranslationX() == 0) {
                    listeFichiers.setTranslationX(listeFichiers.getWidth());
                }

                ObjectAnimator anim1 = ObjectAnimator.ofFloat(listeFichiers,"translationX",0);
                anim1.setDuration(dureeAnimations);
                anim1.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        listViewChapitres.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                });
                anim1.start();


                typeFichier = TYPE_LISTE_FICHIERS;
                optionsMenu(TYPE_LISTE_FICHIERS,"");
            }
            else {
                listViewChapitres.setVisibility(View.VISIBLE);
                listeFichiers.setVisibility(View.INVISIBLE);

                if (listViewChapitres.getTranslationX() == 0) {
                    listViewChapitres.setTranslationX(listViewChapitres.getWidth());
                }
                ObjectAnimator anim1 = ObjectAnimator.ofFloat(listViewChapitres,"translationX",0);
                anim1.setDuration(dureeAnimations);
                anim1.start();
                typeFichier = TYPE_TEXTEAUTEUR_CHAPITRES;
                optionsMenu(TYPE_TEXTEAUTEUR_CHAPITRES,titreTexte.getText().toString());
            }



        }
    }
    void lanceAnimationRevientTablet() {
        frameLayListeFichiers.setVisibility(View.VISIBLE);
        if (frameLayListeFichiers.getTranslationX() == 0) {
            frameLayListeFichiers.setTranslationX(frameLayListeFichiers.getWidth());
        }
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(frameLayListeFichiers,"translationX",0);
        anim1.setDuration(dureeAnimations);
        anim1.start();
        typeFichier = TYPE_LISTE_FICHIERS;
        optionsMenu(TYPE_LISTE_FICHIERS,"");
    }

    void lanceAnimationMontreListeChapitres() {
        float delta = listeFichiers.getWidth();

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(listeFichiers,"translationX",delta);
        anim1.setDuration(dureeAnimations);
        anim1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                listeFichiers.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim1.start();
        vueWebTxt.loadDataWithBaseURL("", htmlbidon, "text/html", "utf-8", "");
        vueWebTxt.setVisibility(View.INVISIBLE);


    }

    void lanceAnimationMontreWebVueTablet(final int typeFichierNum, final String nomFichier) {
        float delta = -frameLayListeFichiers.getWidth();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(frameLayListeFichiers,"translationX",delta);
        anim1.setDuration(dureeAnimations);
        anim1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                frameLayListeFichiers.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim1.start();
        optionsMenu(typeFichierNum,nomFichier);
    }
    void lanceAnimationMontreWebVuePhone(final int typeFichierNum, final String nomFichier) {

        if (typeFichier == TYPE_PDF || typeFichier == TYPE_HTML) {
            float delta = listeFichiers.getWidth() + 50;
            listViewChapitres.setVisibility(View.INVISIBLE);
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(listeFichiers,"translationX",delta);
            anim1.setDuration(dureeAnimations);
            anim1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    listeFichiers.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            anim1.start();
        }
        if (typeFichier == TYPE_TEXTEAUTEUR) {
            float delta = listViewChapitres.getWidth() + 50;
            ObjectAnimator anim1 = ObjectAnimator.ofFloat(listViewChapitres,"translationX",delta);
            anim1.setDuration(dureeAnimations);
            anim1.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    listViewChapitres.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            anim1.start();

        }
        vueWebTxt.setVisibility(View.VISIBLE);
        optionsMenu(typeFichierNum,nomFichier);
    }


    WebViewClient setWbC() {
        return new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!mTwoPane) {
                    if (typeFichier == TYPE_TEXTEAUTEUR) {
                        if (pageACharger != -1) {
                            Log.d(TAG,"va lancer cache langue - " + pageACharger);
                            webViewEvalJS("(function() {cacheLangue(" + String.valueOf(pageACharger) + ")})()");
                            //(from: " (function() {cacheLangue(\(self.pageACharger))})()")
                        }
                    }
                }
            }
            @Override
            public void onScaleChanged(WebView view, float oldScale, float newScale) {
                super.onScaleChanged(view, oldScale, newScale);
                //Log.d(TAG,"webVue client on sc changed : old scale : " + oldScale);
                //Log.d(TAG,"webVue client on sc changed : new scale : " + currentScale);
                if (initialScale < 0) {
                    initialScale = oldScale;
                }
                currentScale = newScale/initialScale;
            }
            @Override
            public boolean onRenderProcessGone(WebView view,
                                               RenderProcessGoneDetail detail) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (!detail.didCrash()) {
                        // Renderer was killed because the system ran out of memory.
                        // The app can recover gracefully by creating a new WebView instance
                        // in the foreground.
                        Log.d(ActivitePrincipale2.TAG, TAG + "System killed the WebView rendering process " +
                                "to reclaim memory. Recreating...");

                        // By this point, the instance variable "mWebView" is guaranteed
                        // to be null, so it's safe to reinitialize it.
                        destroyWebVue();

                        return true; // The app continues executing.
                    }
                }

                // Renderer crashed because of an internal error, such as a memory
                // access violation.
                Log.d(ActivitePrincipale2.TAG, TAG + "The WebView rendering process crashed!");

                // In this example, the app itself crashes after detecting that the
                // renderer crashed. If you choose to handle the crash more gracefully
                // and allow your app to continue executing, you should 1) destroy the
                // current WebView instance, 2) specify logic for how the app can
                // continue executing, and 3) return "true" instead.
                destroyWebVue();

                return true;
                //return false;
            }
            /*
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,String url) {
                return false;
            }
            */
            //public void onPageStarted(WebView view, String url, Bitmap favicon) {vueWebTxt.setVisibility(View.INVISIBLE);}
        };
    }

    void destroyWebVue() {
        Log.d(ActivitePrincipale2.TAG,TAG + "destroy WebView ?");
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(null,null);
        if (vueWebTxt != null) {
            lp = (FrameLayout.LayoutParams) vueWebTxt.getLayoutParams();
            //ViewGroup webViewContainer = (ViewGroup) findViewById(R.id.frameLayWebView);
            //webViewContainer.removeView(vueWebTxt);

            frameLayWebView.removeView(vueWebTxt);

            vueWebTxt.destroy();
            vueWebTxt = null;
        }
        vueWebTxt = new WebViewPDF(getContext());
        if (lp != null) {
            vueWebTxt.setLayoutParams(lp);
            initWebVue();
        }
    }

    WebChromeClient setWCClient() {
        return new WebChromeClient() {
            public boolean onConsoleMessage(ConsoleMessage cm) {
                Log.d(TAG, cm.message() + " -- From line "
                        + cm.lineNumber() + " of "
                        + cm.sourceId() );
                return true;
            }
        };
    }


    String htmlbidon = "<html  <head></head> <body><div><p></p> </div></body></html>"; //Lorem ipsum etc...


    @Override
    public void debug(String texte) {
        Log.d(TAG,"webappInt debug : " + texte);
    }
/*
    public void montrePopUp() {
        popUpVisible = true;
        myHandler.post(myRunnable);
    }
    public void dismissPopUp() {
        formeAChercher = "X";
        if (popUpVisible) {
            popUpVisible = false;
            myHandler.post(myRunnable);
        }
    }
*/
    private void affichePopUp(String texte) {
        final PopupWindow resultat = new PopupWindow(this.getContext());

        WebView wv = new WebView(this.getContext());

        resultat.setContentView(wv);
        wv.setBackgroundColor(Color.WHITE);
        float w,h;
        float wt = vueWebTxt.getWidth(); //StyleHtml.largeurFenetrePixels(this.getActivity());
        float ht = vueWebTxt.getHeight(); //StyleHtml.hauteurFenetrePixels(this.getActivity());

        int widthDP = (int) StyleHtml.largeurFenetre(getActivity());//(int) StyleHtml.conversionLargeur_enDP(vueWebTxt.getWidth(),getActivity());
        int heightDP = (int) StyleHtml.hauteurFenetre(getActivity()); // StyleHtml.conversionHauteur_enDP(vueWebTxt.getHeight(),getActivity());
        //int dy = (int) (StyleHtml.dp2pixel(getResources().getDimension(R.dimen.deltay_popup_window),getActivity()));
        int dy = (int) getResources().getDimension(R.dimen.deltay_popup_window);
// apparemment fait de lui-meme la conversion ...

        if (mTwoPane) {
            w = Math.min(wt,ht) / 2;
            h = Math.min(wt,ht) / 3;
        }
        else {
            w = Math.min(wt,ht) * 2/3; // - 2 * dy; //
            h = Math.min(wt,ht) / 2;
        }

        int ww = (int) w;
        int hh = (int) h;
        wv.setLayoutParams(new AbsListView.LayoutParams(ww-2,hh-2,View.TEXT_ALIGNMENT_VIEW_START));
        resultat.setWidth(ww);
        resultat.setHeight(hh);
        wv.loadDataWithBaseURL("", texte, "text/html", "utf-8", "");


        resultat.setTouchInterceptor(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_OUTSIDE) {
                    resultat.dismiss();
                    return true;
                }
                return false;
            }
        });

        resultat.setOutsideTouchable(true);

        Log.d(ActivitePrincipale2.TAG,TAG + "vueweb width : " + vueWebTxt.getWidth() + " - height : " + vueWebTxt.getHeight());
        Log.d(ActivitePrincipale2.TAG,TAG + "vueweb widthdp : " + widthDP + " - heightdp : " + heightDP);
        Log.d(ActivitePrincipale2.TAG,TAG + "pointtouche.x : " + pointTouche.x + " pointtouche.y : " + pointTouche.y);

        Point pt;

        int posX = Math.max(0,pointTouche.x - ww/2);
        if (pointTouche.y > vueWebTxt.getHeight() - hh + dy) {
            pt = new Point(posX,pointTouche.y - hh + 2 * dy); // + dy

            Log.d(ActivitePrincipale2.TAG,TAG + "> : resultat x  : " + pt.x + " y : " + pt.y);
        }
        else {
            pt = new Point(posX,pointTouche.y + hh - dy); //- dy
            Log.d(ActivitePrincipale2.TAG,TAG + "< : resultat x  : " + pt.x + " y : " + pt.y);

        }
        Log.d(ActivitePrincipale2.TAG,TAG + "wb pos.x : " + pt.x + " wb pos.y : " + pt.y + " popup width : " + (ww-2) + " popup height : " + hh + " - dy : " + dy);
        resultat.showAtLocation(vueWebTxt, Gravity.TOP | Gravity.LEFT, pt.x, pt.y); // top left


        popUpVisible = true;
        boutonNewCarte.setVisibility(View.VISIBLE);
    }

    final Runnable myRunnable = new Runnable() {
        @Override
        public void run() {

            if (formeAChercher.equals("X")) {
            }
            else {
                ArrayList resultats = MeF.flex.rechercheForme(formeAChercher,false,"");
                if (resultats.size() == 0) {
                    if (!formeAChercher.toLowerCase().equals(formeAChercher)) {
                        resultats = MeF.flex.rechercheForme(formeAChercher.toLowerCase(),false,"");
                    }
                }
                ArrayList resultatsPropres = MeF.trieListeResultats(resultats);
                resultatsCourants = new ArrayList();
                resultatsCourants.addAll(resultats);
                String html = MeF.rechercheHtmlDsTexte(formeAChercher,resultatsPropres,vueWebTxt.getWidth(),taillePolice);
                affichePopUp(html);
            }
        }
    };

    /*
        // callback pour evaluateJavaScript
        @Override
        public void onReceiveValue(String s) {
            recupereTexte(s);
        }
       */
    public void recuperePages(int pageCourante, int nbPages) {
        nbPagePDF = pageCourante + "/" + nbPages;

       //blangue.setText(nbPagePDF);
        // !!!! NON !!! ne pas changer UI (ds main thread ????)
        // sinon message W/cr_BindingManager: Cannot call determinedVisibility() - never saw a connection for the pid
        new changePageTask().execute(nbPagePDF);
    }

    @Override
    public void recupereChapitreCourant(int chapitre) {
        //Log.d(TAG,"recupChap? " + chapitre);
        positionCourante = chapitre;
    }


    private class changePageTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            blangue.setText(result);

        }

        @Override
        protected String doInBackground(String... params) {
            return params[0];
        }
    }

    public void recupereTexte(String texte) {
        Log.d("recupere texte", "T" + texte + "T");
        formeAChercher = texte;
        if (formeAChercher.length() > 0 && langueAffiche.equals("Latin")) {
            //if (!formeAChercher.equals("Suivant") && !formeAChercher.equals("Précédent"))
            popUpVisible = true;
            myHandler.post(myRunnable);
        }

        /*
            ClipboardManager cm = (ClipboardManager)
                    mContext.getSystemService(Context.CLIPBOARD_SERVICE);
            String txtSelect = cm.getPrimaryClip().getItemAt(0).getText().toString();
            Log.d("clipboard texte== ", txtSelect);
            */
    }

    void webViewEvalJS(String js) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            vueWebTxt.evaluateJavascript(js, null);
        } else {
            vueWebTxt.loadUrl("javascript:"+js);
        }
    }



    String unhighlightScript() {
        String sc = "function unhighlight() {";
        sc += "var el = document.getElementById('highlight32');";
        sc += "if (el) {";
        sc += "var txt = el.innerHTML;";
        sc += "var txtNode = document.createTextNode(txt);";
        sc += "el.parentNode.replaceChild(txtNode,el);";
        sc += "return true;";
        sc += "}";
        sc += "return false; }";
        return sc;
    }


    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        view.performClick();
        //Log.d(TAG,"touche" + popUpVisible);
       // debugAnalyseForme();
        boutonNewCarte.setVisibility(View.GONE);

        float px = motionEvent.getX(); // * currentScale;
        float py = motionEvent.getY(); // * currentScale;
        pointTouche.set((int) px, (int) py);
        switch(motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                if (typeFichier == TYPE_HTML) {
                    webViewEvalJS(unhighlightScript());
                }
                toucheBouge = false;
                break;
            case MotionEvent.ACTION_MOVE:

                counter++; //check how long the button is pressed
                if(counter>5){
                    toucheBouge = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                //Log.d(TAG,"touche action up " + toucheBouge);
                if(counter<5){
                    //this is just onClick, handle it(10 is example, try different numbers)
                    if (typeFichier == TYPE_HTML) {
                        /*
                        if (!popUpVisible) {
                            if (langueAffiche.equals("Latin")) {
                                Log.d(TAG,"point : " + String.valueOf(pointTouche.x) + "-" + String.valueOf(pointTouche.y));
                                String scr = MeF.getTextScript("SelectTextScriptEXT");

                                scr = scr.replace("XCOORD",String.valueOf(pointTouche.x));
                                scr = scr.replace("YCOORD",String.valueOf(pointTouche.y));
                                scr = scr.replace("WIDTH",String.valueOf(vueWebTxt.getWidth()));
                                scr = scr.replace("HEIGTH",String.valueOf(vueWebTxt.getHeight()));

                                String js2 = "(function (){" + scr +
                                        "})()";
                                webViewEvalJS(js2);
                                return false;
                            }
                        }
                        else {
                            //popUpVisible = true;
                        }
                        */
                    }


                    if (!toucheBouge) {}
                    toucheBouge = false;
                }else{
                    //it's a move
                }
                counter = 0;

                break;
            default:
                break;
        }

        return false;

    }


    @Override
    public boolean onLongClick(View v) {
        if (typeFichier == TYPE_PDF) {
            Log.d(TAG,"long click  ? ");
            /*
            Log.d(TAG,"point : " + String.valueOf(pointTouche.x) + "-" + String.valueOf(pointTouche.y));
            String scr = MeF.getTextScript("SelectTextScript");
            scr = scr.replace("XCOORD",String.valueOf(pointTouche.x));
            scr = scr.replace("YCOORD",String.valueOf(pointTouche.y));
            scr = scr.replace("WIDTH",String.valueOf(vueWebTxt.getWidth()));
            scr = scr.replace("HEIGTH",String.valueOf(vueWebTxt.getHeight()));
            String js2 = "(function (){" + scr +
                    "})()";
            webViewEvalJS(js2);
            */
        }
        return false;
    }


    // interface SelectDocuments
    @Override
    public void ouvreDocuments() {
        listeFichiers.collapseGroup(mArrayAdapterTextes.gestionTextes.listeAuteurs.size());
        File yo = mArrayAdapterTextes.gestionTextes.GF.getDirectoryStorage(getActivity());
        Uri uri = Uri.fromFile(yo);

        /*
       // String p = mContext.getExternalFilesDir(null).toString() +  File.separator + "Tabula" + File.separator;
        String p = mContext.getFilesDir().getPath() +  File.separator + "Tabula" + File.separator;
        Uri uri4 = new Uri.Builder()
                .scheme(BuildConfig.APPLICATION_ID)
                .authority(mContext.getApplicationContext().getPackageName() + ".provider")
                .path(p)
                .build();
        //Uri uri4 = //FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".provider", yo);

        Log.d(TAG,"uri : " + uri4.toString() + " authority " + uri4.getAuthority());
        String p01 = mContext.getApplicationContext().getPackageName() + ".provider" + "/" + p;
        Uri uriyo = Uri.parse(p01);
        Log.d(TAG,"uriyo : " + uriyo + " orig : " + p01);

        //openDirectory(uri);
        String p1 = Environment.getExternalStorageDirectory().getPath()
                +  File.separator + "Tabula" + File.separator;

        Uri uri22 = Uri.parse(p1);

        Uri uri2 = Uri.parse(mContext.getApplicationContext().getPackageName() + ".provider" + "/" + p1);

        Log.d(TAG,"uri2 : " + uri2.toString() + " orig : " + p1);
        String p2 = mContext.getFilesDir().getPath() +  File.separator + "Tabula" + File.separator;
*/
        openFile(uri);
    }

    /*  appelle activite browse document */
    // si apres KitKat = 19
    private void openFile(Uri pickerInitialUri) {
        Log.d(ActivitePrincipale2.TAG,TAG + "open file : " + pickerInitialUri.toString());
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
       // intent.setType("application/pdf");

        // Optionally, specify a URI for the file that should appear in the
        // system file picker when it loads.

        if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.O) {
            //intent.putExtra("path",pickerInitialUri.toString());
        } else {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);
        }

        intent.setType("*/*");

        startActivityForResult(intent, 1);
    }




    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode,resultCode,data);
Log.d(ActivitePrincipale2.TAG,TAG + " !!!!! ===== retester avec UriHelper get path et installe Gaffiot a partir de " +
        "Buffered Reader + voir lire debut fichier ==============");
        if (resultCode == Activity.RESULT_OK) {

            Uri uri = data.getData();
            Log.d(TAG," activity result: " + uri.toString());
            String dernpart = uri.getLastPathSegment();
            String nomFichier = dernpart;
            if (dernpart.contains("/")) {
                int indt = dernpart.indexOf("/");
                if (nomFichier.length() > indt + 1) {
                    nomFichier = nomFichier.substring(indt + 1);
                }

            }

            if (dernpart.endsWith(".html")) { // || dernpart.endsWith(".rtf")
                //String path = URI_helper.getPath(getContext(),uri);
                nomFichier = nomFichier.replace(".html","");
                String cf = "";
             //   String cf = GestionFichiers.readStringFromInputStream(getActivity(),uri);
             //   GestionFichiers GF = new GestionFichiers(mContext);
             //   String cf = GF.readStringFromPath(path);

                try {
                    InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
                    /*
                    int sizeAvailable = inputStream.available(); //fis.available();
                    int size = Math.min(sizeAvailable,1024);
                    byte[] buffer = new byte[size];

                    inputStream.read(buffer);
                    inputStream.close();

                    cf = new String(buffer);
                    */

                    BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder total = new StringBuilder(inputStream.available());
                    for (String line; (line = r.readLine()) != null; ) {
                        total.append(line).append('\n');
                    }
                    cf = total.toString();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                cf = cf.replace("HTML","html");
                cf = cf.replace("BODY","body");
                cf = cf.replace("HEAD","head");

                cf = cf.replace("META","meta");

                //MiseEnForme.afficheGrosLog(cf,TAG);

                //Log.d(TAG,"string length : " + cf.length());

                chargeHtmlString(cf,nomFichier,true);

            }

            String path = URI_helper.getPath(getContext(),uri);

            if (dernpart.endsWith(".tex")) {
                installeGaffiot(path);
            }
            if (dernpart.endsWith(".pdf")) {
                //nomFichier = mArrayAdapterTextes.gestionTextes.getNomFichierFromPath(path);
                nomFichier = nomFichier.replace(".pdf","");
                chargePDFfile(nomFichier, path, true);
            }


            Log.d(TAG,"yo bien recu1 ... dern part : " + dernpart + " path : " + path);
        }


    }
    public void openDirectory(Uri uriToLoad) {
        // Choose a directory using the system's file picker.
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);

        // Provide read access to files and sub-directories in the user-selected
        // directory.
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when it loads.
        if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.O) {
            intent.putExtra(DocumentsContract.PROVIDER_INTERFACE,uriToLoad);

        } else {
            intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, uriToLoad);
        }


        startActivityForResult(intent, 1);
    }


    void changueLangue() {
        String js = "(function() {changeLangue()})()";
        webViewEvalJS(js);
    }

    @Override
    public void onClick(View v) {

        popUpVisible = false;
        if (v == boutonRevient) {
            boutonNewCarte.setVisibility(View.GONE);
            revient();
        }
        if (v == blangue) {
            if (typeFichier != TYPE_PDF) {
                if (langueAffiche.equals("Latin")) {
                    blangue.setText("Français");
                    langueAffiche = "Français";
                }
                else {
                    blangue.setText("Latin");
                    langueAffiche = "Latin";
                }
                changueLangue();

            }
            //return true;
        }

        if (v == bgauche || v == bdroit) {
            if (mTwoPane) {
                String js = "(function() {getChapitreCourant()})()";
                webViewEvalJS(js);
            }
        }

        if (v == bgauche) {
            Log.d(TAG,"bgauche - " + positionCourante + " - " + listeChapitres.size());
            if (positionCourante > 0) {
                String js = "(function() {enArriere()})()";
                webViewEvalJS(js);
                positionCourante -= 1;

                if (positionCourante <= 0) {
                    bgauche.setEnabled(false);
                }
            }
            bdroit.setEnabled(true);

        }
        if (v == bdroit) {
            Log.d(TAG,"bdroit - " + positionCourante + " - " + listeChapitres.size());
            if (positionCourante < listeChapitres.size() - 1 || listeChapitres.size() == 0) {
                //  listeChapitres.size() = 0 : tablette
                String js = "(function() {enAvant()})()";
                webViewEvalJS(js);
                positionCourante += 1;

                if (positionCourante >= listeChapitres.size() - 1 && listeChapitres.size() > 0) {
                    bdroit.setEnabled(false);
                }
            }
            bgauche.setEnabled(true);


        }
        if (v == boutonNewCarte) {
            createDial();
        }
    }


    private static class AsyncTaskInstalleDict extends AsyncTask<String,Integer,Integer> implements EcouteInstall {
        private ProgressDialog dialog;
        //warning : This AsyncTask class should be static or leaks might occur (anonymous android.os.AsyncTask)
//https://stackoverflow.com/questions/44309241/warning-this-asynctask-class-should-be-static-or-leaks-might-occur
        private WeakReference<FragmentTextes> activityReference;

        // only retain a weak reference to the activity
        AsyncTaskInstalleDict(FragmentTextes context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(), "init parlos ! ",
            //      Toast.LENGTH_SHORT).show();
            dialog = new ProgressDialog(activityReference.get().getActivity());//= ProgressDialog.show(getApplicationContext(), "Please wait..", "Doing stuff..", true);
            dialog.setMessage("Installation du dictionnaire ...");
              dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            // reset the bar to the default value of 0
             dialog.setProgress(0);
             dialog.setMax(72165);
            //dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            activityReference.get().MeF.flex.bdDics.ecouteInstalle = this;
            dialog.show();
        }

        @Override
        protected Integer doInBackground(String ...params) {
            //do some serious stuff...
            String nf = params[0];
            File f = new File(nf);
            if (activityReference.get().MeF.flex.bdDics.installeGaffiot(f,"GaffiotInd")) {
                return 1;
            }
            return 0;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {

            dialog.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Integer result) {

            //signal.countDown();
            dialog.dismiss();
            Toast toast;
            if (result == 0) {
                toast = Toast.makeText(activityReference.get().getContext(),"le Dictionnaire n'a pas pu être installé ...",Toast.LENGTH_SHORT);
                //toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                toast.show();
                //toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            }
            else {
                toast = Toast.makeText(activityReference.get().getContext(),"le Dictionnaire Gaffiot a été installé.",Toast.LENGTH_SHORT);
                //toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                toast.show();
                // Toast.makeText(getApplicationContext(), "Dictionary installed sucessfully !",Toast.LENGTH_SHORT).show();

                GestionSettings gs = new GestionSettings(activityReference.get().getActivity());
                gs.setDicts("PG");

            }

        }

        @Override
        public void progression(int nombre) {
            publishProgress(nombre);

        }
    }



    public void createDial() {
        DialogueAjouteCarte dial = new DialogueAjouteCarte(getContext(),getActivity(),MeF);
        dial.createDialPourResultatsRecherches(resultatsCourants);


    }

    void debugAnalyseForme() {
        recupereTexte("hominem");
    }


    // a voir
    // https://stackoverflow.com/questions/33162152/storage-permission-error-in-marshmallow
    private static final int PERMISSION_REQUEST_CODE = 1;
    private void requestPermissionWrite() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), "la permission d'accès au stockage est nécessaire pour l'ajout de documents dans l'application.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }
    private void requestPermissionRead() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(getContext(), "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }




}
