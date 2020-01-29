package com.verbole.dcad.tabula;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Fragment_FlashCard.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Fragment_FlashCard#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Fragment_FlashCard extends Fragment implements View.OnClickListener {
    static final String TAG = "FrFlashCard ";
    //OnFinTestListener mCallback;

    // Container Activity must implement this interface
    public interface OnFinTestListener {
        public void finTest();
    }

    public enum ETAT {INITIAL,DEMANDEMOT,DEMANDEDEF,MONTREMOT,MONTREDEF}

    ETAT etat = ETAT.INITIAL;

    WebView webVueDemande;
    WebView webVueReponse;
    TextView tvDemande;
    TextView tvReponse;
    Button boutonMontre;
    Button boutonBonneReponse;
    Button boutonMauvaiseReponse;
    ConstraintLayout layOutDemande;
    //FrameLayout layOutDemande;
    ConstraintLayout layOutReponse;
    Context mContext;
    MiseEnForme MeF;
    FlashCardsDB FCdb;

    TextView tvScore;
    Button boutonRevient;

    int nbMaxCartes = 20;
    int optionFC = 0;
    int taillePolice = 14;

    List<Carte> listeCartes = new ArrayList<>();

    int indiceSelectionne = 0;
    int indiceSuivant = 0;
    boolean editTxt = false;

    boolean aStoppe = false;
    String parentTag;

    Toolbar mToolBar;

    ArrayList<Integer> listeIndicesFlashCards = new ArrayList<>();
    ArrayList<Integer> listeOrdinaleIndices = new ArrayList<>(); // necessaire pour saved instance state
    // la listeIndicesFlashCards n'est que la liste 1,2,3 ... des indices qui restent
    // la listeOrdinaleIndices indique leur ordre
    // en effet pas possible de sauver la liste des Cartes ds saved instance state car ce n'est pas un type simple


    int score = 0;
    int scoreTotal = 0;

    int compteCartes = 0;


    String listeCourante = "";

    String textDemandeCourant = "";
    String htmlTextDemandeCourant = "";
    String textDemandeSuivant = "";
    String htmlTextDemandeSuivant = "";

    String textReponseCourant = "";
    String htmlTextReponseCourant = "";
    String textReponseSuivant = "";
    String htmlTextReponseSuivant = "";

    FinTestListener finTestListener;

    @Override
    public Context getContext() {
        return super.getContext();
    }

    private OnFragmentInteractionListener mListener;

    public Fragment_FlashCard() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment Fragment_FlashCard.
     */
    // TODO: Rename and change types and number of parameters
    public static Fragment_FlashCard newInstance() {
        Fragment_FlashCard fragment = new Fragment_FlashCard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(ActivitePrincipale2.TAG, TAG + "on create ");
        if (getArguments() != null) {
            listeCourante = getArguments().getString("nomListe");
            parentTag = getArguments().getString("parentTag");
            Log.d(ActivitePrincipale2.TAG,"parentTag : " + parentTag);
           // String nomListe = savedInstanceState.getString("nomListe");
        }
    }
