package com.verbole.dcad.tabula;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;


public class FragmentCartes extends Fragment implements View.OnClickListener {
    static final String TAG = "fragmentCartes ";
    ListView listViewListes;
    ListView listViewCartes;
    WebView webVueCarte;
    Context mContext;
    MiseEnForme MeF;
    FlashCardsDB FCdb;
    String txtHtmlCarte = "";

    //ArrayAdapter aradListes;
    MyCustomAdapterListes aradListes;


    LanceFlashCardListener lanceTestListener;

    MyCustomAdapterCartes aradCartes;
    ArrayList<Carte> listeCartes;

    long dureeAnimations = 600;
    int taillePolice = 14;
    float largeurFenetre = 0;
    boolean mTwoPane = true;
    String nomListeCourant = "";

    RelativeLayout frLay;
    LinearLayout frLayGen;
    FrameLayout frLayFC;

    Button boutonEdit;
    TextView titreTexte;
    ImageView boutonRevient;
    Button boutonSupprimer;
    Toolbar mToolBar;

    //boolean isEdit = false;

    public enum ETAT {LISTELISTES,LISTELISTESEDIT,LISTECARTES, LISTECARTESEDIT,CARTE,CARTEEDIT,FCTEST}

    ETAT etat = ETAT.LISTELISTES;

    @Override
    public Context getContext() {
        return super.getContext();
    }


    private OnFragmentInteractionListener mListener;

    public FragmentCartes() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_cartes, container, false);
       // Log.d(ActivitePrincipale2.TAG, TAG + "on create view ");
        mContext = getContext();

        MeF = new MiseEnForme(mContext);
        FCdb = FlashCardsDB.getInstance(mContext); //new FlashCardsDB(mContext);

        listViewListes = view.findViewById(R.id.listeListes);
        listViewCartes = view.findViewById(R.id.listeCartes);

        FCdb.openDataBase();

       // if (aradListes == null) { surtout pas !!!! fout le bordel
            aradListes = new MyCustomAdapterListes(this.getContext(),R.layout.liste_listes_cartes);
      //  }

        listViewListes.setAdapter(aradListes);
        listViewListes.setVisibility(View.VISIBLE);
        //listViewListes.setOnItemClickListener(this);

        if (listeCartes == null) {
            listeCartes = new ArrayList<>();
        }

        //if (aradCartes == null) {
            aradCartes = new MyCustomAdapterCartes(this.getContext(),R.layout.liste_listes_cartes,listeCartes);
        //}

        listViewCartes.setAdapter(aradCartes);
        listViewCartes.setVisibility(View.INVISIBLE);
        //listViewCartes.setOnItemClickListener(this);

        webVueCarte = view.findViewById(R.id.webViewCarte);
        webVueCarte.getSettings().setBuiltInZoomControls(true);
        webVueCarte.getSettings().setDisplayZoomControls(false);
        webVueCarte.getSettings().setJavaScriptEnabled(true);


     //   webVueCarte.loadDataWithBaseURL("", "", "text/html", "utf-8", "");
        webVueCarte.setVisibility(View.INVISIBLE);
        prepareWebVue();

        mToolBar = view.findViewById(R.id.toolbar_fragment_cartes);

        boutonEdit = view.findViewById(R.id.action_edit);
        boutonEdit.setOnClickListener(this);

        boutonRevient = view.findViewById(R.id.action_revient);
        boutonRevient.setOnClickListener(this);
        //boutonRevient.setVisibility(View.INVISIBLE);
        boutonRevient.setEnabled(false);

        boutonSupprimer = view.findViewById(R.id.boutonSupprimer);
        boutonSupprimer.setOnClickListener(this);
        boutonSupprimer.setVisibility(View.GONE);

        int[][] states = new int[][] {
                new int[] { android.R.attr.state_enabled}, // enabled
                new int[] {-android.R.attr.state_enabled}, // disabled
                new int[] {-android.R.attr.state_checked}, // unchecked
                new int[] { android.R.attr.state_pressed}  // pressed
        };

        int[] colors = new int[] {
                Color.BLACK,
                Color.GRAY,
                Color.GRAY,
                Color.BLACK
        };

        ColorStateList myList = new ColorStateList(states, colors);

        boutonSupprimer.setTextColor(myList);
       // boutonRevient.setTextColor(myList);

        frLay = view.findViewById(R.id.frameLayCartes);
        frLayGen = view.findViewById(R.id.LinLayFragCartes);
        frLayFC = view.findViewById(R.id.FrLayOutFrFC);
        frLayFC.setVisibility(View.GONE);

        titreTexte = view.findViewById(R.id.titreCarte);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
