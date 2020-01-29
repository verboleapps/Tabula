package com.verbole.dcad.tabula;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SearchView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTabHost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dcad on 1/16/18.
 */

public class ActivitePrincipale extends FragmentActivity implements TabHost.OnTabChangeListener, Fragment_FlashCard.OnFinTestListener //,TabLayout.OnTabSelectedListener
{
    private static final int PERMISSION_REQUEST_CODE = 1;
    private List<String> tabTitles = new ArrayList<>();
    private FragmentTabHost tabHost;
    String TAG = " TABULA ActivitePPale";
    int screenWidth;
    int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principale_tabs);

        if (Build.VERSION.SDK_INT >= 23)
        {
            if (checkPermission())
            {
                // Code for above or equal 23 API Oriented Device
                // Your Permission granted already .Do next code
            } else {
                requestPermission(); // Code for permission
            }
        }
        else
        {
            // Code for Below 23 API Oriented Device
            // Do next code
        }

        if( BuildConfig.BUILD_TYPE.contentEquals( "debug" ) && Build.VERSION.SDK_INT >= 28){
            StrictMode.setThreadPolicy( new StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build());
            StrictMode.setVmPolicy( new StrictMode.VmPolicy.Builder()
                    .detectNonSdkApiUsage()
                    .penaltyLog()
                    .build());
        }


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;
        Log.d(TAG,"get screen size : h " + String.valueOf(screenHeight) + " - w " + String.valueOf(screenWidth));


        tabTitles.add("Dictionnaire");
        tabTitles.add("Cartes");
        tabTitles.add("Textes");
        tabTitles.add("Grammaire");
        tabTitles.add("Information");
//https://stackoverflow.com/questions/5799320/android-remove-space-between-tabs-in-tabwidget/5804436#5804436
      //  https://maxalley.wordpress.com/2012/10/27/android-styling-the-tabs-in-a-tabwidget/
        // Set a toolbar which will replace the action bar.
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        // Setup the Tabs
  //      tabHost = findViewById(R.id.mTabHost);
  //      tabHost.setup(this,getSupportFragmentManager(),R.id.rtabcontent);

        /*
        tabHost.addTab(tabHost.newTabSpec("tab1").setIndicator(tabTitles.get(0)),FragmentDictionnaire.class, null);
        tabHost.addTab(tabHost.newTabSpec("tab2").setIndicator(tabTitles.get(1)), FragmentTextes.class, null);
        tabHost.addTab(tabHost.newTabSpec("tab3").setIndicator(tabTitles.get(2)), FragmentGrammaire.class, null);
        tabHost.addTab(tabHost.newTabSpec("tab4").setIndicator(tabTitles.get(3)), FragmentAide.class, null);
        */

        tabHost.addTab(getTabSpec("tab1",tabTitles.get(0)),FragmentDictView.class,null);
        tabHost.addTab(getTabSpec("tab2",tabTitles.get(1)),FragmentCartes.class,null);
        tabHost.addTab(getTabSpec("tab3",tabTitles.get(2)),FragmentTextes.class,null);
        tabHost.addTab(getTabSpec("tab4",tabTitles.get(3)),FragmentGrammaire.class,null);
        tabHost.addTab(getTabSpec("tab5",tabTitles.get(4)),FragmentAide.class,null);

        float s = getResources().getDimension(R.dimen.taille_police_tabulations);
        Log.d(TAG,"get ressources : taille police bt toolbars : " + s);
        //textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 65);
        //textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.my_text_size_in_sp));

        tabHost.setCurrentTab(0);
        tabHost.setOnTabChangedListener(this);


        GestionFichiers GF = new GestionFichiers(this);
        GF.creeDirectoryStorage(this);

        //dbHelper db = new dbHelper(this);

    }

    TabHost.TabSpec getTabSpec(String tag, String titre) {
        TabHost.TabSpec tabspec = tabHost.newTabSpec(tag);
        View v = LayoutInflater.from(this).inflate(R.layout.tabulation_layout,null);
        TextView tv = v.findViewById(R.id.tabulation_text_view);
        tv.setText(titre.toUpperCase());


        if (screenWidth < 500) {
            tv.setTextSize(10);
        }

        tabspec.setIndicator(v);
        return tabspec;

    }

    @Override
    public void onTabChanged(String tabId) {
        Log.d(TAG," tab : " + tabId + " changed");
        //if (tabId.equals("tab2")) {
            View pv = tabHost.getChildAt(0);
            if (pv != null) {
                //FragmentDictionnaire fg = (FragmentDictionnaire) getSupportFragmentManager().findFragmentByTag("tab1");
                FragmentDictView fg = (FragmentDictView) getSupportFragmentManager().findFragmentByTag("tab1");
                if (fg != null) {

                    SearchView br = fg.barreRecherche;
                    if (br != null) {
                        fg.barreRecherche.clearFocus();
                    }
                    /*
                    if (fg.MeF != null) {
                        fg.MeF.flex.fermeDBs();
                    }
                    */
                }

            }


    }

    void fermeBases() {
        String tb = tabHost.getCurrentTabTag();
        if (tb.equals("tab2")) {
            FragmentDictView fg = (FragmentDictView) getSupportFragmentManager().findFragmentByTag("tab1");
           // FragmentDictionnaire fg = (FragmentDictionnaire) getSupportFragmentManager().findFragmentByTag("tab1");
            /*
            if (fg != null) {
                if (fg.MeF != null) {
                    fg.MeF.flex.fermeDBs();
                }
            }

             */
        }
        if (tb.equals("tab1")) {
            FragmentTextes fg = (FragmentTextes) getSupportFragmentManager().findFragmentByTag("tab2");
            if (fg != null) {
                if (fg.MeF != null) {
                    fg.MeF.flex.fermeDBs();
                }
            }
        }
        if (tb.equals("tab3")) {
            FragmentCartes fg = (FragmentCartes) getSupportFragmentManager().findFragmentByTag("tab3");
            if (fg != null) {
                if (fg.MeF != null) {
                    fg.MeF.flex.fermeDBs();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "on Destroy ");
        fermeBases();
    }

    @Override
    public void onStart() {
        super.onStart();
        //Log.d(TAG, "on start ");
    }

    @Override
    public void onStop() {
        super.onStop();
        //fermeBases();
        Log.d(TAG, "on stop ");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "on pause ");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "on resume");
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ActivitePrincipale.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(ActivitePrincipale.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Toast.makeText(ActivitePrincipale.this, "La permission d'accès en écriture permet de créer le dossier \"Tabula\" et d'y copier des fichiers. Veuillez accorder cette permission.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(ActivitePrincipale.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission accordée, vous pouvez maintenant utiliser le répertoire local.");
                } else {
                    Log.e("value", "Permission refusée, vous ne pouvez pas utiliser le répertoire local.");
                }
                break;
        }
    }

    @Override
    public void finTest() {

        FragmentCartes fg = (FragmentCartes) getSupportFragmentManager().findFragmentByTag("tab2");
        if (fg != null) {

            fg.frLay.setVisibility(View.VISIBLE);
            fg.mToolBar.setVisibility(View.VISIBLE);
            fg.frLayFC.setVisibility(View.GONE);
            fg.etat = FragmentCartes.ETAT.LISTELISTES;
            fg.boutonRevient.setEnabled(false);
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG,"back ???");
            //return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