*/



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment__flash_card, container, false);
    //    this.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mContext = getContext();

        Log.d(ActivitePrincipale2.TAG, TAG + "on create view - a stoppe ? " + aStoppe);

        MeF = new MiseEnForme(mContext);
        FCdb =  FlashCardsDB.getInstance(mContext); //new FlashCardsDB(mContext);
        FCdb.openDataBase();

        if (getArguments() != null) {
            listeCourante = getArguments().getString("nomListe");
        }
        if (savedInstanceState != null) {
            recupereInstanceState(savedInstanceState);
            aStoppe = true;
        }


        layOutDemande = view.findViewById(R.id.layOutDemande);
        layOutReponse = view.findViewById(R.id.layOutReponse);
        tvDemande = view.findViewById(R.id.tvDemande);
        tvReponse = view.findViewById(R.id.tvReponse);

        final FrameLayout frlbm = view.findViewById(R.id.frLayBoutonMontre);
        layOutDemande.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float x = frlbm.getX();
                float y = frlbm.getY();
                //Log.d("===","lay out dem on click - x " + String.valueOf(x) + " - y " + String.valueOf(y) + " h : " + String.valueOf(frlbm.getHeight()));
                montreReponseCarte();
            }
        });

        boutonBonneReponse = view.findViewById(R.id.boutonBonneReponse);
        boutonMauvaiseReponse = view.findViewById(R.id.boutonMauvaiseReponse);
        boutonMontre =  view.findViewById(R.id.boutonMontre);
        boutonMontre.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);

        boutonMauvaiseReponse.setOnClickListener(this);
        boutonBonneReponse.setOnClickListener(this);
        boutonMontre.setOnClickListener(this);

        tvScore = view.findViewById(R.id.text_score);
        boutonRevient = view.findViewById(R.id.action_revient);
        boutonRevient.setOnClickListener(this);
        boutonRevient.setText("Fermer");

        webVueDemande = view.findViewById(R.id.webVueDemande);
        webVueReponse = view.findViewById(R.id.webVueReponse);
        webVueReponse.loadDataWithBaseURL(null,"","text/html","utf-8","");
        webVueDemande.loadDataWithBaseURL(null,"","text/html","utf-8","");

        //ConstraintSet csDem = new ConstraintSet();
        //csDem.clone(layOutDemande);
        //https://developer.android.com/reference/android/support/constraint/ConstraintSet

        optionFC = FragmentAide.optionFC;
        nbMaxCartes = FragmentAide.nbMaxFC;

        if (optionFC == 0) {
            if (etat == ETAT.INITIAL) {
                etat = ETAT.DEMANDEMOT;
            }
            tvDemande.setVisibility(View.VISIBLE);
            tvReponse.setVisibility(View.INVISIBLE);
            webVueDemande.setVisibility(View.INVISIBLE);
            webVueReponse.setVisibility(View.VISIBLE);
        }
        else {
            if (etat == ETAT.INITIAL) {
                etat = ETAT.DEMANDEDEF;
            }
            tvDemande.setVisibility(View.INVISIBLE);
            tvReponse.setVisibility(View.VISIBLE);
            webVueReponse.setVisibility(View.INVISIBLE);
            webVueDemande.setVisibility(View.VISIBLE);
        }
        //tvReponse.setVisibility(View.VISIBLE);
        //webVueDemande.setVisibility(View.VISIBLE);

        mToolBar = view.findViewById(R.id.toolbar_fragment_flash_cards);

        if (!aStoppe) {
            initTest();
        }
        else {
            reInitTest();
            /*
            if (optionFC == 0) {

                if (etat == ETAT.DEMANDEDEF || etat == ETAT.MONTREMOT) {
                    reInitTest();
                   // initTest();
                }
            }
            else {
                if (etat == ETAT.DEMANDEMOT || etat == ETAT.MONTREDEF) {
                    //initTest();
                    reInitTest();
                }
            }

             */
        }
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(ActivitePrincipale2.TAG,TAG + " save instance state ? 2e indice : ");
        outState.putString("listeCourante",listeCourante);
        outState.putInt("indiceSelectionne",indiceSelectionne);
        outState.putInt("indiceSuivant",indiceSuivant);

        outState.putIntegerArrayList("listeIndicesFlashCards",listeIndicesFlashCards);
        outState.putIntegerArrayList("listeOrdinaleIndices",listeOrdinaleIndices);
        outState.putInt("score",score);
        outState.putInt("scoreTotal",scoreTotal);
        outState.putInt("compteCartes",compteCartes);

        outState.putInt("nbMaxCartes",nbMaxCartes);
        outState.putInt("optionFC",optionFC);

        outState.putString("textDemandeCourant",textDemandeCourant);
        outState.putString("htmlTextDemandeCourant",htmlTextDemandeCourant);
        outState.putString("textDemandeSuivant",textDemandeSuivant);
        outState.putString("htmlTextDemandeSuivant",htmlTextDemandeSuivant);
        outState.putString("textReponseCourant",textReponseCourant);
        outState.putString("htmlTextReponseCourant",htmlTextReponseCourant);
        outState.putString("textReponseSuivant",textReponseSuivant);
        outState.putString("htmlTextReponseSuivant",htmlTextReponseSuivant);

        outState.putInt("etat",etat.ordinal());


    }

    void saveInstanceState() {

    }

    void recupereInstanceState(Bundle savedInstanceState) {

        listeCourante = savedInstanceState.getString("listeCourante");
        indiceSelectionne = savedInstanceState.getInt("indiceSelectionne");
        indiceSuivant = savedInstanceState.getInt("indiceSuivant");
        listeIndicesFlashCards = savedInstanceState.getIntegerArrayList("listeIndicesFlashCards");
        listeOrdinaleIndices = savedInstanceState.getIntegerArrayList("listeOrdinaleIndices");
        score = savedInstanceState.getInt("score");
        scoreTotal = savedInstanceState.getInt("scoreTotal");
        compteCartes = savedInstanceState.getInt("compteCartes");
        nbMaxCartes = savedInstanceState.getInt("nbMaxCartes");
        optionFC = savedInstanceState.getInt("optionFC");
        textDemandeCourant = savedInstanceState.getString("textDemandeCourant");
        htmlTextDemandeCourant = savedInstanceState.getString("htmlTextDemandeCourant");
        textDemandeSuivant = savedInstanceState.getString("textDemandeSuivant");
        htmlTextDemandeSuivant = savedInstanceState.getString("htmlTextDemandeSuivant");
        textReponseCourant = savedInstanceState.getString("textReponseCourant");
        htmlTextReponseCourant = savedInstanceState.getString("htmlTextReponseCourant");
        textReponseSuivant = savedInstanceState.getString("textReponseSuivant");
        htmlTextReponseSuivant = savedInstanceState.getString("htmlTextReponseSuivant");

        int temp = savedInstanceState.getInt("etat");
        etat = ETAT.values()[temp];

        ArrayList<Carte> lct = FCdb.getListeCartes(listeCourante);
        for (int i = 0;i < listeOrdinaleIndices.size(); i++) {
            Carte c = lct.get(listeOrdinaleIndices.get(i));
            listeCartes.add(c);
        }


        Log.d(ActivitePrincipale2.TAG,TAG + " recupere instance state ?");

    }

