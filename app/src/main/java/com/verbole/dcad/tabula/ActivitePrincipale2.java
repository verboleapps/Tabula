package com.verbole.dcad.tabula;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.StrictMode;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.Map;

public class ActivitePrincipale2 extends AppCompatActivity implements TabLayout.OnTabSelectedListener, Fragment_FlashCard.OnFinTestListener {

    String[] tabTitles = {"Dictionnaire","Cartes","Textes","Grammaire","Information"}; //Information

    private static final int PERMISSION_REQUEST_CODE = 1;

    TabLayout tabLayout;
    ViewPagerSimple viewPager;
    ViewPagerAdapter viewPagerAdapter;

    static String TAG = "TABULA_tag ";
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


        float s = getResources().getDimension(R.dimen.taille_police_tabulations);
        Log.d(TAG,"get ressources : taille police bt toolbars : " + s);
        //textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 65);
        //textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.my_text_size_in_sp));

        GestionFichiers GF = new GestionFichiers(this);
        GF.creeDirectoryStorage(this);


        viewPager = findViewById(R.id.viewPager);
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(this);

        //dbHelper db = new dbHelper(this);

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(ActivitePrincipale2.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(ActivitePrincipale2.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            //Toast.makeText(ActivitePrincipale.this, "La permission d'accès en écriture permet de créer le dossier \"Tabula\" et d'y copier des fichiers. Veuillez accorder cette permission.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(ActivitePrincipale2.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
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
    public void onTabSelected(TabLayout.Tab tab) {
        Log.d(ActivitePrincipale2.TAG,TAG + "onTabSelected ... " + tab.getPosition());
        hideSoftKeyBoard();
        viewPager.setCurrentItem(tab.getPosition(),false);
        if (tab.getPosition() == 1) {
            // reinit liste cartes car onResume n'est pas appelee si tab precedente 0
            String tag = viewPagerAdapter.mFragmentTags.get(1);
            FragmentCartesGeneral fcg = (FragmentCartesGeneral) getSupportFragmentManager().findFragmentByTag(tag);
            if (fcg != null) {
                fcg.reinitListeListes();
            }

        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //hideSoftKeyBoard();
        if (tab.getPosition() == 0) {
            String tag = viewPagerAdapter.mFragmentTags.get(0);
            FragmentDictView frd = (FragmentDictView) getSupportFragmentManager().findFragmentByTag(tag);
            if (frd != null) {
                frd.barreRecherche.clearFocus();
            }

        }
        if (tab.getPosition() == 1) {
            viewPagerAdapter.saveState();
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        if (tab.getPosition() == 1) {

        }

    }

    private void hideSoftKeyBoard() {
        Log.d(ActivitePrincipale2.TAG,TAG + "hide soft kb");
        InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            if(imm.isAcceptingText()) { // verify if the soft keyboard is open
                View v = getCurrentFocus();
                if (v != null) {
                    IBinder ib = v.getWindowToken();
                    if (ib != null) {
                        imm.hideSoftInputFromWindow(ib,0);
                        //imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
                    }
                    else {
                        // Log.d(ActivitePrincipale2.TAG, TAG + "ibinder null ");
                    }
                }
                else {
                   // Log.d(ActivitePrincipale2.TAG, TAG + "view focus null ");
                }

            }
            else {
               // Log.d(ActivitePrincipale2.TAG, TAG + " imm not accepting text");
            }
        }
        else {
           // Log.d(ActivitePrincipale2.TAG,TAG + "input method manager null ...");
        }

    }

    @Override
    public void finTest() {
        Log.d(ActivitePrincipale2.TAG,TAG + "fin test FC ... ");

    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private Map<Integer, String> mFragmentTags;


        public ViewPagerAdapter(FragmentManager fm) {
           // super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT); androidx.appcompat:appcompat:1.1.0 ??
            super(fm);
            mFragmentTags = new HashMap<Integer, String>();
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return new FragmentDictView();

                case 1:
                    return new FragmentCartesGeneral();

                case 2:
                    return new FragmentTextes();

                case 3:
                    return new FragmentGrammaire();

                case 4:
                    return new FragmentAide();

                default:
                    return null;
            }
        }

        // Will be displayed as the tab's label
        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public Parcelable saveState() {
            Log.d(TAG,"save state ??");
            return super.saveState();

            //return null;
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
            super.restoreState(state,loader);
            Log.d(TAG,"restore state ??");
        }


        // Returns total number of pages
        @Override
        public int getCount() {
            return 5;
        }

        @NonNull
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            Object obj = super.instantiateItem(container, position);
            if (obj instanceof Fragment) {
                // record the fragment tag here.
                Fragment f = (Fragment) obj;
                String tag = f.getTag();
                mFragmentTags.put(position, tag);
            }
            return obj;
        }
    }

    boolean actionRevient() {
        int sel = tabLayout.getSelectedTabPosition();
        String tag = viewPagerAdapter.mFragmentTags.get(sel);

        if (tag != null) {
            //Log.d(TAG,"tag : " + tag + " sel : " + sel);
            if (sel == 0) {
                FragmentDictView frd = (FragmentDictView) getSupportFragmentManager().findFragmentByTag(tag);
                return frd.revient();
            }
            if (sel == 1) {

                FragmentCartesGeneral fcg = (FragmentCartesGeneral) getSupportFragmentManager().findFragmentByTag(tag);
                return fcg.revient();
            }
            if (sel == 2) {
                FragmentTextes frc = (FragmentTextes) getSupportFragmentManager().findFragmentByTag(tag);
                return frc.revient();
            }
            if (sel == 3) {
                //FragmentGrammaire frg = (FragmentGrammaire) viewPagerAdapter.getItem(3);

            }
            if (sel == 4) {
                FragmentAide fra = (FragmentAide) getSupportFragmentManager().findFragmentByTag(tag);
                return fra.revient();
            }
        }
        return true;

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Log.d(TAG,"back ???");
            boolean r = actionRevient();
           // return false; = on remonte pas + haur
            if (!r) {
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
