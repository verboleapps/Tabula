package com.verbole.dcad.tabula;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by dcad on 1/11/18.
 */

class ListeVueAdapterDragDrop extends ArrayAdapter implements View.OnTouchListener, View.OnDragListener, View.OnLongClickListener {
    Context mContext;
    final List<String> dictionnaires;


    public ListeVueAdapterDragDrop(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
        dictionnaires = new ArrayList<>();
        dictionnaires.addAll(objects);
        mContext = context;
    }

    @Override
    public @Nullable
    String getItem(int position) {
        return dictionnaires.get(position);
    }

    @Override
    public int getCount() {
        return dictionnaires.size();
    }

    /*
    @Override
    public long getItemId(int position) {
        return position;
    }
    */
    String getOrdreDictsFromList() {
        String res = "";
        for (String d : dictionnaires) {
            if (d.equals("Tabula")) {
                res += "P";
            }
            if (d.equals("Gaffiot")) {
                res += "G";
            }
        }
        return res;
    }

    @Override
    public @NonNull
    View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.liste_row_dicos, null);
        }

        TextView tv = v.findViewById(R.id.textVueListeDico);
        tv.setText(dictionnaires.get(position));
        //tv.setOnTouchListener(this);
        tv.setOnDragListener(this);
        tv.setOnLongClickListener(this);
        tv.setTag(position);

        return v;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        v.performClick();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ClipData data = ClipData.newPlainText("tag",""); //ClipData.newPlainText("tag",(String) v.getTag());
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            //view.startDrag(data, shadowBuilder, view, 0);
            //view.setVisibility(View.INVISIBLE);
            int t = (int) v.getTag();
            if (t >= dictionnaires.size()) {
                return false;
            }
            boolean res = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                res = v.startDragAndDrop(data,shadowBuilder,v,0);
            }
            else {
                res = v.startDrag(data,shadowBuilder,v,0);
            }
            return res; //true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            ClipData data = ClipData.newPlainText("tag","");//ClipData.newPlainText("tag",(String) v.getTag());
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            int t = (int) v.getTag();
            if (t == 0) {
                return false;
            }
            boolean res = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                res = v.startDragAndDrop(data,shadowBuilder,v,0);
            }
            else {
                res = v.startDrag(data,shadowBuilder,v,0);
            }
            return res; //true;
        }
        return false;
    }

    @Override
    public boolean onDrag(View v, DragEvent event) {
        //int action = event.getAction();
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                //Log.d("TABULA listeadaptDD","drag started");
                // do nothing
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                //v.setBackgroundDrawable(enterShape);
                //Log.d("TABULA listeadaptDD","drag entered");
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                //v.setBackgroundDrawable(normalShape);
                //Log.d("TABULA listeadaptDD","drag exited");
                break;
            case DragEvent.ACTION_DROP:
                //Log.d("TABULA listeadaptDD","drop");
                    // Dropped, reassign View to ViewGroup
                    View view = (View) event.getLocalState();
                    int dragTag = (int) view.getTag();
                    //ViewGroup owner = (ViewGroup) view.getParent();
                    //owner.removeView(view);
                    int targetTag = (int) v.getTag();
                    //view.setVisibility(View.VISIBLE);
                Collections.swap(dictionnaires,dragTag,targetTag);
                notifyDataSetChanged();
                GestionSettings gs = new GestionSettings(mContext);
                String od = getOrdreDictsFromList();
                gs.setDicts(od);

                if (dragTag == targetTag) {
                    //Log.d("TABULA listeadaptDD","long click");
                    int tag = (int) v.getTag();
                    String dic = dictionnaires.get(tag);
                    if (dic.equals("Gaffiot")) {
                        desinstalle();
                    }
                }

                break;
            case DragEvent.ACTION_DRAG_ENDED:
                //v.setBackgroundDrawable(normalShape);
                //Log.d("TABULA listeadaptDD","drag ended");
            default:
                break;
        }
        return true;
    }

    void desinstalle() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("voulez-vous désinstaller le dictionnaire Gaffiot ?");
        builder.setCancelable(false);
        builder.setPositiveButton("non",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.setNegativeButton("oui",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                MiseEnForme MeF = new MiseEnForme(mContext);
                MeF.flex.bdDics.dropTable("GaffiotInd");
                dictionnaires.clear();
                dictionnaires.add("Tabula");
                notifyDataSetChanged();
                GestionSettings gs = new GestionSettings(mContext);
                gs.setDicts("P");
                Toast toast = Toast.makeText(mContext,"le dictionnaire a été désinstallé ...",Toast.LENGTH_SHORT);
                //toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER_VERTICAL,0,0);
                toast.show();
                dialog.cancel();
            }
        });


        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onLongClick(View v) {
        int tag = (int) v.getTag();
        //Log.d("TABULA listeadaptDD","long click");
        String dic = dictionnaires.get(tag);
        ClipData data = ClipData.newPlainText("tag",""); //ClipData.newPlainText("tag",(String) v.getTag());
        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
        boolean res = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            res = v.startDragAndDrop(data,shadowBuilder,v,0);
        }
        else {
            res = v.startDrag(data,shadowBuilder,v,0);
        }

        return true;
    }
}




