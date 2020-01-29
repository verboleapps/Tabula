package com.verbole.dcad.tabula;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dcad on 1/16/18.
 */

public class FragmentGrammaire extends Fragment {
    public FragmentGrammaire() {
        super();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.activity_grammaire, container, false);
        WebView myWebView = (WebView) view.findViewById(R.id.webVueGramm);

        AssetManager asmatt = getActivity().getAssets();//getContext().getAssets();
        InputStream input;
        String texte = "";
        int taillePolice = getResources().getInteger(R.integer.taille_police_webvues);
        try {
            String fichGramm = "vues/AbregeGrammLatin.html";

            if (myWebView.getWidth() < 700) {
                fichGramm = "vues/AbregeGrammLatinPetit.html";

            }

            input = asmatt.open(fichGramm);
            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            texte = new String(buffer);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //txthh = txtHtml.stringByReplacingOccurrencesOfString("TABLE DES MATIERES", withString: "Sommaire")
        texte = texte.replaceFirst("TABLE DES MATIERES","SOMMAIRE");
        StyleHtml style = new StyleHtml();
        texte = style.styleHtmlGrammaire(myWebView.getWidth(),taillePolice) + texte;

        myWebView.loadDataWithBaseURL(null,texte,"text/html","utf-8",null);
        myWebView.getSettings().setBuiltInZoomControls(true);
        myWebView.getSettings().setDisplayZoomControls(false);
        return view;
    }
}
