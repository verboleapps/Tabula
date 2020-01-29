package com.verbole.dcad.tabula;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class DialogueAjouteCarte {
    Context mContext;
    MiseEnForme MeF;
    Activity activite;

    DialogueAjouteCarte(Context context, Activity activity, MiseEnForme mf) {
        mContext = context;
        MeF = mf;
        activite = activity;
    }


    public void createDial(String formeAChercher) {
        final String entTemp = formeAChercher;

        final FlashCardsDB FCdb;
        FCdb = FlashCardsDB.getInstance(mContext); //new FlashCardsDB(mContext);
        FCdb.openDataBase();
        final List<String> ls = FCdb.listeListes(1);
        String[] liste = new String[ls.size()];
        ls.toArray(liste);


        final AlertDialog.Builder myDialog =
                new AlertDialog.Builder(activite);
        myDialog.setTitle("Selectionnez une liste : ");

        myDialog.setItems(liste, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String nomListe = ls.get(i);
                ArrayList<EnregDico> lec = MeF.flex.rechercheEntreesDsBasePourAjouterCartes(entTemp,"","motsimple");
                ArrayList<String> listePOS = new ArrayList<>();

                String posT = "";

                for (EnregDico e : lec) {
                    Carte c = creeCarte(e.mot,e.POS,""); // motOrig;
                    posT = c.pos;
                    if (c.pos.equals("VPAR")) {
                        posT = "ADJ";
                    }

                    if (!listePOS.contains(posT)) {
                        FCdb.ajouteCarteAListe(c,nomListe);
                        listePOS.add(posT);
                    }

                    // dialog.dismiss();
                }

                dialogInterface.dismiss();
            }
        });

        myDialog.setNegativeButton("Fermer", null);
        myDialog.setPositiveButton("Nouvelle Liste", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Log.d("===","new list");

                //Dialog dd = new Dialog(getActivity());
                //dd.setTitle("yo dialog 2");
                final EditText et = new EditText(mContext);
                AlertDialog.Builder builder = new AlertDialog.Builder(activite);
                builder.setView(et);

                builder.setPositiveButton("Valider",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        String input = et.getText().toString();
                        if (!input.isEmpty()) {
                            creeNewListe(input, ls, FCdb);
                            createDial(formeAChercher);
                        }

                    }
                });

                builder.setNegativeButton("Annuler",null);
                AlertDialog ad = builder.create();
                ad.setTitle("Nouvelle liste :");
                ad.show();
            }
        });


        myDialog.show();

    }

    public void createDial(EnregDico entree) {

        final FlashCardsDB FCdb;
        FCdb = FlashCardsDB.getInstance(mContext); //new FlashCardsDB(mContext);
        FCdb.openDataBase();
        final List<String> ls = FCdb.listeListes(1);
        String[] liste = new String[ls.size()];
        ls.toArray(liste);


        final AlertDialog.Builder myDialog =
                new AlertDialog.Builder(activite);
        myDialog.setTitle("Selectionnez une liste : ");

        myDialog.setItems(liste, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String nomListe = ls.get(i);
                Carte c = creeCarte(entree.mot,entree.POS,""); // motOrig;
                FCdb.ajouteCarteAListe(c,nomListe);
                dialogInterface.dismiss();
            }
        });

        myDialog.setNegativeButton("Fermer", null);
        myDialog.setPositiveButton("Nouvelle Liste", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Log.d("===","new list");

                //Dialog dd = new Dialog(getActivity());
                //dd.setTitle("yo dialog 2");
                final EditText et = new EditText(mContext);
                AlertDialog.Builder builder = new AlertDialog.Builder(activite);
                builder.setView(et);

                builder.setPositiveButton("Valider",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        String input = et.getText().toString();
                        if (!input.isEmpty()) {
                            creeNewListe(input, ls, FCdb);
                            createDial(entree);
                        }

                    }
                });

                builder.setNegativeButton("Annuler",null);
                AlertDialog ad = builder.create();
                ad.setTitle("Nouvelle Liste :");
                ad.show();
            }
        });


        myDialog.show();

    }

    public void createDialPourResultatsRecherches(ArrayList<ResultatRecherche> resultats) {


        final FlashCardsDB FCdb;
        FCdb = FlashCardsDB.getInstance(mContext);// new FlashCardsDB(mContext);
        FCdb.openDataBase();
        final List<String> ls = FCdb.listeListes(1);
        String[] liste = new String[ls.size()];
        ls.toArray(liste);


        final AlertDialog.Builder myDialog =
                new AlertDialog.Builder(activite);
        myDialog.setTitle("Selectionnez une liste : ");

        myDialog.setItems(liste, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String nomListe = ls.get(i);

                ArrayList<String> listePOS = new ArrayList<>();
                String posT = "";

                for (ResultatRecherche resT : resultats) {
                    Carte c = creeCarte(resT.mot.mot,resT.mot.POS,""); // motOrig;
                    posT = c.pos;
                    if (c.pos.equals("VPAR")) {
                        posT = "ADJ";
                    }

                    if (!listePOS.contains(posT)) {

                        FCdb.ajouteCarteAListe(c,nomListe);
                        listePOS.add(posT);

                    }

                    // dialog.dismiss();
                }

                dialogInterface.dismiss();
            }
        });

        myDialog.setNegativeButton("Fermer", null);
        myDialog.setPositiveButton("Nouvelle Liste", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("===","new list");

                //Dialog dd = new Dialog(getActivity());
                //dd.setTitle("yo dialog 2");
                final EditText et = new EditText(mContext);
                AlertDialog.Builder builder = new AlertDialog.Builder(activite);
                builder.setView(et);

                builder.setPositiveButton("Valider",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        String input = et.getText().toString();
                        if (!input.isEmpty()) {
                            creeNewListe(input, ls, FCdb);
                            createDialPourResultatsRecherches(resultats);
                        }

                    }
                });

                builder.setNegativeButton("Annuler",null);
                AlertDialog ad = builder.create();
                ad.setTitle("Nouvelle liste :");
                ad.show();
            }
        });


        myDialog.show();

    }

    public void createDialPourResultatFTS(ResultatFTS resultat) {

        final FlashCardsDB FCdb;
        FCdb = FlashCardsDB.getInstance(mContext);// new FlashCardsDB(mContext);
        FCdb.openDataBase();
        final List<String> ls = FCdb.listeListes(1);
        String[] liste = new String[ls.size()];
        ls.toArray(liste);

        final AlertDialog.Builder myDialog =
                new AlertDialog.Builder(activite);
        myDialog.setTitle("Selectionnez une liste : ");

        myDialog.setItems(liste, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String nomListe = ls.get(i);

                String posT = "";

                Carte c = creeCarte(resultat.motsimple,resultat.POS,""); // motOrig;
                posT = c.pos;
                if (c.pos.equals("VPAR")) {
                    posT = "ADJ";
                }
Log.d("Dialogue","ajoute carte : " + c.entree + " -" + c.pos + "-");
                FCdb.ajouteCarteAListe(c,nomListe);

                dialogInterface.dismiss();
            }
        });

        myDialog.setNegativeButton("Fermer", null);
        myDialog.setPositiveButton("Nouvelle Liste", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d("===","new list");

                //Dialog dd = new Dialog(getActivity());
                //dd.setTitle("yo dialog 2");
                final EditText et = new EditText(mContext);
                AlertDialog.Builder builder = new AlertDialog.Builder(activite);
                builder.setView(et);

                builder.setPositiveButton("Valider",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        String input = et.getText().toString();
                        if (!input.isEmpty()) {
                            creeNewListe(input, ls, FCdb);

                            createDialPourResultatFTS(resultat);
                        }

                    }
                });

                builder.setNegativeButton("Annuler",null);
                AlertDialog ad = builder.create();
                ad.setTitle("Nouvelle liste :");
                ad.show();
            }
        });


        myDialog.show();

    }

    Carte creeCarte(String mot, String pos, String customDef) {
        Carte c = new Carte();
        c.entree = mot;
        c.pos = pos;
        c.customDef = customDef;
        return c;
    }

    void creeNewListe(String nomListe, List<String> liste, FlashCardsDB fcdb) {
        if (!liste.contains(nomListe)) {
            fcdb.addListeFC(nomListe);
        }
        else {
            String nom2 = nomListe;
            int i = 1;
            while (liste.contains(nom2)) {
                nom2 = nomListe + "_" + String.valueOf(i);
                i += 1;
            }
            fcdb.addListeFC(nom2);
        }

        /*
                            if (!ls.contains(input)) {
                                FCdb.addListeFC(input);
                            }
                            else {
                                String nom2 = input;
                                int i = 1;
                                while (ls.contains(nom2)) {
                                    nom2 = input + "_" + String.valueOf(i);
                                    i += 1;
                                }
                                FCdb.addListeFC(nom2);
                            }
                            */
    }
}