/*
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mCallback = (OnFinTestListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnFinTestListener");
        }
    }
*/

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(ActivitePrincipale2.TAG, TAG + "on detach ");

        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
      //  MeF.flex.fermeDBs();
        Log.d(ActivitePrincipale2.TAG, TAG + "on destroy ");
        FCdb.close();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(ActivitePrincipale2.TAG, TAG + "on start ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(ActivitePrincipale2.TAG, TAG + "on stop ");
        aStoppe = true;

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(ActivitePrincipale2.TAG, TAG + "on pause ");
       // MeF.flex.fermeDBs();
        FCdb.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(ActivitePrincipale2.TAG, TAG + "on resume ");
      //  MeF.flex.ouvreDBS();
        FCdb.openDataBase();
        aStoppe = false;

        if (etat == ETAT.DEMANDEDEF || etat == ETAT.DEMANDEMOT) {
            layOutReponse.setVisibility(View.INVISIBLE);
            layOutDemande.setVisibility(View.VISIBLE);
        }
        if (etat == ETAT.MONTREDEF || etat == ETAT.MONTREMOT) {
            layOutReponse.setVisibility(View.VISIBLE);
            layOutDemande.setVisibility(View.INVISIBLE);
        }
        if (etat == ETAT.MONTREMOT) {
            tvReponse.setText(textReponseCourant);
            tvReponse.setVisibility(View.VISIBLE);
        }
        if (etat == ETAT.MONTREDEF) {
            webVueReponse.loadDataWithBaseURL(null,htmlTextReponseCourant,"text/html","utf-8","");
            webVueReponse.setVisibility(View.VISIBLE);
        }
        if (etat == ETAT.DEMANDEMOT) {
            webVueReponse.loadDataWithBaseURL(null,htmlTextReponseCourant,"text/html","utf-8","");
            tvDemande.setText(textDemandeCourant);
            tvDemande.setVisibility(View.VISIBLE);
        }
        if (etat == ETAT.DEMANDEDEF) {
            tvReponse.setText(textReponseCourant);
            webVueDemande.loadDataWithBaseURL(null,htmlTextDemandeCourant,"text/html","utf-8","");
            webVueDemande.setVisibility(View.VISIBLE);

        }
        updateLabelScore();
    }

    void updateLabelScore() {
        String txtsc = String.valueOf((compteCartes + 1)) + "/" + String.valueOf(scoreTotal);
        tvScore.setText(txtsc);
    }

    void reInitTest() {
        Log.d(ActivitePrincipale2.TAG,TAG + " reinit test : compte carte : " + compteCartes + " score " + score + " indice select " + indiceSelectionne);
        if (optionFC == 0) {
            if (etat == ETAT.DEMANDEMOT) {
                tvDemande.setText(textDemandeCourant);
                webVueReponse.loadDataWithBaseURL(null,htmlTextReponseCourant,"text/html","utf-8","");
                layOutDemande.setVisibility(View.VISIBLE);
                layOutReponse.setVisibility(View.INVISIBLE);
            }
            else {
                layOutDemande.setVisibility(View.INVISIBLE);
                layOutReponse.setVisibility(View.VISIBLE);
                webVueReponse.setVisibility(View.VISIBLE);
            }

        }
        else {
            if (etat == ETAT.DEMANDEDEF) {
                tvReponse.setText(textReponseCourant);
                //Log.d("===","entree : " + htmlTextDemandeCourant);
                webVueDemande.loadDataWithBaseURL(null,htmlTextDemandeCourant,"text/html","utf-8","");
                layOutDemande.setVisibility(View.VISIBLE);
                layOutReponse.setVisibility(View.INVISIBLE);
            }
            else {
                layOutDemande.setVisibility(View.INVISIBLE);
                layOutReponse.setVisibility(View.VISIBLE);
                //Log.d("=====","mrep carte - " + htmlTextReponseCourant);
                webVueDemande.loadDataWithBaseURL(null, htmlTextDemandeSuivant, "text/html", "utf-8", "");

            }

        }


        updateLabelScore();
    }

    void initTest() {
        listeIndicesFlashCards.clear();
        listeCartes.clear();
        listeOrdinaleIndices.clear();
        ArrayList<Carte> lct = FCdb.getListeCartes(listeCourante);
        //for (Carte c : lct) { listeCartes.add(c); }



        int max = lct.size();
        if (nbMaxCartes < max) {
            max = nbMaxCartes;
        }
        int compte = 0;

        while (compte < max) {
            int ind;
            double d = Math.random() * lct.size();

            ind = (int) d;
            //Log.d("==random==",String.valueOf(ind));
            //let ind = MeF.randomHasard(max: ar.count)
            if (!listeOrdinaleIndices.contains(ind)) {
                listeOrdinaleIndices.add(ind);
                listeIndicesFlashCards.add(compte);
                Carte c = lct.get(ind);
                listeCartes.add(c);
                //Log.d("====","listecarte : " + c.entree);
                compte += 1;
            }
        }
        randomChoisitCarte(true);
        score = 0;
        compteCartes = 0;
        scoreTotal = listeCartes.size();
        rechercheEnregistrements(true);
        if (optionFC == 0) {
            etat = ETAT.DEMANDEMOT;
            tvDemande.setText(textDemandeCourant);
            webVueReponse.loadDataWithBaseURL(null,htmlTextReponseCourant,"text/html","utf-8","");
        }
        else {
            etat = ETAT.DEMANDEDEF;
            tvReponse.setText(textReponseCourant);
            //Log.d("===","entree : " + htmlTextDemandeCourant);
            webVueDemande.loadDataWithBaseURL(null,htmlTextDemandeCourant,"text/html","utf-8","");
        }
        layOutDemande.setVisibility(View.VISIBLE);
        layOutReponse.setVisibility(View.INVISIBLE);

        updateLabelScore();
    }

    void rechercheEnregistrements(boolean first) {
        int largeurFenetre = webVueReponse.getWidth();
        if (indiceSelectionne > -1) {
            Carte b1 = listeCartes.get(indiceSelectionne);
            if (optionFC == 0) {

                String def = MeF.chargeDefsFlashCard(b1.entree,b1.pos,largeurFenetre,taillePolice); //MeF.chargeDefsFlashCard(mot: b1.entreeCard!, POS: b1.pos!, largeurFenetre: largeurFenetre,taillePoliceBase: self.taillePolice)
                if (b1.customDef != null) {
                    if (!b1.customDef.isEmpty()) {
                        def = MeF.metEnFormeCustomDef(b1.customDef,def); //MeF.metEnFormeCustomDef(customDef : customDef!, textHtml : def)
                    }
                }
                if (first) {
                    textDemandeCourant = b1.entree;
                    htmlTextDemandeCourant = b1.entree;
                    textReponseCourant = def;
                    htmlTextReponseCourant = def;
                }
                else {

                    textDemandeSuivant = b1.entree;
                    htmlTextDemandeSuivant = b1.entree;
                    textReponseSuivant = def;
                    htmlTextReponseSuivant = def;
                }
            }
            else {
                String def = MeF.chargeDefsFlashCard_pourDemande(b1.entree,b1.pos,largeurFenetre,taillePolice);//MeF.chargeDefsFlashCard_pourDemande(mot: b1.entreeCard!, POS: b1.pos!, largeurFenetre: largeurFenetre,taillePoliceBase: self.taillePolice)
                if (first) {
                    textReponseCourant = b1.entree;
                    htmlTextReponseCourant = b1.entree;
                    htmlTextDemandeCourant = def;
                    textDemandeCourant = def;
                    webVueDemande.loadDataWithBaseURL(null,htmlTextDemandeCourant,"text/html","utf-8","");
                }
                else {
                    textReponseSuivant = b1.entree;
                    htmlTextReponseSuivant = b1.entree;
                    htmlTextDemandeSuivant = def;
                    textDemandeSuivant = def;
                }
            }

            if (first && listeCartes.size() > 1) {
                Carte b2 = listeCartes.get(indiceSuivant);
                if (optionFC == 0) {
                    String def = MeF.chargeDefsFlashCard(b2.entree,b2.pos,largeurFenetre,taillePolice);//MeF.chargeDefsFlashCard(mot: b2.entreeCard!, POS: b2.pos!, largeurFenetre: largeurFenetre,taillePoliceBase: self.taillePolice)

                    textDemandeSuivant = b2.entree;
                    htmlTextDemandeSuivant = b2.entree;
                    textReponseSuivant = def;
                    htmlTextReponseSuivant = def;
                }
                else {
                    String def = MeF.chargeDefsFlashCard_pourDemande(b2.entree,b2.pos,largeurFenetre,taillePolice);//MeF.chargeDefsFlashCard_pourDemande(mot: b2.entreeCard!, POS: b2.pos!, largeurFenetre: largeurFenetre,taillePoliceBase: self.taillePolice)
                    textReponseSuivant = b2.entree;
                    htmlTextReponseSuivant = b2.entree;
                    htmlTextDemandeSuivant = def;
                    textDemandeSuivant = def;
                    webVueDemande.loadDataWithBaseURL(null,htmlTextDemandeCourant,"text/html","utf-8","");
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == boutonBonneReponse) {
            if (compteCartes < scoreTotal) {
                compteCartes += 1;
                score += 1;
               // scoreStr = "\(self.score)/\(self.scoreTotal)";

            }
            chargeCarteSuivante();
        }
        if (view == boutonMauvaiseReponse) {
            if (compteCartes < scoreTotal) {
                compteCartes += 1;
            }
            chargeCarteSuivante();
        }
        if (view == boutonMontre) {
            if (layOutDemande.getVisibility() == View.VISIBLE) {
                montreReponseCarte();
            }
            //if (optionFC == 0) { }
        }
        if (view == boutonRevient) {
            Log.d("===","revient ??");
/*
            mToolBar.setVisibility(View.GONE);
            layOutReponse.setVisibility(View.GONE);
            layOutDemande.setVisibility(View.GONE);

 */
            revient();
        }
    }

    FragmentManager recupereFragmentManager() {
        return getActivity().getSupportFragmentManager();
        //getChildFragmentManager(); //.beginTransaction();
    }

    boolean revient() {
        finTestListener.finTest();
/*
        FragmentManager fm = recupereFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        //fm.beginTransaction().remove(this).commit();
        //fm.popBackStack();

        FragmentCartes ffc = new FragmentCartes();
        ft.replace(R.id.FrLayOutFrFC, ffc, parentTag);
        Log.d(ActivitePrincipale2.TAG,TAG + "replace ??");
        ft.addToBackStack(null);
        ft.commit();

       // fm.popBackStack();
*/
        /*
        FragmentCartes fc = (FragmentCartes) fm.findFragmentByTag(parentTag);
        if (fc != null) {
            ft.replace(R.id.FrLayOutFrFC, fc, parentTag);

        //FragmentCartes ffc = new FragmentCartes();
        //ft.replace(R.id.FrLayOutFrFC, ffc, "NewFragmentTag");
            ft.commit();
            fm.popBackStack();
            //ft.addToBackStack(null);

        }
*/




        return false;
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


    // appelee la 1ere fois et qd repeat
    void montreDemandeCarte() {

        if (optionFC == 0) {
            etat = ETAT.DEMANDEMOT;
            tvDemande.setText(textDemandeCourant);
            webVueReponse.loadDataWithBaseURL(null, htmlTextReponseCourant, "text/html", "utf-8", "");
        }
        else {
            etat = ETAT.DEMANDEDEF;
            webVueDemande.loadDataWithBaseURL(null, htmlTextDemandeCourant, "text/html", "utf-8", "");

            tvReponse.setText(textReponseCourant);
        }



        layOutReponse.setVisibility(View.INVISIBLE);
        layOutDemande.setVisibility(View.VISIBLE);

        //self.webVueResultat.loadHTMLString("", baseURL: nil)

        //UIView.transition(from: self.vueReponseCarte, to: self.vueDemandeCarte, duration: 0.8, options: [.transitionFlipFromLeft,.showHideTransitionViews], completion: {(fini : Bool) in
        //    self.rechercheEnregistrements()
        //})
        rechercheEnregistrements(false);
        //self.perform(#selector(selectorUpdateLabelScore), with: nil, afterDelay: 0.01)
        updateLabelScore();

    }

    void randomChoisitCarte(boolean first) {
        if (listeIndicesFlashCards.size() <= 0) {
            indiceSelectionne = -1;
            if (indiceSuivant == -1) {
                indiceSuivant = -2;
            }
            else {
                indiceSuivant = -1;
            }
            if (listeCartes.size() <= 2) {
                indiceSuivant = -2;
            }
        }
        if (listeIndicesFlashCards.size() == 1) {
            indiceSelectionne = (int) listeIndicesFlashCards.get(0);
            //indiceSelectionne = res;
            listeIndicesFlashCards.remove(0);

            indiceSuivant = -1;
            if (listeCartes.size() <= 1) {
                indiceSuivant = -2;
            }
        }
        if (listeIndicesFlashCards.size() > 1) {
            indiceSuivant = indiceSelectionne;

            //var nb = self.listeIndicesFlashCards.count
            int randomnb = 0; //MeF.randomHasard(max:nb)
            int res = listeIndicesFlashCards.get(randomnb);
            indiceSelectionne = res;
            listeIndicesFlashCards.remove(randomnb);

            if (first && listeIndicesFlashCards.size() > 0) {
                //nb = self.listeIndicesFlashCards.count
                int randomnb2 = 0; //MeF.randomHasard(max:nb)
                res = listeIndicesFlashCards.get(randomnb2);
                indiceSuivant = res;
                listeIndicesFlashCards.remove(randomnb2);
            }
        }
        //return res
    }

    public void chargeCarteSuivante() {
Log.d("===","charg c s : " + String.valueOf(indiceSuivant));
        if (indiceSuivant > -1) {

            randomChoisitCarte(false);
            textReponseCourant = textReponseSuivant;
            textDemandeCourant = textDemandeSuivant;
            htmlTextDemandeCourant = htmlTextDemandeSuivant;
            htmlTextReponseCourant = htmlTextReponseSuivant;

            montreDemandeCarte();
        }
        else {

            if (indiceSuivant == -1) {
                randomChoisitCarte(false);
                textReponseCourant = textReponseSuivant;
                textDemandeCourant = textDemandeSuivant;
                htmlTextDemandeCourant = htmlTextDemandeSuivant;
                htmlTextReponseCourant = htmlTextReponseSuivant;

                montreDemandeCarte();
            }
            else {
               // score = "Score : \(self.score*100/self.listeFlashCards.count)%\n";
               // score += "(\(self.score)/\(self.listeFlashCards.count))";
                //print(score)
                int scmax = listeCartes.size();
                if (scmax == 0) {
                    scmax = score; // pr eviter div par zero
                }
                String txtScore = "Score : " + String.valueOf(score*100/scmax) + "%\n";
                txtScore += "(" + String.valueOf(score) + "/" + String.valueOf(listeCartes.size() + ")");
                finTest(txtScore);
            }

        }

    }

    public void montreReponseCarte() {
        layOutDemande.setVisibility(View.INVISIBLE);
        layOutReponse.setVisibility(View.VISIBLE);
        //Log.d("=====","mrep carte - " + htmlTextReponseCourant);

        if (optionFC == 0) {
            etat = ETAT.MONTREDEF;
            webVueReponse.setVisibility(View.VISIBLE);
        }
        else {
            etat = ETAT.MONTREMOT;
            webVueDemande.loadDataWithBaseURL(null, htmlTextDemandeSuivant, "text/html", "utf-8", "");

        }
        //UIView.transition(from: self.vueDemandeCarte, to: self.vueReponseCarte, duration: 0.8, options: [.transitionFlipFromLeft,.showHideTransitionViews], completion: {(fini : Bool) in
        //})

    }

    void finTest(String score) {
        //Log.d("===",score);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(score);

        builder.setPositiveButton("Recommencer",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                webVueReponse.loadDataWithBaseURL(null,"","text/html","utf-8","");
                webVueDemande.loadDataWithBaseURL(null,"","text/html","utf-8","");

                initTest();
            }
        });

        builder.setNegativeButton("Quitter",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {

               // mCallback.finTest();
                finTestListener.finTest();
            }
        });
        AlertDialog ad = builder.create();
        ad.show();

    }
    void finTest2(String score) {
        Log.d("===",score);
        //let alert: UIAlertController = UIAlertController(title: score, message: "", preferredStyle: .alert)


        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.fin_test_dialog);
        //dialog.setTitle("Selectionnez une liste");

        TextView tv = dialog.findViewById(R.id.fin_test_score_tv);
        tv.setText(score);

        Button boutonQuitter = dialog.findViewById(R.id.boutonDialogQuitter);
        boutonQuitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                finTestListener.finTest();
                //mCallback.finTest();
            }
        });
        Button boutonRecommencer = dialog.findViewById(R.id.boutonDialogRecommencer);
        boutonRecommencer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webVueReponse.loadDataWithBaseURL(null,"","text/html","utf-8","");
                webVueDemande.loadDataWithBaseURL(null,"","text/html","utf-8","");
                dialog.dismiss();
                initTest();
            }
        });

        dialog.show();

    }

}
