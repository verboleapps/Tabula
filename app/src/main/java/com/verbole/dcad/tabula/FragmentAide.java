package com.verbole.dcad.tabula;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by dcad on 1/16/18.
 */

public class FragmentAide extends Fragment implements AdapterView.OnItemClickListener,
        View.OnClickListener //, TextView.OnEditorActionListener
{
    WebView myWebView;
    ListView listeAide;
    String[] nomsMenu = {"Aide","Cartes","Dictionnaires","Crédits"};
    CustBaseAdapter mAdapter;
    private boolean mTwoPanes;
    ListView listeVueDicos;
    int taillePolice = 12;
    ListeVueAdapterDragDrop mDragDropAdapter;
    List<String> dictionnaires; // = new ArrayList<String>(Arrays.asList("Tabula"));
    Toolbar mToolBar;
    ImageView boutonRevient;
    RelativeLayout relLayAide;
    long dureeAnimations = 600;

    RelativeLayout relLayOptionsFC;
    TextView labelNbMaxFC;
    EditText tvNbMaxFC;
    TextView labelOptionFC;
    ToggleButton switchOptionFC;
    TextView titreTexte;

    int currSelection = 1;

    public static int optionFC = 0;
    public static int nbMaxFC = 20;


    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_aide, container, false);

        mAdapter = new CustBaseAdapter();
        mTwoPanes = StyleHtml.verifieEcranEstTablette(this.getActivity());

        taillePolice = getResources().getInteger(R.integer.taille_police_webvues) + 2;

        myWebView = (WebView) view.findViewById(R.id.webVueAide);
        listeAide = view.findViewById(R.id.listeVueAide);
        listeAide.setAdapter(mAdapter);
        listeAide.setOnItemClickListener(this);


        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);
        //myWebView.loadDataWithBaseURL(null,"","text/html","utf-8",null);
       // webVueLoadHtml("vues/pageAide.html");
        myWebView.setVisibility(View.VISIBLE);


        getOdreDictsListe();
        listeVueDicos = view.findViewById(R.id.listeVueDicos);
        listeVueDicos.setVisibility(View.INVISIBLE);
        mDragDropAdapter = new ListeVueAdapterDragDrop(this.getContext(),R.layout.liste_ligne_dictionnaire,dictionnaires);
        listeVueDicos.setAdapter(mDragDropAdapter);

        mToolBar = view.findViewById(R.id.toolbar_fragment_aide);

        mToolBar.setVisibility(View.GONE);

        boutonRevient = view.findViewById(R.id.action_revient);
        boutonRevient.setOnClickListener(this);

        titreTexte = view.findViewById(R.id.titreTexte);
        titreTexte.setVisibility(View.GONE);

        relLayAide = view.findViewById(R.id.relLayAide);

        myWebView.setTranslationX(listeAide.getWidth());

        relLayOptionsFC = view.findViewById(R.id.relLayOptionsFC);
        labelNbMaxFC = view.findViewById(R.id.labelNbMax);
        tvNbMaxFC = view.findViewById(R.id.tvNbMaxFC);
        //tvNbMaxFC.setOnEditorActionListener(this);
        tvNbMaxFC.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Log.d("===","? ch seq : " + charSequence + " i: " + String.valueOf(i) + " i1: " + String.valueOf(i1) + " i2: " + String.valueOf(i2));
                int nbT = 0;
                try {
                    nbT = Integer.parseInt(charSequence.toString());
                    //Log.d("===","? ch seq : " + charSequence + " nb: " + String.valueOf(nbT));
                } catch(NumberFormatException nfe) {
                    // Handle parse error.
                }

                if (nbT > 0 && nbT < 500) {
                    nbMaxFC = nbT;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        labelOptionFC = view.findViewById(R.id.labelSwitch);
        switchOptionFC = view.findViewById(R.id.switchOptionFC);
        switchOptionFC.setOnClickListener(this);
        relLayOptionsFC.setVisibility(View.INVISIBLE);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();

    }


    void animationMontre(final int option) {

        ObjectAnimator anim1 = ObjectAnimator.ofFloat(listeAide,"translationX",-listeAide.getWidth());
        anim1.setDuration(dureeAnimations);

        if (!mTwoPanes) {
            anim1.start();
            titreTexte.setVisibility(View.VISIBLE);
            titreTexte.setText(nomsMenu[option/2]);
            if (option == 5) {
                myWebView.setVisibility(View.INVISIBLE);
                listeVueDicos.setVisibility(View.VISIBLE);
                relLayOptionsFC.setVisibility(View.INVISIBLE);
            }
            else {
                if (option == 3) {
                    myWebView.setVisibility(View.INVISIBLE);
                    listeVueDicos.setVisibility(View.INVISIBLE);
                    relLayOptionsFC.setVisibility(View.VISIBLE);
                }
                else {
                    myWebView.setVisibility(View.VISIBLE);
                    listeVueDicos.setVisibility(View.INVISIBLE);
                    relLayOptionsFC.setVisibility(View.INVISIBLE);
                }

            }
            mToolBar.setVisibility(View.VISIBLE);
        }
        else {
            //Log.d("===","anim montre : " + String.valueOf(option));
            if (option == 5) {
                myWebView.setVisibility(View.INVISIBLE);
                listeVueDicos.setVisibility(View.VISIBLE);
                relLayOptionsFC.setVisibility(View.INVISIBLE);
                mToolBar.setVisibility(View.GONE);
            }
            if (option == 1 || option == 7) {
                titreTexte.setVisibility(View.VISIBLE);
                titreTexte.setText(nomsMenu[option/2]);
                anim1.start();
                myWebView.setVisibility(View.VISIBLE);
                listeVueDicos.setVisibility(View.INVISIBLE);
                relLayOptionsFC.setVisibility(View.INVISIBLE);
                mToolBar.setVisibility(View.VISIBLE);
            }
            if (option == 3) {
                relLayOptionsFC.setVisibility(View.VISIBLE);
                listeVueDicos.setVisibility(View.INVISIBLE);
                mToolBar.setVisibility(View.GONE);
            }
        }


    }
    void animationRevient() {
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(listeAide,"translationX",0);
        anim1.setDuration(dureeAnimations);

        if (!mTwoPanes) {
            anim1.start();
            listeVueDicos.setVisibility(View.INVISIBLE);
            relLayOptionsFC.setVisibility(View.INVISIBLE);

        }
        else {
            if (myWebView.getVisibility() == View.VISIBLE) {
                anim1.start();
            }
        }
        mToolBar.setVisibility(View.GONE);

    }

    void webVueLoadHtml(int menuOption) {
        String nomFichier = "";
        if (menuOption == 1) {
            nomFichier = "vues/pageAide.html";
        }
        if (menuOption == 7) {
            nomFichier = "vues/pageCredits.html";
        }

        AssetManager asmatt = getContext().getAssets();
        InputStream input;
        String texte = "";

        try {
            input = asmatt.open(nomFichier);
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            texte = new String(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }
        StyleHtml st = new StyleHtml();
        texte = st.styleHtmlAide(myWebView.getWidth(),taillePolice) + texte;

        //if (!mTwoPanes) {
            if (menuOption == 1) {
                texte = texte.replace("<h1 align=center>Aide</h1>","");
            }
            if (menuOption == 7) {
                texte = texte.replace("<h1 align=center>Crédits</h1>","");
            }
       // }

        myWebView.loadDataWithBaseURL(null,texte,"text/html","utf-8",null);
    }

    void loadReglage(int menuOption) {

        if (menuOption == 1 || menuOption == 7) {
            webVueLoadHtml(menuOption);
            animationMontre(menuOption);
        }
        if (menuOption == 3) {
            animationMontre(menuOption);
        }
        if (menuOption == 5) {
            getOdreDictsListe();
            mDragDropAdapter.notifyDataSetChanged();
            animationMontre(menuOption);
        }



    }

    void getOdreDictsListe() {
        GestionSettings gs = new GestionSettings(this.getContext());
        String ordreDicts = gs.getOrdreDicts();
        if (ordreDicts.equals("PG")) {
            //dictionnaires.clear();
            //dictionnaires.add("Tabula");
            //dictionnaires.add("Gaffiot");
            dictionnaires = new ArrayList<String>(Arrays.asList("Tabula","Gaffiot"));
        }
        if (ordreDicts.equals("GP")) {
            //dictionnaires.clear();
            //dictionnaires.add("Gaffiot");
            //dictionnaires.add("Tabula");
            dictionnaires = new ArrayList<String>(Arrays.asList("Gaffiot","Tabula"));
        }
        if (ordreDicts.equals("P")) {
            //dictionnaires.clear();
            //dictionnaires.add("Tabula");
            dictionnaires = new ArrayList<String>(Arrays.asList("Tabula"));
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position % 2 == 1) {
            loadReglage(position);
            currSelection = position;
            if (mTwoPanes) {
                int[] val = {1,3,5,7};
                for (int i : val) {
                    if (i == position) {
                        mAdapter.select(i);
                    }
                    else {
                        mAdapter.unselect(i);
                    }
                }

            }

        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {

            if (!mTwoPanes) {
                // ?????
                if (myWebView.getVisibility() == View.VISIBLE) {
                    myWebView.setVisibility(View.INVISIBLE);
                    listeVueDicos.setVisibility(View.INVISIBLE);
                    relLayOptionsFC.setVisibility(View.INVISIBLE);
                    listeAide.setVisibility(View.VISIBLE);
                    return true;
                }
                if (listeVueDicos.getVisibility() == View.VISIBLE) {
                    listeVueDicos.setVisibility(View.INVISIBLE);
                    myWebView.setVisibility(View.INVISIBLE);
                    listeAide.setVisibility(View.VISIBLE);
                    relLayOptionsFC.setVisibility(View.INVISIBLE);
                    return true;
                }
                if (relLayOptionsFC.getVisibility() == View.VISIBLE) {
                    listeVueDicos.setVisibility(View.INVISIBLE);
                    myWebView.setVisibility(View.INVISIBLE);
                    listeAide.setVisibility(View.VISIBLE);
                    relLayOptionsFC.setVisibility(View.INVISIBLE);
                    return true;
                }
            }

        }

        return super.onOptionsItemSelected(item);
    }

    boolean revient() {
        tvNbMaxFC.clearFocus();
        boolean rep = true;
        if (mToolBar.getVisibility() == View.VISIBLE) {
            rep = false;
        }
        animationRevient();

        return rep;
    }

    @Override
    public void onClick(View v) {
        if (v == boutonRevient) {
            revient();
        }
        if (v == switchOptionFC) {

            if (switchOptionFC.isChecked()) {
                optionFC = 1;
            }
            else {
                optionFC = 0;
            }
        }
    }
/*
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        Log.d("====","edit act : " + keyEvent.toString() + " - " + String.valueOf(i));
        return false;
    }
*/
    private class CustBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 8;
        }

        @Override
        public String getItem(int position) {
            int ind = position / 2;
            return nomsMenu[ind];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater mInflater  =  (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null) {
                if (position % 2 == 0) {
                    convertView =  mInflater.inflate(R.layout.menu_separateur,null);
                }
                else {
                    convertView =  mInflater.inflate(R.layout.liste_menu_aide,null);
                    TextView tv = (TextView) convertView.findViewById(R.id.textVueMenuListe);
                    int ind = position / 2;
                    tv.setText(nomsMenu[ind]);

                }
            }
            return convertView;
        }
        void select(int position) {
            View v = listeAide.getChildAt(position);
            TextView tv = v.findViewById(R.id.textVueMenuListe);
            tv.setBackgroundColor(Color.GRAY);
        }
        void unselect(int position) {
            View v = listeAide.getChildAt(position);
            TextView tv = v.findViewById(R.id.textVueMenuListe);
            tv.setBackgroundColor(getResources().getColor(R.color.blanc));
        }
    }

}