*/
    @Override
    public void onDetach() {
        super.onDetach();
       // Log.d(ActivitePrincipale2.TAG, TAG + "on Detach ");
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  Log.d(ActivitePrincipale2.TAG, TAG + "on Destroy ");
        //MeF.flex.fermeDBs();
        FCdb.close();
    }

    @Override
    public void onStart() {
        super.onStart();
       // Log.d(ActivitePrincipale2.TAG, TAG + "on start ");
    }

    @Override
    public void onStop() {
        super.onStop();
       // Log.d(ActivitePrincipale2.TAG, TAG + "on stop ");
    }

    @Override
    public void onPause() {
        super.onPause();
        //MeF.flex.fermeDBs();
        FCdb.close();
      //  Log.d(ActivitePrincipale2.TAG, TAG + "on pause ");
    }

    @Override
    public void onResume() {
        super.onResume();

        //MeF.flex.ouvreDBS();
        FCdb.openDataBase();

        aradListes.miseAJourListes();

       // Log.d(ActivitePrincipale2.TAG, TAG + "on resume " + etat + " " + listViewListes.getHeight());
/*
        if (etat == ETAT.FCTEST) {
            frLay.setVisibility(View.GONE);
            mToolBar.setVisibility(View.GONE);
            frLayFC.setVisibility(View.VISIBLE);

            FragmentManager fm = recupereFragmentManager();
            Fragment_FlashCard ffc = (Fragment_FlashCard) fm.findFragmentByTag("fcTestTag");
            FragmentTransaction ft = fm.beginTransaction();

            //   ft.replace(R.id.FrLayOutFrFC, ffc, "fcTestTag");
            ft.show(ffc);
            ft.addToBackStack(null);
            ft.commit();
        }
*/
        if (etat == ETAT.LISTELISTES || etat == ETAT.LISTELISTESEDIT) {
            listViewListes.setVisibility(View.VISIBLE);
            listViewCartes.setVisibility(View.VISIBLE);
            webVueCarte.setVisibility(View.GONE);
            boutonSupprimer.setVisibility(View.GONE);
            boutonEdit.setText("Editer");
            aradListes.desactivateAll();
            etat = ETAT.LISTELISTES;
            boutonRevient.setEnabled(false);
        }
        if (etat == ETAT.LISTECARTES || etat == ETAT.LISTECARTESEDIT) {
            listViewListes.setVisibility(View.INVISIBLE);

            listViewCartes.setVisibility(View.VISIBLE);
            boutonSupprimer.setVisibility(View.GONE);
            boutonEdit.setVisibility(View.VISIBLE);
            boutonEdit.setText("Editer");
            aradCartes.miseAJourListeParNom(nomListeCourant);
            aradCartes.desactivateAll();
            etat = ETAT.LISTECARTES;
            boutonRevient.setEnabled(true);
        }
        if (etat == ETAT.CARTE || etat == ETAT.CARTEEDIT) {
            webVueCarte.loadDataWithBaseURL(null, txtHtmlCarte, "text/html", "utf-8", null);
            listViewCartes.setVisibility(View.INVISIBLE);
            aradCartes.miseAJourListeParNom(nomListeCourant);
            webVueCarte.setVisibility(View.VISIBLE);
            boutonRevient.setEnabled(true);
            listViewListes.setVisibility(View.INVISIBLE);

            boutonEdit.setVisibility(View.INVISIBLE);
            boutonEdit.setText("Editer");
            boutonSupprimer.setVisibility(View.GONE);
            etat = ETAT.CARTE;
        }
    }
