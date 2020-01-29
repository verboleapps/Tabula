package com.verbole.dcad.tabula;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

/**
 * Created by dcad on 3/24/16.
 */

public class WebViewPDF extends WebView //implements GestureDetector.OnGestureListener
{
    private Context context;
    WebAppInterface wAppInt;
    //PopupWindow resultat;
    //WebView webRes;
    //Boolean resVisible = false;
    //Point pointTouche = new Point(0,0);

    Activity mActivity;
    //private GestureDetectorCompat mDetector;

    String javascr;
    String laForme;

    public WebViewPDF(Context context) {
        super(context);
        init(context);
    }

    public WebViewPDF(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public WebViewPDF(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    /* possible hack
    cf https://stackoverflow.com/questions/41025200/android-view-inflateexception-error-inflating-class-android-webkit-webview/49024931#49024931
    ds ce cas remplacer les init par :
    super(getFixedContext(context));
    super(getFixedContext(context), attrs);
    super(getFixedContext(context), attrs, defStyle);

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public WebViewPDF(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(getFixedContext(context), attrs, defStyleAttr, defStyleRes);
    }

    private static Context getFixedContext(Context context) {
        if (Build.VERSION.SDK_INT >= 21 && Build.VERSION.SDK_INT < 23) // Android Lollipop 5.0 & 5.1
            return context.createConfigurationContext(new Configuration());
        return context;
    }
*/
    private void init(Context context) {
        this.context = context;

        // add JavaScript interface for copy

        this.wAppInt = new WebAppInterface(context);
        addJavascriptInterface(wAppInt, "JSInterface");
    }

    void loadPDF(final String filePath) {
        loadUrl("file:///android_asset/pdfviewer/index.html?file=" + filePath);
    }


    void getAct(Activity act) {
        mActivity = act;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    /*
    void getSelectionWebVue() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            String js2 = "(function (){" +
                    "var txt = window.getSelection().toString();" +
                     " JSInterface.getText(txt);" +
                    " return txt;" +
                    "})()";
            // calling the js function

            this.evaluateJavascript(js2,
                    new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            Log.d("on receive value",value);
                            laForme = value;

                        }
                    });
        } else {
            //vueWebTxt.loadUrl(urltxt);

            //loadUrl("javascript:" + js1);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        this.mDetector.onTouchEvent(event);
        pointTouche.set((int) event.getX(), (int) event.getY());

        if (resVisible) {
            resVisible = false;
            resultat.dismiss();
        }
        //Log.d("touche ","=== " + laForme);
        //getSelectionWebVue();

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
             //   _handler.postDelayed(_longPressed, LONG_PRESS_TIME);
                break;
            case MotionEvent.ACTION_MOVE:
                if (resVisible) {
                    resVisible = false;
                    resultat.dismiss();
                }
              //  _handler.removeCallbacks(_longPressed);
                break;
            case MotionEvent.ACTION_UP:
              //  _handler.removeCallbacks(_longPressed);
                break;
        }
       // return true;
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        //Log.d(DEBUG_TAG,"onDown: " + event.toString());
        return true;
    }


    @Override
    public boolean onFling(MotionEvent event1, MotionEvent event2,
                           float velocityX, float velocityY) {
        //Log.d(DEBUG_TAG, "onFling: " + event1.toString()+event2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
                            float distanceY) {
       // Log.d(DEBUG_TAG, "onScroll: " + e1.toString()+e2.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        //Log.d(DEBUG_TAG, "onSingleTapUp: " + event.toString());
        if (resVisible) {
        }
        return true;
    }

    public void AfficheResRecherche(String texte) {

        if (resVisible) {resultat.dismiss();}

        webRes.loadDataWithBaseURL(null, texte, "text/html", "utf-8", null);

        if (pointTouche.y > this.getHeight() - 250) {
            pointTouche.y -= 50;
        }
        else {
            pointTouche.y += 250;
        }

        resultat.showAtLocation(this, 50, pointTouche.x, pointTouche.y);
        resVisible = true;
    }
*/
}