package com.verbole.dcad.tabula;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dcad on 1/3/18.
 */

interface SelectDocuments {
    void ouvreDocuments();
}

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    Context context;
    ArrayList<Object> textes;
    List<String> sections;
    private LayoutInflater mInflater;
    private GestionTextes mGestionTextes;
    int indiceDocuments;
    boolean isFichiers;
    String TAG = "CustExp.ListAdapt.";
    //int sectionSelected = -1;
    //int texteSelected = -1;

    public SelectDocuments interfSelectDocs;

    public CustomExpandableListAdapter(Context context, GestionTextes gestionTextes)
    {
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mGestionTextes = gestionTextes;

        /*
        sections = new ArrayList<>();
        sections.add("Auteurs");
        List<String> la = gestionTextes.getListeAuteurs();
        for (String a : la) {
            sections.add(a);
        }
        */
        sections = mGestionTextes.getListeAuteurs();
        textes = new ArrayList<>();
        for (String a : sections) {
            List<String> parties = gestionTextes.listeDesPartiesOeuvresAuteur(a);
            textes.add(parties);
            /*
            for (String p : parties) {
                textes.add(p);
            }
            */
        }
        indiceDocuments = sections.size();
        sections.add("Documents");
        isFichiers = false;
        if (Build.VERSION.SDK_INT <
                Build.VERSION_CODES.KITKAT) {
            initListeFichiers();
        } else {

        }

        //textes.add("Documents");
    }

    void initListeFichiers() {
        List<String> listF = mGestionTextes.getListeNomsFichiers();
        if (listF.size() > 0) {
            if (textes.size() == mGestionTextes.listeAuteurs.size()) {
                textes.add(listF);
            }
            else {
                textes.remove(textes.size() - 1);
                textes.add(listF);
                Log.d(ActivitePrincipale2.TAG," nb textes : " + String.valueOf(textes.size()));
            }
            isFichiers = true;
        }
        else {
            isFichiers = false;
        }
        notifyDataSetChanged();
        Log.d(ActivitePrincipale2.TAG," - textesize 1 : " + String.valueOf(textes.size()));
    }

    @Override
    public int getGroupCount() {
        return sections.size();
    }

    @Override
    public int getChildrenCount(int i) {
        if (textes.size() > i) {
            return ((List<String>) textes.get(i)).size();
        }
        else {
            return 0;
        }
    }

    @Override
    public Integer getGroup(int i) {
        return i;
    }

    @Override
    public String getChild(int i, int i1) {
        if (textes.size() > i) {
            List<String> child = (List<String>) textes.get(i);
            return child.get(i1);
        }
        else return "";

    }

    @Override
    public long getGroupId(int i) {
        return 0;
    }

    @Override
    public long getChildId(int i, int i1) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {

        if (view == null) {

            if (i == indiceDocuments) {
                Log.d(ActivitePrincipale2.TAG,"get group vue  = indice doc ??? " + i);
                view = mInflater.inflate(R.layout.section_header_document,null);
            }
            else {
                view = mInflater.inflate(R.layout.section_header,null);
            }

        }
      //  Log.d(ActivitePrincipale2.TAG," getGroupview : " + String.valueOf(i));
        TextView textVue = (TextView) view.findViewById(R.id.sectionHeaderVue);
        textVue.setText(sections.get(i));
        LinearLayout rellay = (LinearLayout) view.findViewById(R.id.relLay);
        if (i == indiceDocuments) {
            //Log.d("YO","getgroupView : " + String.valueOf(i) + " - isFich : " + String.valueOf(isFichiers));
            if (!isFichiers) {
              //  view.setVisibility(View.INVISIBLE);
                view.setVisibility(View.VISIBLE);
            }
            else {
                view.setVisibility(View.VISIBLE);
            }
            if (Build.VERSION.SDK_INT > 16) {
                if (b) {
                    rellay.setBackground(context.getResources().getDrawable(R.drawable.bordure_section_header_document_enfonce));

                }
                else {
                    rellay.setBackground(context.getResources().getDrawable(R.drawable.bordure_section_header_document));
                }
            }
            if (Build.VERSION.SDK_INT == 16) {
                if (b) {
                    rellay.setBackground(context.getResources().getDrawable(R.color.fond_gris_clair));
                }
                else {
                    rellay.setBackground(context.getResources().getDrawable(R.color.fond_grisfonce));
                    //Log.d("YO","getgroupView : " + String.valueOf(i) + " - bool : " + String.valueOf(b));
                }
            }
        }
        else {
            view.setVisibility(View.VISIBLE);
            if (Build.VERSION.SDK_INT > 16) {
                if (b) {
                    rellay.setBackground(context.getResources().getDrawable(R.drawable.bordure_section_header_textes_enfonce));
                }
                else {
                    rellay.setBackground(context.getResources().getDrawable(R.drawable.bordure_section_header_textes));
                    //Log.d("YO","getgroupView : " + String.valueOf(i) + " - bool : " + String.valueOf(b));
                }
            }
            if (Build.VERSION.SDK_INT == 16) {
                if (b) {
                    rellay.setBackground(context.getResources().getDrawable(R.color.fond_gris_clair));
                }
                else {
                    rellay.setBackground(context.getResources().getDrawable(R.color.fond_grisfonce));
                    //Log.d("YO","getgroupView : " + String.valueOf(i) + " - bool : " + String.valueOf(b));
                }
            }

        }
        //textVue.setTextSize(20);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(R.layout.list_row_texte,null);
        }
        List<String> child = (List<String>) textes.get(i);

        TextView textVue = (TextView) view.findViewById(R.id.listRowVue);
        textVue.setText(child.get(i1));


        //textVue.setTextSize(16);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        if (groupPosition == indiceDocuments) {
            if (interfSelectDocs != null) {
                interfSelectDocs.ouvreDocuments();
            }


        }
    }
}