/*
    FragmentManager recupereFragmentManager() {
        return getActivity().getSupportFragmentManager();
        //getChildFragmentManager(); //.beginTransaction();
    }
*/
    boolean revient() {
        /*
        if (etat == ETAT.FCTEST) {
            Log.d(ActivitePrincipale2.TAG,TAG + "test FC ?");

            FragmentManager ft = recupereFragmentManager();
            Fragment_FlashCard ffc = (Fragment_FlashCard) ft.findFragmentByTag("fcTestTag");
            if (ffc != null) {
                ffc.revient();
            }
            etat = ETAT.LISTELISTES;
        }
*/
        if (etat == ETAT.LISTECARTES || etat == ETAT.LISTECARTESEDIT) {
            ETAT anc = etat;
            etat = ETAT.LISTELISTES;
            passageEtat(anc,etat);

            int nbListes = aradListes.list.size();//listeListes.size();
            aradListes.miseAJourListes();
            for (int i = 0; i < nbListes; i++) {
                updateBoutons(i);
            }
            return false;
        }

        if (etat == ETAT.CARTE || etat == ETAT.CARTEEDIT) {
            ETAT anc = etat;
            etat = ETAT.LISTECARTES;
            passageEtat(anc,etat);

            int nbListes = listeCartes.size();
            for (int i = 0; i < nbListes; i++) {
                updateBoutons(i);
            }
            return false;
        }
        return true;
    }



    @Override
    public void onClick(View v) {
        //Log.d(ActivitePrincipale2.TAG, TAG + "on click " + etat + " " + listViewListes.getHeight() + " " + listViewListes.getWidth());
        if (v == boutonRevient) {
            revient();
            aradCartes.clearListeEliminations();
            aradListes.clearListeEliminations();
        }
        if (v == boutonEdit) {
            aradCartes.clearListeEliminations();
            aradListes.clearListeEliminations();
            if (etat == ETAT.LISTECARTES || etat == ETAT.LISTECARTESEDIT) {
                ETAT anc = etat;
                if (anc == ETAT.LISTECARTES) {
                    etat = ETAT.LISTECARTESEDIT;
                }
                if (anc == ETAT.LISTECARTESEDIT) {
                    etat = ETAT.LISTECARTES;
                }

                passageEtat(anc,etat);

                int nbListes = listeCartes.size();
                Log.d(ActivitePrincipale2.TAG,TAG + "edit : " + nbListes);
                for (int i = 0; i < nbListes; i++) {
                    updateBoutons(i);
                }
            }

            if (etat == ETAT.LISTELISTES || etat == ETAT.LISTELISTESEDIT) {
                ETAT anc = etat;
                if (anc == ETAT.LISTELISTES) {
                    etat = ETAT.LISTELISTESEDIT;
                }
                if (anc == ETAT.LISTELISTESEDIT) {

                    etat = ETAT.LISTELISTES;
                }
                aradListes.notifyDataSetChanged();
                passageEtat(anc,etat);

                int nbListes = aradListes.list.size();//listeListes.size();
                for (int i = 0; i < nbListes; i++) {
                    updateBoutons(i);
                }
            }
        }
        if (v == boutonSupprimer) {
            if (etat == ETAT.LISTELISTESEDIT) {
                aradListes.supprime();
                aradListes.desactivateAll();
            }
            if (etat == ETAT.LISTECARTESEDIT) {
                aradCartes.supprime();
                aradCartes.desactivateAll();
            }
        }

    }

    void passageEtat(ETAT ancien, ETAT nouveau) {

        if (ancien == ETAT.LISTELISTES) {
            if (nouveau == ETAT.LISTELISTESEDIT) {
                boutonSupprimer.setVisibility(View.VISIBLE);
                boutonSupprimer.setActivated(false);
                boutonSupprimer.setEnabled(false);
                boutonEdit.setText("Fermer");
            }
            //if (nouveau == ETAT.LISTECARTES) { }
        }
        if (ancien == ETAT.LISTELISTESEDIT) {
            if (nouveau == ETAT.LISTELISTES) {
                boutonSupprimer.setVisibility(View.GONE);
                boutonEdit.setText("Editer");
                aradListes.desactivateAll();
            }
            if (nouveau == ETAT.LISTECARTES) {
                animationMontreListeCartes();
            }
        }
        if (ancien == ETAT.LISTECARTES) {
            if (nouveau == ETAT.LISTELISTES) {
                animationListeListesRevient();
            }
            if (nouveau == ETAT.LISTECARTESEDIT) {
                boutonSupprimer.setVisibility(View.VISIBLE);

                boutonSupprimer.setEnabled(false);
                boutonEdit.setText("Fermer");
            }
            if (nouveau == ETAT.CARTE) {
                lanceAnimationMontreWebVuePhone();
            }
        }
        if (ancien == ETAT.LISTECARTESEDIT) {
            if (nouveau == ETAT.LISTELISTES) {
                animationListeListesRevient();
            }
            if (nouveau == ETAT.LISTECARTES) {
                boutonSupprimer.setVisibility(View.GONE);
                boutonEdit.setText("Editer");
            }
            if (nouveau == ETAT.CARTE) {
                lanceAnimationMontreWebVuePhone();

            }
        }
        if (ancien == ETAT.CARTE) {
            if (nouveau == ETAT.LISTECARTES) {
                animationListeCartesRevient();
            }
            if (nouveau == ETAT.CARTEEDIT) {
                boutonEdit.setText("Fermer");
            }
        }
        if (ancien == ETAT.CARTEEDIT) {
            if (nouveau == ETAT.LISTECARTES) {
                animationListeCartesRevient();
            }
            if (nouveau == ETAT.CARTE) {
                boutonEdit.setText("Editer");
            }
        }


    }

    void animationListeListesRevient() {
        if (listViewListes.getTranslationX() == 0) {
            listViewListes.setTranslationX(listViewListes.getWidth());
        }
        listViewListes.setVisibility(View.VISIBLE);

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(listViewListes,"translationX",0);
        anim1.setDuration(dureeAnimations);
        anim1.start();

        boutonSupprimer.setVisibility(View.GONE);
        boutonRevient.setEnabled(false);
        boutonEdit.setText("Editer");

    }
    void animationListeCartesRevient() {
        if (listViewCartes.getTranslationX() == 0) {
            listViewCartes.setTranslationX(listViewCartes.getWidth());
        }
        listViewCartes.setVisibility(View.VISIBLE);

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(listViewCartes,"translationX",0);
        anim1.setDuration(dureeAnimations);
        anim1.start();

        boutonSupprimer.setVisibility(View.GONE);
        boutonEdit.setVisibility(View.VISIBLE);
        boutonEdit.setText("Editer");

    }
    void animationMontreListeCartes() {
        listViewListes.setVisibility(View.VISIBLE);
        listViewCartes.setVisibility(View.VISIBLE);
        float delta = listViewListes.getWidth();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(listViewListes,"translationX",delta);
        anim1.setDuration(dureeAnimations);
        anim1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                listViewListes.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim1.start();

        webVueCarte.setVisibility(View.GONE);
        boutonEdit.setText("Editer");
        boutonSupprimer.setVisibility(View.GONE);
        boutonRevient.setEnabled(true);

    }
    void lanceAnimationMontreWebVueTablet() {
        //float delta = -frameLayListeFichiers.getWidth();
        //ObjectAnimator anim1 = ObjectAnimator.ofFloat(frameLayListeFichiers,"translationX",delta);
        //anim1.setDuration(dureeAnimations);
        //anim1.start();
        //optionsMenu(typeFichierNum,nomFichier);
    }
    void lanceAnimationMontreWebVuePhone() {
        listViewCartes.setVisibility(View.VISIBLE);
        webVueCarte.setVisibility(View.VISIBLE);
        float delta = listViewCartes.getWidth();

        listViewListes.setVisibility(View.INVISIBLE);

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(listViewCartes,"translationX",delta);
        anim1.setDuration(dureeAnimations);
        anim1.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                listViewCartes.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        anim1.start();

        boutonEdit.setVisibility(View.INVISIBLE);
        boutonEdit.setText("Editer");
        boutonSupprimer.setVisibility(View.GONE);


    }

    void prepareWebVue() {
        WebSettings webSettings = webVueCarte.getSettings();
        webSettings.setJavaScriptEnabled(true);
        WebAppInterface2 wAppInt = new WebAppInterface2(this.getActivity());

        webVueCarte.addJavascriptInterface(wAppInt, "Android");
        // myWebView.setWebChromeClient(new WebChromeClient());

        webVueCarte.setWebViewClient(new WebViewClient() {

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
                String ttx = MeF.traiteUrlScheme(str, webVueCarte.getWidth(), taillePolice);
                // Log.d("LINK : ", url);

                if (!ttx.equals("")) {
                    webVueCarte.loadDataWithBaseURL(null, ttx, "text/html", "utf-8", null);
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
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void updateBoutons(int index){
        View v;
        if (etat == ETAT.LISTELISTES || etat == ETAT.LISTELISTESEDIT) {
            v = listViewListes.getChildAt(index -
                    listViewListes.getFirstVisiblePosition());
            if(v == null)
                return;
            CheckBox cb = v.findViewById(R.id.checkBoxListeListes);
            ImageView btLL = v.findViewById(R.id.boutonListeListes);
            if (etat == ETAT.LISTELISTES) {
                btLL.setVisibility(View.VISIBLE);
                cb.setVisibility(View.GONE);
            }
            if (etat == ETAT.LISTELISTESEDIT) {
                btLL.setVisibility(View.INVISIBLE);
                cb.setVisibility(View.VISIBLE);
            }
        }
        if (etat == ETAT.LISTECARTES || etat == ETAT.LISTECARTESEDIT) {
            v = listViewCartes.getChildAt(index -
                    listViewCartes.getFirstVisiblePosition());
            if(v == null)
                return;
            CheckBox cb = v.findViewById(R.id.checkBoxListeListes);
            //ImageView btLL = v.findViewById(R.id.boutonListeListes);
            if (etat == ETAT.LISTECARTES) {
                //btLL.setVisibility(View.INVISIBLE);
                cb.setVisibility(View.GONE);
            }
            if (etat == ETAT.LISTECARTESEDIT) {
                //btLL.setVisibility(View.VISIBLE);
                cb.setVisibility(View.VISIBLE);
            }
        }
    }

    //===========================
    private class MyCustomAdapterListes extends ArrayAdapter {
        private ArrayList<String> list;
        private Context context;
        private ArrayList<Integer> listeEliminations = new ArrayList<>();

        public MyCustomAdapterListes(Context context, int resource) { //, ArrayList<String> listes
            super(context, resource); //, listes
            this.list = FCdb.listeListes(1);
            this.context = context;
            //Log.d(ActivitePrincipale2.TAG,TAG + "custom adapt liste " + list.size());
        }

        public void miseAJourListes() {
            this.list.clear();
            ArrayList<String> l = FCdb.listeListes(1);
            this.list.addAll(l);
            listeEliminations.clear();
            //Log.d(ActivitePrincipale2.TAG,TAG + "custom adapt maj liste " + l.size());
            notifyDataSetChanged();
        }
        public void clearListeEliminations() {
            listeEliminations.clear();
            notifyDataSetChanged();
        }

        public void desactivateAll() {
            listeEliminations.clear();
            for (int i = 0;i < list.size(); i++) {
                View v = listViewListes.getChildAt(i -
                        listViewListes.getFirstVisiblePosition());

                if(v == null)
                    return ;
                CheckBox cb = v.findViewById(R.id.checkBoxListeListes);
                cb.setChecked(false);
            }
        }

        private boolean isOneChecked() {
            if (listeEliminations.size() > 0) {
                return true;
            }
            /*
            for (int i = 0;i < list.size(); i++) {
                View v = listViewListes.getChildAt(i -
                        listViewListes.getFirstVisiblePosition());

                //if(v == null) return false;
                CheckBox cb = v.findViewById(R.id.checkBoxListeListes);
                if (cb.isChecked()) {
                    return true;
                }
            }

             */
            return false;
        }

        public void supprime() {
            boolean change = false;
            for (int pos : listeEliminations) {

                String nl = list.get(pos);
                FCdb.supprimeListe(nl);
                Log.d(ActivitePrincipale2.TAG,TAG + " pos " + pos + " a eliminer - " + nl);
            }
            /*
            for (int i = 0;i < list.size(); i++) {
                View v = listViewListes.getChildAt(i -
                        listViewListes.getFirstVisiblePosition());

                if(v == null)
                    return;
                CheckBox cb = v.findViewById(R.id.checkBoxListeListes);
                if (cb.isChecked()) {
                    String nl = list.get(i);
                    FCdb.supprimeListe(nl);
                    change = true;
                }
            }


            if (change) {
                //ArrayList<String> nl = FCdb.listeListes(1);
                //miseAJourListes(nl);
            }

             */
            miseAJourListes();
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }
        /*
        @Override
        public long getItemId(int pos) {
            return 0;  //list.get(pos).getId();
            //just return 0 if your list items do not have an Id variable.
        }
        */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;

            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.liste_listes_cartes, null);
            }

            //Handle TextView and display string from your list
            final TextView tvListItemText  = (TextView)view.findViewById(R.id.tvListeListes);
            final EditText listItemText = (EditText) view.findViewById(R.id.etListeListes);

            String infoListe = list.get(position);
            ArrayList<Carte> lct = FCdb.getListeCartes(infoListe);

            final String premNomL = infoListe;

            listItemText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        String text = v.getText().toString();
                        text = text.trim();
                        //Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                        FCdb.renameListeFC(premNomL,text);
                        //passageEtat(ETAT.LISTELISTESEDIT,ETAT.LISTELISTES);
                        //miseAJourListes(FCdb.listeListes(1));
                        miseAJourListes();
                        //aradListes.notifyDataSetChanged();
                        return true;
                    }
                    return false;
                }
            });

            final CheckBox cb = (CheckBox) view.findViewById(R.id.checkBoxListeListes);
            final ImageView btLl = view.findViewById(R.id.boutonListeListes);

            if (etat == ETAT.LISTELISTESEDIT) {
                listItemText.setEnabled(true);
                listItemText.setCursorVisible(true);

                infoListe += "    ";
                listItemText.setText(infoListe);
                tvListItemText.setVisibility(View.GONE);
                listItemText.setVisibility(View.VISIBLE);

                cb.setVisibility(View.VISIBLE);
                btLl.setVisibility(View.GONE);
            }
            else {

                String strc = "cartes";
                if (lct.size() < 2) {
                    strc = "carte";
                }
                int length1 = infoListe.length();
                infoListe += " (" + String.valueOf(lct.size()) + " " + strc + ")";
                //tvListItemText.setText(infoListe);

                SpannableString infoListeSpannable;
                infoListeSpannable = new SpannableString(infoListe);
                infoListeSpannable.setSpan(new RelativeSizeSpan(0.75f),length1,infoListe.length(),0);
                tvListItemText.setText(infoListeSpannable);

                tvListItemText.setVisibility(View.VISIBLE);
                listItemText.setVisibility(View.GONE);
                listItemText.clearFocus();

                cb.setVisibility(View.GONE);
                btLl.setVisibility(View.VISIBLE);
            }

            btLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listeCartes.clear();
                    String nomListe = (String) aradListes.getItem(position);
                    ArrayList<Carte> lct = FCdb.getListeCartes(nomListe);
                    for (Carte c : lct) {
                        listeCartes.add(c);
                    }
                    //listeCartes = FCdb.getListeCartes(nomListe);
                    aradCartes.miseAJourListe(lct);
                    aradCartes.nomListe = nomListe;
                    aradCartes.notifyDataSetChanged();
                    nomListeCourant = nomListe;
                    etat = ETAT.LISTECARTES;
                    passageEtat(ETAT.LISTELISTESEDIT,ETAT.LISTECARTES);
                }
            });

            if (listeEliminations.contains(position)) {
                cb.setChecked(true);
            }
            else {
                cb.setChecked(false);
            }

            cb.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something

                    if (cb.isChecked()) {
                        listeEliminations.add(position);
                        boutonSupprimer.setEnabled(true);
                    }
                    else {
                        if (listeEliminations.contains(position)) {
                            //listeEliminations.remove(position);
                            Integer posI = position; // car si int enleve a l'index ...
                            listeEliminations.remove(posI);
                        }

                        if (listeEliminations.size() > 0) {
                            boutonSupprimer.setEnabled(true);
                        }
                        else {
                            boutonSupprimer.setEnabled(false);
                        }
                        /*
                        if (isOneChecked()) {
                            boutonSupprimer.setEnabled(true);
                        }
                        else {
                            boutonSupprimer.setEnabled(false);
                        }

                         */
                    }
                    // notifyDataSetChanged();
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.d("===","item click " + String.valueOf(position));
                    String nomListe = (String) aradListes.getItem(position);

                    if (etat == ETAT.LISTELISTES) {
                        lanceTestListener.lanceTest(nomListe);

                    }
                    if (etat == ETAT.LISTELISTESEDIT) {
                        listeCartes.clear();
                        ArrayList<Carte> lct = FCdb.getListeCartes(nomListe);
                        for (Carte c : lct) {
                            listeCartes.add(c);
                        }
                        //listeCartes = FCdb.getListeCartes(nomListe);
                        nomListeCourant = nomListe;
                        aradCartes.miseAJourListe(lct);
                        aradCartes.nomListe = nomListe;
                        aradCartes.notifyDataSetChanged();

                        etat = ETAT.LISTECARTES;
                        passageEtat(ETAT.LISTELISTESEDIT,ETAT.LISTECARTES);

                    }
                }
            });
            /*
            ArrayList<Carte> lct = FCdb.getListeCartes(list.get(position));
            int nbCartes = lct.size();
            String strCartes = " carte";
            if (nbCartes > 1 || nbCartes == 0) {
                strCartes = " cartes";
            }
            listItemText.setText(list.get(position) + " " + nbCartes + strCartes);

         //   listItemText.setText(list.get(position));

            Log.d(ActivitePrincipale2.TAG,TAG + "!!!!! change custom adapt liste get view " + list.get(position));

            final CheckBox cb = (CheckBox) view.findViewById(R.id.checkBoxListeListes);
            final ImageView btLl = view.findViewById(R.id.boutonListeListes);

            // ????
            if (etat == ETAT.LISTELISTESEDIT) {
                cb.setVisibility(View.VISIBLE);
                listItemText.setVisibility(View.VISIBLE);
                btLl.setVisibility(View.VISIBLE);
            }
            else {
                cb.setVisibility(View.GONE);
                listItemText.setVisibility(View.VISIBLE);
                btLl.setVisibility(View.INVISIBLE);
            }

            btLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listeCartes.clear();
                    String nomListe = (String) aradListes.getItem(position);
                    ArrayList<Carte> lct = FCdb.getListeCartes(nomListe);
                    for (Carte c : lct) {
                        listeCartes.add(c);
                    }
                    //listeCartes = FCdb.getListeCartes(nomListe);
                    aradCartes.miseAJourListe(lct);
                    aradCartes.nomListe = nomListe;
                    aradCartes.notifyDataSetChanged();
                    nomListeCourant = nomListe;
                    etat = ETAT.LISTECARTES;
                    passageEtat(ETAT.LISTELISTESEDIT,ETAT.LISTECARTES);
                }
            });


            cb.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something

                    if (cb.isChecked()) {
                        boutonSupprimer.setEnabled(true);
                    }
                    else {
                        if (isOneChecked()) {
                            boutonSupprimer.setEnabled(true);
                        }
                        else {
                            boutonSupprimer.setEnabled(false);
                        }
                    }
                    // notifyDataSetChanged();
                }
            });

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.d("===","item click " + String.valueOf(position));
                    String nomListe = (String) aradListes.getItem(position);

                    if (etat == ETAT.LISTELISTES) {
                        lanceTestListener.lanceTest(nomListe);

                    }
                    if (etat == ETAT.LISTELISTESEDIT) {
                        listeCartes.clear();
                        nomListeCourant = nomListe;
                        ArrayList<Carte> lct = FCdb.getListeCartes(nomListe);
                        for (Carte c : lct) {
                            listeCartes.add(c);
                        }
                        //listeCartes = FCdb.getListeCartes(nomListe);
                        nomListeCourant = nomListe;
                        aradCartes.miseAJourListe(lct);
                        aradCartes.nomListe = nomListe;
                        aradCartes.notifyDataSetChanged();

                        etat = ETAT.LISTECARTES;
                        passageEtat(ETAT.LISTELISTESEDIT,ETAT.LISTECARTES);

                    }
                }
            });
*/
            return view;
        }

    }

    void miseAjourListesListes() {
        aradListes.miseAJourListes();
    }

    private class MyCustomAdapterCartes extends ArrayAdapter {
        private ArrayList<Carte> list = new ArrayList<>();
        private Context context;
        String nomListe = ""; // nom de la liste
        private ArrayList<Integer> listeEliminations = new ArrayList<>();

        public MyCustomAdapterCartes(Context context, int resource, ArrayList<Carte> liste) {
            super(context, resource, liste);
            this.list = liste;
            this.context = context;
        }

        public void miseAJourListe(ArrayList liste) {
            this.list.clear();
            for (Object e : liste) {
                Carte c = (Carte) e;
                this.list.add(c);
            }
            listeEliminations.clear();
            notifyDataSetChanged();
        }
        public void clearListeEliminations() {
            listeEliminations.clear();
            notifyDataSetChanged();
        }

        public void miseAJourListeParNom(String nomListeCarte) {
            ArrayList<Carte> lct = FCdb.getListeCartes(nomListeCarte);
            for (Carte c : lct) {
                listeCartes.add(c);
            }
            //listeCartes = FCdb.getListeCartes(nomListe);
            miseAJourListe(lct);
            nomListe = nomListeCarte;
            listeEliminations.clear();
            notifyDataSetChanged();

        }

        public void desactivateAll() {
            for (int i = 0;i < list.size(); i++) {
                View v = listViewCartes.getChildAt(i -
                        listViewCartes.getFirstVisiblePosition());

                if(v == null)
                    return ;
                CheckBox cb = v.findViewById(R.id.checkBoxListeListes);
                cb.setChecked(false);
            }
            listeEliminations.clear();
        }

        private boolean isOneChecked() {
            if (listeEliminations.size() > 0) {
                return true;
            }
            /*
            for (int i = 0;i < list.size(); i++) {
                View v = listViewCartes.getChildAt(i -
                        listViewCartes.getFirstVisiblePosition());

                //if(v == null) return false;
                CheckBox cb = v.findViewById(R.id.checkBoxListeListes);
                if (cb.isChecked()) {
                    return true;
                }
                if (listeEliminations.contains(i)) {
                    Log.d(ActivitePrincipale2.TAG,TAG + " yo position " + i + " a eliminer");
                }
            }

             */
            return false;
        }

        public void supprime() {

            for (int pos : listeEliminations) {
                Carte c = listeCartes.get(pos);
                Log.d(ActivitePrincipale2.TAG,TAG + " pos " + pos + " a eliminer - " + c.entree);
                FCdb.deleteCarteFromListe(c,nomListe);
            }
            /*
            boolean change = false;

            for (int i = 0;i < list.size(); i++) {
                View v = listViewCartes.getChildAt(i -
                        listViewCartes.getFirstVisiblePosition());

                if(v == null)
                    return;
                CheckBox cb = v.findViewById(R.id.checkBoxListeListes);
                if (cb.isChecked()) {
                    Carte c = listeCartes.get(i);
                    FCdb.deleteCarteFromListe(c,nomListe);
                    change = true;
                }
            }
            if (change) {
                ArrayList<Carte> nl = FCdb.getListeCartes(nomListe);
                miseAJourListe(nl);
            }

             */
            ArrayList<Carte> nl = FCdb.getListeCartes(nomListe);
            miseAJourListe(nl);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int pos) {
            return list.get(pos);
        }
        /*
        @Override
        public long getItemId(int pos) {
            return 0;  //list.get(pos).getId();
            //just return 0 if your list items do not have an Id variable.
        }
        */
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.liste_cartes, null);
            }

            //Handle TextView and display string from your list
            final TextView listItemText = (TextView)view.findViewById(R.id.tvListeListes);
            Carte obj = list.get(position);
            String sp = obj.entree;
            SpannableString spst = MeF.metEnformeMotDsListe(sp,obj.pos,"",taillePolice);
            listItemText.setText(spst);

            final CheckBox cb = (CheckBox) view.findViewById(R.id.checkBoxListeListes);
            //final ImageView btLl = view.findViewById(R.id.boutonListeListes);


            // ????
            if (etat == ETAT.LISTECARTESEDIT) {
                cb.setVisibility(View.VISIBLE);
                listItemText.setVisibility(View.VISIBLE);
                //btLl.setVisibility(View.GONE);
            }
            else {
                cb.setVisibility(View.GONE);
                listItemText.setVisibility(View.VISIBLE);
               // btLl.setVisibility(View.GONE);
            }
