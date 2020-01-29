package com.verbole.dcad.tabula;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


interface LanceFlashCardListener {
    void lanceTest(String nomListe);
}

interface FinTestListener {
    void finTest();
}

public class FragmentCartesGeneral extends Fragment implements LanceFlashCardListener, FinTestListener {

    static final String TAG = "fragCartesGeneral ";

    public enum ETAT_GENERAL {DEBUT,LISTE,FCTEST}

    ETAT_GENERAL etat = ETAT_GENERAL.DEBUT;
    String parentTag = "";
    String nomListeCourante;


    public FragmentCartesGeneral() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_cartes_general, container, false);
        Log.d(ActivitePrincipale2.TAG, TAG + "on create view ");
        if (savedInstanceState != null) {
            int temp = savedInstanceState.getInt("etat");
            etat = ETAT_GENERAL.values()[temp];
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(ActivitePrincipale2.TAG, TAG + "on start ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(ActivitePrincipale2.TAG, TAG + "on resume ");
        if (etat == ETAT_GENERAL.LISTE) {
            lanceFragmentListeCartes();
        }
        if (etat == ETAT_GENERAL.DEBUT) {
            ajouteFragmentListeCartes();
        }

        if (etat == ETAT_GENERAL.FCTEST) {
            restoreTestFlashCards();
            //lanceFragmentFlashCard(nomListeCourante);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("etat",etat.ordinal());
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(ActivitePrincipale2.TAG, TAG + "on pause ");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(ActivitePrincipale2.TAG, TAG + "on stop ");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(ActivitePrincipale2.TAG, TAG + "on destroy ");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(ActivitePrincipale2.TAG, TAG + "on detach ");
    }

    void reinitListeListes() {
        FragmentManager fm = getChildFragmentManager();
        FragmentCartes fc = (FragmentCartes) fm.findFragmentByTag("fListeCartes");
        if (fc != null) {
            fc.miseAjourListesListes();
        }
    }

    boolean revient() {
        Log.d(ActivitePrincipale2.TAG,TAG + "revient ? " + etat.toString());
        if (etat == ETAT_GENERAL.FCTEST) {
            etat = ETAT_GENERAL.LISTE;
            lanceFragmentListeCartes();
        }
        else {
            if (etat == ETAT_GENERAL.LISTE) {

                FragmentManager fm = getChildFragmentManager();
                FragmentCartes fc = (FragmentCartes) fm.findFragmentByTag("fListeCartes");
                return fc.revient();
            }
        }

        return false;
    }

    void lanceFragmentFlashCard(String nomListe) {
        etat = ETAT_GENERAL.FCTEST;
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        Bundle b = new Bundle();
        b.putString("nomListe",nomListe);
        b.putString("parentTag",getTag());

        Fragment_FlashCard ffc = new Fragment_FlashCard();
        ffc.finTestListener = this;
        ffc.setArguments(b);

        ft.replace(R.id.FrLayOutFrFC, ffc, "fcTestTag");
        ft.addToBackStack("fcTest");
        ft.commit();

    }

    void restoreTestFlashCards() {
        FragmentManager fm = getChildFragmentManager();
        Fragment_FlashCard fc = (Fragment_FlashCard) fm.findFragmentByTag("fcTestTag");
        if (fc != null) {
            fc.finTestListener = this;
        }


    }

    void ajouteFragmentListeCartes() {
        etat = ETAT_GENERAL.LISTE;
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        FragmentCartes ffc = new FragmentCartes();
        ffc.lanceTestListener = this;
        ft.add(R.id.FrLayOutFrFC, ffc, "fListeCartes");
        Log.d(ActivitePrincipale2.TAG,TAG + "ajoute ??");
        ft.addToBackStack(null);
        ft.commit();
    }

    void lanceFragmentListeCartes() {
        etat = ETAT_GENERAL.LISTE;
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        FragmentCartes ffc = new FragmentCartes();
        ffc.lanceTestListener = this;
        ft.replace(R.id.FrLayOutFrFC, ffc, "fListeCartes");
        Log.d(ActivitePrincipale2.TAG,TAG + "replace ??");
        ft.addToBackStack(null);
        ft.commit();


    }

    @Override
    public void lanceTest(String nomListe) {
        nomListeCourante = nomListe;
        lanceFragmentFlashCard(nomListe);

    }

    @Override
    public void finTest() {
        lanceFragmentListeCartes();
    }
}
