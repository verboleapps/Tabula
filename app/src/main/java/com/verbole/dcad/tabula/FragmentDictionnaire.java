package com.verbole.dcad.tabula;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.FilterQueryProvider;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentDictionnaire.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentDictionnaire#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDictionnaire extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener,
        AbsListView.OnScrollListener, LoaderManager.LoaderCallbacks<Cursor>, SearchView.OnCloseListener,
        View.OnFocusChangeListener {
    static final String TAG = "TABULA Dictionnaire";

    ListView listViewMots;

    DictionaryAdapter mArrayAdapter;
    DictionaryLoader mDicLoad;
    FilterQueryProv mFilter;

    ListView listViewMotsFR;
    ListeEntreesAdapter mListAdapterFR;
    List<ResultatFTS> listeMotsFR;
    RelativeLayout relLay;

    static String leTexte = "";
    static int indexRecherche = 0;
    SearchView barreRecherche;
    WebView myWebView;
    MiseEnForme MeF;
    Button boutonLangue;
    String langueRecherche = "Latin";

    // tt cela devrait etre mis ds data model ou ds MiseEnForme ...
    String entreeCourante = "";
    ResultatFTS resultatFTSCourant; // utilise pour sauver resultats FTS en flash carte
    ArrayList resultatsCourants; // utilise pour sauver resultats analyse forme en flash cartes

    Button boutonNewCarte;

    float largeurFenetre = 0;
    int taillePolice = 14;
    long dureeAnimations = 600;

    private boolean mTwoPane;
    private boolean isDefVisible = false;
    private boolean aStoppe = false;
    String texteHtmlCourant = "";

    AddStringTask myTask;

    private OnFragmentInteractionListener mListener;


    public FragmentDictionnaire() {
        // Required empty public constructor
    }


    public static FragmentDictionnaire newInstance(String param1, String param2) {
        FragmentDictionnaire fragment = new FragmentDictionnaire();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"debut on createView");
        View view =  inflater.inflate(R.layout.activity_dictionnaire, container, false);

        GestionSettings gs = new GestionSettings(getContext());
        gs.setDefault();
        gs.setTaillePolice(14);
        //taillePolice = gs.getTaillePolice();
        taillePolice = getResources().getInteger(R.integer.taille_police_webvues) + 2;

        mTwoPane = StyleHtml.verifieEcranEstTablette(this.getActivity());
        if (!mTwoPane) {
            this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        MeF = new MiseEnForme(getContext());

        barreRecherche = (SearchView) view.findViewById(R.id.barreRecherche);

        barreRecherche.setIconifiedByDefault(false);
        barreRecherche.setOnQueryTextListener(this);

        // barreRecherche.setOnQueryTextFocusChangeListener(this);
        if (StyleHtml.largeurFenetre(this.getActivity()) > 340) {
            barreRecherche.setSubmitButtonEnabled(true);

        }
        barreRecherche.setIconified(false);
        //  recherche.clearFocus(); //=========???? a tester pour faire disparaitre clavier

        if (mArrayAdapter == null) {
            //ds cet ordre sinon apres rotation de l'ecran et retour sur cette acti, plantage
            mArrayAdapter = new DictionaryAdapter(this.getContext(),MeF.listeDesEntrees(""),0);
            //this.getActivity().getSupportLoaderManager().initLoader(0,null,this);
            LoaderManager.getInstance(this).initLoader(0,null,this);

        }
        if (mFilter == null) {
            mFilter = new FilterQueryProv();
            mArrayAdapter.setFilterQueryProvider(mFilter);
        }

        listViewMots = (ListView) view.findViewById(R.id.TableListeMots);
        listViewMots.setAdapter(mArrayAdapter);

        listViewMots.setOnItemClickListener(this);
        listViewMots.setOnScrollListener(this);
        if (indexRecherche > 0) {ScrollToIndexRecherche(indexRecherche);}

        listeMotsFR = new ArrayList<ResultatFTS>();
        if (mListAdapterFR == null) {
            mListAdapterFR = new ListeEntreesAdapter(this.getContext(),R.layout.list_row_texte,listeMotsFR,taillePolice); //new ArrayAdapter(this,R.layout.list_row_texte,listeMotsFR);
        }

        listViewMotsFR = view.findViewById(R.id.TableListeMotsFR);
        listViewMotsFR.setOnItemClickListener(this);
        listViewMotsFR.setOnScrollListener(this);
        listViewMotsFR.setAdapter(mListAdapterFR);
        listViewMotsFR.setVisibility(View.INVISIBLE);

        boutonLangue = (Button) view.findViewById(R.id.boutonLangue);
        boutonLangue.setOnClickListener(this);


        boutonNewCarte = view.findViewById(R.id.boutonNewCarte);
        boutonNewCarte.setOnClickListener(this);
        boutonNewCarte.getBackground().setColorFilter(getResources().getColor(R.color.bleu), PorterDuff.Mode.SRC_ATOP);

        //getSupportActionBar().setTitle("");
        // getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        // getSupportActionBar().setCustomView(R.layout.actionbar);
        // getSupportActionBar().setDisplayShowHomeEnabled(true);

        //if (myWebView == null) {
            myWebView = (WebView) view.findViewById(R.id.webDico);
            prepareWebVue();
        //}


        if (!mTwoPane) {
            myWebView.setVisibility(View.INVISIBLE);
            barreRecherche.setOnClickListener(this);

        }
        boutonNewCarte.setVisibility(View.GONE);
        relLay = view.findViewById(R.id.horizontalLA);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
        void onFragmentInteraction(Uri uri);
    }

    void prepareWebVue() {
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        WebAppInterface2 wAppInt = new WebAppInterface2(this.getActivity());

        myWebView.addJavascriptInterface(wAppInt, "Android");
        // myWebView.setWebChromeClient(new WebChromeClient());

        myWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                final Uri uri = Uri.parse(url);
                return handleUri(uri);
            }
            private boolean handleUri(final Uri uri) {
                final String host = uri.getHost();
                final String scheme = uri.getScheme();
                // Based on some condition you need to determine if you are going to load the url
                // in your web view itself or in a browser.
                // You can use `host` or `scheme` or any part of the `uri` to decide.
                String str = "";
                try {
                    str = URLDecoder.decode(uri.toString(),"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String txt = "";
                int index1 = 0;
                //String url = host;
                String ttx = MeF.traiteUrlScheme(str, myWebView.getWidth(), taillePolice);
                // Log.d("LINK : ", url);

                if (!ttx.equals("")) {
                    myWebView.loadDataWithBaseURL(null, ttx, "text/html", "utf-8", null);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
            }

            @Override
            public void onLoadResource(WebView view, String url) {
            }
        });
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);
    }
    private class WebAppInterface2 {
        Context mContext;

        /** Instantiate the interface and set the context */
        WebAppInterface2(Context c) {
            mContext = c;
        }
        @JavascriptInterface
        public void showHTML(String html) {
            new AlertDialog.Builder(mContext).setTitle("HTML").setMessage(html)
                    .setPositiveButton(android.R.string.ok, null).setCancelable(false).create().show();
        }
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on Destroy ");
       // MeF.flex.fermeDBs();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "on start ");
        //InitDBTask idb = new InitDBTask();
        //idb.execute();

        MeF.flex.ouvreDBS();
        //mDicLoad.maMeF.flex.ouvreDBS();


    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "on stop ");
        MeF.flex.fermeDBs();
        //CloseDBTask cdb = new CloseDBTask();
        //cdb.execute();
        aStoppe = true;
    }


    @Override
    public void onPause() {
        super.onPause();
        MeF.flex.fermeDBs();
        //CloseDBTask cdb = new CloseDBTask();
        //cdb.execute();
        Log.d(TAG, "on pause ");
    }

    @Override
    public void onResume() {
        super.onResume();
        MeF.flex.ouvreDBS();
       // InitDBTask idb = new InitDBTask();
       // idb.execute();

        Log.d(TAG, "on resume ");
        //if (leTexte.length() > 0)
            //barreRecherche.setQuery(leTexte,true);

        boutonLangue.setText(langueRecherche);
        barreRecherche.clearFocus();

        if (texteHtmlCourant.length() > 0) {

            myWebView.loadDataWithBaseURL(null, texteHtmlCourant, "text/html", "utf-8", null);
           
            boutonNewCarte.setVisibility(View.VISIBLE);
        }
        else {
            boutonNewCarte.setVisibility(View.GONE);
        }

        if (mTwoPane) {
            if (leTexte.length() > 0) {
                // inutile car onQueryTextChange est appelee aussi qd fragment relance
                //boutonLangue.setText(langueRecherche);

            }
            if (langueRecherche.equals("Latin")) {
                listViewMotsFR.setVisibility(View.INVISIBLE);
                listViewMots.setVisibility(View.VISIBLE);
            }
            else {
                listViewMots.setVisibility(View.INVISIBLE);
                listViewMotsFR.setVisibility(View.VISIBLE);
                //boutonNewCarte.setVisibility(View.GONE);
            }
        }
        else {
            if (isDefVisible) {
                listViewMotsFR.setVisibility(View.INVISIBLE);
                listViewMots.setVisibility(View.INVISIBLE);
                boutonLangue.setVisibility(View.GONE);
                isDefVisible = true;
                myWebView.setVisibility(View.VISIBLE);
                //boutonLangue.setVisibility(View.INVISIBLE);
                boutonNewCarte.setVisibility(View.VISIBLE);
            }
            else {
                boutonLangue.setVisibility(View.VISIBLE);
                myWebView.setVisibility(View.INVISIBLE);
                if (langueRecherche.equals("Latin")) {
                    listViewMots.setVisibility(View.VISIBLE);
                    listViewMotsFR.setVisibility(View.INVISIBLE);
                }
                else {
                    listViewMotsFR.setVisibility(View.VISIBLE);
                    listViewMots.setVisibility(View.INVISIBLE);
                }
            }
        }

        aStoppe = false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!mTwoPane) {
            toucheSearchVue();
        }

    }

    void toucheSearchVue() {
        //Log.d(TAG,"touch search vue");
        if (!mTwoPane) {
            if (isDefVisible) {
                float toX;
                if (langueRecherche.equals("Latin")) {
                    toX = listViewMots.getWidth();
                }
                else {
                    toX = listViewMotsFR.getWidth();
                }

                Animation a = new TranslateAnimation(toX,0,0,0);
                a.setDuration(dureeAnimations);

                AnimationSet set = new AnimationSet(true);
                set.addAnimation(a);

                set.setFillEnabled(true);
                if (langueRecherche.equals("Latin")) {
                    listViewMots.startAnimation(set);
                }
                else {
                    listViewMotsFR.startAnimation(set);
                }
                //myWebView.startAnimation(set);

                set.setAnimationListener(new Animation.AnimationListener() {

                    public void onAnimationStart(Animation animation) {

                        if (langueRecherche.equals("Latin")) {
                            listViewMots.setVisibility(View.VISIBLE);
                            listViewMotsFR.setVisibility(View.INVISIBLE);
                        }
                        else {
                            listViewMotsFR.setVisibility(View.VISIBLE);
                            listViewMots.setVisibility(View.INVISIBLE);
                        }
                    }

                    public void onAnimationRepeat(Animation animation) {
                    }

                    public void onAnimationEnd(Animation animation) {
                        myWebView.setVisibility(View.INVISIBLE);
                        boutonNewCarte.setVisibility(View.GONE);
                        boutonLangue.setVisibility(View.VISIBLE);

                    }
                });

                isDefVisible = false;
            }
        }
        texteHtmlCourant = "";
    }


    @Override
    public void onClick(View v) {
        if (myTask != null) {
            myTask.cancel(true);
            myTask = null;
        }
        if (v == boutonLangue) {

            if (langueRecherche.equals("Latin")) {
                langueRecherche = "Francais";
                if (!mTwoPane) {
                    myWebView.setVisibility(View.INVISIBLE);
                }
                listViewMotsFR.setVisibility(View.VISIBLE);
                listViewMots.setVisibility(View.INVISIBLE);

            }
            else {
                langueRecherche = "Latin";
                if (!mTwoPane) {
                    myWebView.setVisibility(View.INVISIBLE);
                }
                listViewMots.setVisibility(View.VISIBLE);
                listViewMotsFR.setVisibility(View.INVISIBLE);

            }
            boutonLangue.setText(langueRecherche);
            texteHtmlCourant = "";

        }
        if (v == boutonNewCarte) {
            createDial();
        }
        if (v == barreRecherche) {
            final float avx = listViewMots.getX();
            Log.d(TAG,"posx mtnt : " + avx + listViewMots.getVisibility());
            if (!mTwoPane) {
                toucheSearchVue();
            }
            texteHtmlCourant = "";
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String texte = "";
        if (myTask != null) {
            myTask.cancel(true);
            myTask = null;
        }

        barreRecherche.clearFocus();
        if (langueRecherche.equals("Latin")) {
            String entree = mArrayAdapter.getEntreeListe(position);
            entreeCourante = entree;
            largeurFenetre = myWebView.getWidth();

        /*
        if (Build.VERSION.SDK_INT >= 19) {
            // chromium, enable hardware acceleration
            myWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            // older android version, disable hardware acceleration
            myWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        */
            //Log.d(TAG,"width webvue : " + String.valueOf(myWebView.getWidth()));
            texte = MeF.chargeDef_et_Flexions(entree,largeurFenetre,taillePolice);
            texteHtmlCourant = texte;
        }
        else {
            ResultatFTS entree = mListAdapterFR.getItem(position); //listeMotsFR.get(position); -> non
            // a cause AsyncTask, liste plus utilisee ...
            largeurFenetre = listViewMotsFR.getWidth();
            if (entree != null) {
                EnregDico enrT = MeF.flex.db.rechercheEntreeDicoParMotOrigDico(entree.mot,entree.dico);
                resultatFTSCourant = entree.copie();
                resultatFTSCourant.POS = enrT.POS;
              //  Log.d(TAG,"entree : " + enrT.mot + " pos " + enrT.POS);
                texte = MeF.chargeMot_Definition(entree,largeurFenetre,taillePolice);
                texteHtmlCourant = texte;
                //ActivityCompat.invalidateOptionsMenu(this.getActivity());
                Objects.requireNonNull(getActivity()).invalidateOptionsMenu();
            }



            //   invalidateOptionsMenu();
        }
        myWebView.loadDataWithBaseURL(null, texte, "text/html", "utf-8", null);
        lanceAnimationMontreWebVue(texte);

    }

    void lanceAnimationMontreWebVue(String stringHtml) {

        if (mTwoPane) {
            myWebView.setVisibility(View.VISIBLE);

            boutonNewCarte.setVisibility(View.VISIBLE);
        }
        else {

            isDefVisible = true;
            float toX;
            if (langueRecherche.equals("Latin")){
                toX = listViewMots.getWidth();
            }
            else {
                toX = listViewMotsFR.getWidth();
            }
            Animation a = new TranslateAnimation(0,toX,0,0);
            a.setDuration(dureeAnimations);

            AnimationSet set = new AnimationSet(true);
            set.addAnimation(a);
            set.setFillEnabled(true); // ou setFillBefore mais PAS setFillAfter ...

            set.setAnimationListener(new Animation.AnimationListener() {

                public void onAnimationStart(Animation animation) { }

                public void onAnimationRepeat(Animation animation) { }

                public void onAnimationEnd(Animation animation) {
                    myWebView.setVisibility(View.VISIBLE);
                    boutonNewCarte.setVisibility(View.VISIBLE);
                    if (langueRecherche.equals("Latin")) {
                        listViewMots.setVisibility(View.INVISIBLE);
                    }
                    else {
                        listViewMotsFR.setVisibility(View.INVISIBLE);
                    }
                }
            });


            if (langueRecherche.equals("Latin")) {
                listViewMotsFR.setVisibility(View.INVISIBLE);
                listViewMots.startAnimation(set);
            }
            else {
                listViewMots.setVisibility(View.INVISIBLE);
                listViewMotsFR.startAnimation(set);
            }
            boutonLangue.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!langueRecherche.equals("Latin")) {
            return false;
        }

        leTexte = query;
        entreeCourante = "";

        ArrayList resultats = MeF.flex.rechercheForme("?" + query,false,"");
        ArrayList resultatsPropres = MeF.trieListeResultats(resultats);
        resultatsCourants = new ArrayList();
        if (resultats.size() > 0) {
            resultatsCourants.addAll(resultats); // car resultats propres a POS mis en forme
        }

        //ArrayList resultats = MeF.analyseForme("?" + query);
        String texte = MeF.rechercheHtml(query,resultatsPropres,myWebView.getWidth(), taillePolice);


//Log.d("===","rech : " + texte);
        if (mTwoPane) {
            myWebView.loadDataWithBaseURL(null, texte, "text/html", "utf-8", null);
            texteHtmlCourant = texte;
        }
        else {
            myWebView.loadDataWithBaseURL(null, texte, "text/html", "utf-8", null);
            texteHtmlCourant = texte;
            lanceAnimationMontreWebVue(texte);
            isDefVisible = true;
        }
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (langueRecherche.equals("Latin")) {
            if (!mTwoPane) {
                if (myWebView.getVisibility() == View.VISIBLE) {
                    listViewMots.setVisibility(View.VISIBLE);
                    myWebView.setVisibility(View.INVISIBLE);
                    boutonNewCarte.setVisibility(View.GONE);
                    listViewMotsFR.setVisibility(View.INVISIBLE);
                    boutonLangue.setVisibility(View.VISIBLE);
                }
            }
            //adapter.getFilter().filter(cs.toString());
            mArrayAdapter.getFilter().filter(newText);

            leTexte = newText;
        }
        else {
            if (!mTwoPane) {
                if (myWebView.getVisibility() == View.VISIBLE) {
                    listViewMotsFR.setVisibility(View.VISIBLE);
                    listViewMots.setVisibility(View.INVISIBLE);
                    myWebView.setVisibility(View.INVISIBLE);
                    boutonNewCarte.setVisibility(View.GONE);
                    boutonLangue.setVisibility(View.VISIBLE);
                }
            }
            if (newText.length() > 2) {

                if (!newText.endsWith(" ")) {
                    listeMotsFR.clear();
                    listeMotsFR = MeF.rechercheEnregParDefinition(newText);

                    mListAdapterFR.clear();
                    //Log.d(TAG,"qtext ch : " + newText + " - liste size : " + listeMotsFR.size());
                    if (listeMotsFR.size() > 0) {
                        if (myTask != null) {
                            myTask.cancel(true);
                            myTask = null;
                        }

                        myTask = new AddStringTask();
                        myTask.execute(listeMotsFR);
                    }
                }

                //mListAdapterFR.addAll(listeMotsFR);
                //mListAdapterFR.notifyDataSetChanged();
            }

        }
        if (!aStoppe) {
            isDefVisible = false;
        }
        return false;
    }

    public void ScrollToIndexRecherche(int indexRecherche)
    {
        //   Log.d(TAG, "recherche : " + indexRecherche + " " + listeVerbes.get(indexRecherche).toString());
        listViewMots.setSelection(indexRecherche);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    // ============================ Dictionary Adapter =========================
    // loader callbacks interface
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        mDicLoad = new DictionaryLoader(this.getContext(), MeF);
        Log.d(TAG,"onCreate Loader");
        return mDicLoad;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        Log.d(TAG,"onLoad finished");

        if (data.isClosed()) {
            Log.d(TAG,"data closed ?");
            Cursor c = MeF.listeDesEntrees("");
            mArrayAdapter.swapCursor(c);
        }
        else {
            mArrayAdapter.swapCursor(data);
        }

        //mArrayAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        Log.d(TAG,"onLoader reset");
        mArrayAdapter.swapCursor(null);
    }


    @Override
    public boolean onClose() {
        Log.d(TAG,"on Close");
        return false;
    }

    private class DictionaryAdapter extends CursorAdapter {
        public DictionaryAdapter(Context context, Cursor c, int flags) {
            super(context,c,flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
            return LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1,viewGroup,false);
        }

        @Override
        public Cursor swapCursor(Cursor newCursor) {
            //Log.d(TAG,"swap cursor");

            return super.swapCursor(newCursor);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            TextView textVue = (TextView) view.findViewById(android.R.id.text1);
            //textVue.setText(cursor.getString(getCursor().getColumnIndex("word")));
            textVue.setTextColor(Color.BLACK);
            String mot = cursor.getString(2);
            String mt = MeF.metEnformeMotDsListe(mot);
            textVue.setText(mt);
            //SpannableString st = new SpannableString(""); //MeF.metEnformeMotDsListe(motYo,POS,aspect,accent,meaning,taillePolice);
            //textVue.setText(st);

        }

        String getEntreeListe(int position) {
            String res = "";
            if (getCursor().moveToPosition(position)) {
                res = getCursor().getString(2);
            }
            return res;
        }
    }

    private class FilterQueryProv implements FilterQueryProvider {

        @Override
        public Cursor runQuery(CharSequence constraint) {
            String constr = constraint.toString();
            return MeF.listeDesEntrees(constr);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        //setContentView(R.layout.main);
        Log.d("TABULA","on config changed");
        //InitializeUI();
    }

    private  class InitDBTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            MeF.flex.ouvreDBS();
            return null;
        }
    }
    private  class CloseDBTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            MeF.flex.fermeDBs();
            return null;
        }
    }

    private class AddStringTask extends AsyncTask<List<ResultatFTS>, ResultatFTS, List<ResultatFTS>> {
        private ProgressDialog dialog;
        private ProgressBar progBar;

        @Override
        protected void onPreExecute() {
            //Toast.makeText(getApplicationContext(), "init parlos ! ",
            //      Toast.LENGTH_SHORT).show();
/*
            dialog = new ProgressDialog(getActivity());//= ProgressDialog.show(getApplicationContext(), "Please wait..", "Doing stuff..", true);
            dialog.setMessage("Installation du dictionnaire ...");
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.show();
*/
            progBar = new ProgressBar(getActivity(), null, android.R.attr.progressBarStyleSmall);
            progBar.setIndeterminate(true);

            progBar.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(80, 80);
            progBar.setLayoutParams(params );
            //LinearLayout test = new LinearLayout(getActivity());
            //test.addView(progBar);
            relLay.addView(progBar);
        }

        @Override
        protected List<ResultatFTS> doInBackground(List<ResultatFTS>... params) {
            List<ResultatFTS> results = new ArrayList<>();
            if (isCancelled()) {
               // progBar.setVisibility(View.INVISIBLE);
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
                mListAdapterFR.add(v);

            }
            mListAdapterFR.notifyDataSetChanged();
            //progBar.setVisibility(View.INVISIBLE);
            // ((ArrayAdapter<String>)getListAdapter()).add(item[0]);
        }

        @Override
        protected void onPostExecute(List<ResultatFTS> resultats) {

            progBar.setVisibility(View.INVISIBLE);
            mListAdapterFR.clear();
            mListAdapterFR.addAll(resultats);
            mListAdapterFR.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled(List<ResultatFTS> resultats) {

            Log.d("TABULA As task","on cancelled 1");
            progBar.setVisibility(View.INVISIBLE);
        }
        @Override
        protected void onCancelled() {

            Log.d("TABULA As task","on cancelled 2");
            progBar.setVisibility(View.INVISIBLE);
        }
    }

    public void createDial() {
        DialogueAjouteCarte dial = new DialogueAjouteCarte(getContext(),getActivity(),MeF);

        if (langueRecherche.equals("Latin")) {
            if (!entreeCourante.isEmpty()) {
                dial.createDial(entreeCourante);
            }
            else {
                if (resultatsCourants.size() > 0) {
                    dial.createDialPourResultatsRecherches(resultatsCourants);
                }
            }

        }
        else {
            dial.createDialPourResultatFTS(resultatFTSCourant);
        }



    }



}