/*
            btLl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    montreCarte(position);
                }
            });

*/
            if (listeEliminations.contains(position)) {
                cb.setChecked(true);
            }
            else {
                cb.setChecked(false);
            }

            cb.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //do something
                    if (cb.isChecked()) {
                        listeEliminations.add(position);
                        boutonSupprimer.setEnabled(true);
                    }
                    else {
                        if (listeEliminations.contains(position)) {
                            //listeEliminations.remove(position);
                            Integer posI = position; // car si int enleve a l'index ...
                            listeEliminations.remove(posI);
                        }

                        if (listeEliminations.size() > 0) {
                            boutonSupprimer.setEnabled(true);
                        }
                        else {
                            boutonSupprimer.setEnabled(false);
                        }
                    }

                }
            });
//Log.d(ActivitePrincipale2.TAG,TAG + " liste cartes get view " + position);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Log.d("===","item click " + String.valueOf(position));
                    montreCarte(position);

                }
            });

            return view;
        }
        void montreCarte(int position) {
            if (etat == ETAT.LISTECARTES || etat == ETAT.LISTECARTESEDIT) {
                ETAT anc = etat;
                etat = ETAT.CARTE;

                Carte c = (Carte) aradCartes.getItem(position);
                String ent = c.entree;
                String pos = c.pos;
                largeurFenetre = webVueCarte.getWidth();
                String texte = MeF.chargeDef_et_FlexionsFlashCard(ent,pos,largeurFenetre,taillePolice);
                txtHtmlCarte = texte;
                if (mTwoPane) {
                    //Log.d(ActivitePrincipale2.TAG,texte);
                    webVueCarte.loadDataWithBaseURL(null, texte, "text/html", "utf-8", null);
                }
                else {
                    webVueCarte.loadDataWithBaseURL(null, texte, "text/html", "utf-8", null);
                }
                passageEtat(anc,etat);
            }
        }

    }



}
