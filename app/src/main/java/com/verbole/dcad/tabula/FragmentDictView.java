package com.verbole.dcad.tabula;

import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class FragmentDictView extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener, SearchView.OnQueryTextListener,
        AbsListView.OnScrollListener, SearchView.OnCloseListener,
        View.OnFocusChangeListener {
    static final String TAG = "FragDictView ";

    private FragmentDictViewModel mViewModel;

    ListView listViewMots;


    ListView listViewMotsFR;
    ListeEntreesAdapter mListAdapterFR;

    static String leTexte = "";
    static int indexRecherche = 0;
    SearchView barreRecherche;
    WebView myWebView;

    Button boutonLangue;
    String langueRecherche = "Latin";

    // tt cela devrait etre mis ds data model ou ds MiseEnForme ...
    EnregDico entreeCourante = new EnregDico();

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

    DictAdapter mListMotsAdapter;

    private OnFragmentInteractionListener mListener;


    public FragmentDictView() {
        // Required empty public constructor

    }

/*
    public static FragmentDictView newInstance(String param1, String param2) {
        FragmentDictView fragment = new FragmentDictView();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
*/
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(ActivitePrincipale2.TAG,TAG + "on createView");

        View view =  inflater.inflate(R.layout.activity_dictionnaire, container, false);

        mViewModel = ViewModelProviders.of(getActivity()).get(FragmentDictViewModel.class);

        listViewMots = view.findViewById(R.id.TableListeMots);

        mViewModel.getEntrees().observe(getViewLifecycleOwner(), listEntrees -> {

            DictAdapter adapter = new DictAdapter(getContext(),android.R.layout.simple_list_item_1,listEntrees);
            listViewMots.setAdapter(adapter);
        });

        GestionSettings gs = new GestionSettings(getContext());
        gs.setDefault();
        gs.setTaillePolice(14);
        //taillePolice = gs.getTaillePolice();

        mTwoPane = StyleHtml.verifieEcranEstTablette(this.getActivity());

        if (!mTwoPane) {
            this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        }
        else {
            this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }


        barreRecherche = (SearchView) view.findViewById(R.id.barreRecherche);

        barreRecherche.setIconifiedByDefault(false);
        barreRecherche.setOnQueryTextListener(this);

        // barreRecherche.setOnQueryTextFocusChangeListener(this);

        //  recherche.clearFocus(); //=========???? a tester pour faire disparaitre clavier

        listViewMots.setOnItemClickListener(this);
        listViewMots.setOnScrollListener(this);

        //if (myWebView == null) {
        myWebView = (WebView) view.findViewById(R.id.webDico);
        prepareWebVue();
        //}

        if (indexRecherche > 0) {ScrollToIndexRecherche(indexRecherche);}


        listViewMots.setVisibility(View.VISIBLE);


        listViewMotsFR = view.findViewById(R.id.TableListeMotsFR);
        listViewMotsFR.setOnItemClickListener(this);
        listViewMotsFR.setOnScrollListener(this);

        listViewMotsFR.setVisibility(View.INVISIBLE);


        boutonLangue = (Button) view.findViewById(R.id.boutonLangue);
        boutonLangue.setOnClickListener(this);


        boutonNewCarte = view.findViewById(R.id.boutonNewCarte);
        boutonNewCarte.setOnClickListener(this);
        boutonNewCarte.getBackground().setColorFilter(getResources().getColor(R.color.bleu), PorterDuff.Mode.SRC_ATOP);

        if (!mTwoPane) {
            myWebView.setVisibility(View.INVISIBLE);
            barreRecherche.setOnClickListener(this);

        }
        boutonNewCarte.setVisibility(View.GONE);
        isDefVisible = false;

        mViewModel.getHtmlStringResult().observe(getViewLifecycleOwner(),htmlString -> {
            myWebView.loadDataWithBaseURL(null, htmlString, "text/html", "utf-8", null);

        });


        mViewModel.getEntreesFTS().observe(getViewLifecycleOwner(), listEntreesFTS -> {
            ListeEntreesAdapter adapter = new ListeEntreesAdapter(getContext(),R.layout.list_row_texte,listEntreesFTS,taillePolice);
            listViewMotsFR.setAdapter(adapter);

        });

        mViewModel.getFTSHtmlStringResult().observe(getViewLifecycleOwner(),htmlString -> {
            myWebView.loadDataWithBaseURL(null, htmlString, "text/html", "utf-8", null);

        });

        mViewModel.getHtmlAnalyseStringResult().observe(getViewLifecycleOwner(),htmlString -> {
            myWebView.loadDataWithBaseURL(null, htmlString, "text/html", "utf-8", null);

        });

        //Log.d(TAG,"web vuew : " + myWebView.getWidth() + " - screen " + StyleHtml.largeurFenetre(getActivity()));
        if (StyleHtml.largeurFenetre(getActivity()) > 340) {
            taillePolice = getResources().getInteger(R.integer.taille_police_webvues) + 2;
        }
        else {
            taillePolice = getResources().getInteger(R.integer.taille_police_webvues) - 1;
        }
        if (StyleHtml.largeurFenetre(this.getActivity()) > 340) {
            barreRecherche.setSubmitButtonEnabled(true);
        }

        mViewModel.taillePolice = taillePolice;

        //debugSituationVues("fin createView");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //debugSituationVues("on activity created");





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
                String ttx = mViewModel.MeF.traiteUrlScheme(str, myWebView.getWidth(), taillePolice);
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
        Log.d(ActivitePrincipale2.TAG, TAG + "on Destroy ");
        // MeF.flex.fermeDBs();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(ActivitePrincipale2.TAG, TAG + "on start ");
        //InitDBTask idb = new InitDBTask();
        //idb.execute();

       // mViewModel.MeF.flex.ouvreDBS();
        //mDicLoad.maMeF.flex.ouvreDBS();

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(ActivitePrincipale2.TAG, TAG + "on stop ");
       // mViewModel.MeF.flex.fermeDBs();
        //CloseDBTask cdb = new CloseDBTask();
        //cdb.execute();
        aStoppe = true;

    }


    @Override
    public void onPause() {
        super.onPause();
      //  mViewModel.MeF.flex.fermeDBs();
        //CloseDBTask cdb = new CloseDBTask();
        //cdb.execute();
        Log.d(ActivitePrincipale2.TAG, TAG + "on pause ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(ActivitePrincipale2.TAG, TAG + "on resume ");
      //  mViewModel.MeF.flex.ouvreDBS();

        //debugSituationVues("debut on resume");

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
            //    boutonLangue.setVisibility(View.GONE);
                myWebView.setVisibility(View.VISIBLE);

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

        //debugSituationVues("fin on resume");
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

        if (v == boutonLangue) {
            boutonNewCarte.setVisibility(View.GONE);
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
            //Log.d(TAG,"posx mtnt : " + avx + listViewMots.getVisibility());
            revient();
        }


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String texte = "";


        barreRecherche.clearFocus();
        mViewModel.largeurFenetre = myWebView.getWidth();
        if (langueRecherche.equals("Latin")) {
            isDefVisible = true;
            EnregDico EL = mViewModel.getEntree(position); //mArrayAdapter.getEntreeListe(position);
            entreeCourante = EL.copie();
            largeurFenetre = myWebView.getWidth();

            mViewModel.setEntreeCourante(EL);
            lanceAnimationMontreWebVue(true);

            texteHtmlCourant = texte;
        }
        else {
            ResultatFTS entree = mViewModel.getEntreeFTS(position);
            // a cause AsyncTask, liste plus utilisee ...
            largeurFenetre = listViewMotsFR.getWidth();
            if (entree != null) {
                isDefVisible = true;
                mViewModel.setEntreeCouranteFTS(entree);

                resultatFTSCourant = entree.copie();
                resultatFTSCourant.POS = entree.POS;
                lanceAnimationMontreWebVue(true);
            }
        }
    }

    void lanceAnimationMontreWebVue(boolean montreBoutonNewCarte) {
//Log.d(TAG,"montre webvue");
        if (mTwoPane) {
            myWebView.setVisibility(View.VISIBLE);
            if (montreBoutonNewCarte) {
                boutonNewCarte.setVisibility(View.VISIBLE);
            }

        }
        else {


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
                    if (montreBoutonNewCarte) {
                        boutonNewCarte.setVisibility(View.VISIBLE);
                    }
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
            if (StyleHtml.verifieEcranInferieur400dp(getActivity())) {
                boutonLangue.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        if (!langueRecherche.equals("Latin")) {
            return false;
        }
        mViewModel.largeurFenetre = myWebView.getWidth();
        leTexte = query;

        mViewModel.setSearchTextAnalyse(query);
        mViewModel.getHtmlAnalyseStringResult();
        //mViewModel.getSearchTextAnalyse();
        lanceAnimationMontreWebVue(false);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (!aStoppe) {
            isDefVisible = false;
        }
        //Log.d(TAG,"query text change - lvm " + listViewMots.getHeight() + " - visi " + listViewMots.getVisibility());

        if (langueRecherche.equals("Latin")) {
            boutonNewCarte.setVisibility(View.GONE);
            if (!mTwoPane) {
                if (myWebView.getVisibility() == View.VISIBLE) {
                    listViewMots.setVisibility(View.VISIBLE);
                    myWebView.setVisibility(View.INVISIBLE);

                    listViewMotsFR.setVisibility(View.INVISIBLE);
                    boutonLangue.setVisibility(View.VISIBLE);
                }
            }
            mViewModel.setSearchText(newText);
          //  mViewModel.getEntrees();


            leTexte = newText;
        }
        else {
            boutonNewCarte.setVisibility(View.GONE);
            if (!mTwoPane) {
                if (myWebView.getVisibility() == View.VISIBLE) {
                    listViewMotsFR.setVisibility(View.VISIBLE);
                    listViewMots.setVisibility(View.INVISIBLE);
                    myWebView.setVisibility(View.INVISIBLE);

                    boutonLangue.setVisibility(View.VISIBLE);
                }
            }
            if (newText.length() > 2) {

                if (!newText.endsWith(" ")) {

                    mViewModel.setSearchTextFTS(newText);
                    mViewModel.getEntreesFTS();
                }

            }

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

    @Override
    public boolean onClose() {
        Log.d(ActivitePrincipale2.TAG,"on Close");
        return false;
    }



    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        super.onConfigurationChanged(newConfig);
        //setContentView(R.layout.main);
        Log.d(ActivitePrincipale2.TAG,"on config changed");
        //InitializeUI();
    }

    private  class InitDBTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            mViewModel.MeF.flex.ouvreDBS();
            return null;
        }
    }
    private  class CloseDBTask extends AsyncTask<Void,Void,Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            mViewModel.MeF.flex.fermeDBs();
            return null;
        }
    }

    public void createDial() {
        DialogueAjouteCarte dial = new DialogueAjouteCarte(getContext(),getActivity(),mViewModel.MeF);

        if (langueRecherche.equals("Latin")) {
            if (!entreeCourante.mot.isEmpty()) {
                dial.createDial(entreeCourante.mot);
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

    private class DictAdapter extends ArrayAdapter<EnregDico> {

       // List<EnregDico> mListeEnreg;

        public DictAdapter(@NonNull Context context, int resource) {
            super(context, resource);
        }
        public DictAdapter(@NonNull Context context, int resource, @NonNull List<EnregDico> objects) {
            super(context, resource, objects);
            Log.d(ActivitePrincipale2.TAG,TAG + "new DictAdapter nb : " + objects.size());
           // mListeEnreg = objects;
            notifyDataSetChanged();

        }

        @Override
        public @NonNull
        View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            View v = convertView;
            if (v == null) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.list_row_texte, null);
            }

            TextView textVue = (TextView) v.findViewById(R.id.listRowVue);
           // EnregDico obj = mListeEnreg.get(position); //getItem(position);
            EnregDico obj = getItem(position);
            String mot = obj.mot;
            textVue.setText(mot);

            return v;
        }

        void setList(List<EnregDico> liste) {
            //mListeEnreg = liste;
            notifyDataSetChanged();
        }

    }

    void debugSituationVues(String fonction) {
        //Log.d(TAG,  fonction + " : tablette " + mTwoPane + " - defvisible " +  isDefVisible + " langue " + langueRecherche);
        boolean b1 = listViewMots.getVisibility() == View.VISIBLE;
        boolean b2 = listViewMotsFR.getVisibility() == View.VISIBLE;
        boolean b3 = myWebView.getVisibility() == View.VISIBLE;
        //Log.d(TAG,  fonction + " : listmots " + b1 + " - listmotsFR " +  b2 + " webvue " + b3);

        DictAdapter adapter = (DictAdapter) listViewMots.getAdapter();
        if (adapter != null) {
            EnregDico yo = (EnregDico) adapter.getItem(0);
            if (yo != null) {
                Log.d(TAG,fonction + " - " + yo.mot);
            }
        }
    }

    boolean revient() {
        Log.d(ActivitePrincipale2.TAG,TAG + "revient ??");
        if (!mTwoPane) {
            boolean rep = true;
            if (myWebView.getVisibility() == View.VISIBLE) {
                rep = false;
            }
            toucheSearchVue();
            return rep;
        }
        boutonLangue.setVisibility(View.VISIBLE);
        texteHtmlCourant = "";
        return true;
    }


}
