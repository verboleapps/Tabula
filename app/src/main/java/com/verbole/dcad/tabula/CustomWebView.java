package com.verbole.dcad.tabula;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

/**
 * Created by dcad on 3/24/16.
 */

public class CustomWebView extends WebView  //implements GestureDetector.OnGestureListener
{
    private Context context;
    WebAppInterface wAppInt;
    PopupWindow resultat;
    WebView webRes;
    Boolean resVisible = false;
    Point pointTouche = new Point(100,100);
    private GestureDetector mDetector;

    String javascr;

    // override all other constructor to avoid crash
    public CustomWebView(Context context) {
        super(context);
        this.context = context;
        WebSettings webviewSettings = getSettings();
        webviewSettings.setJavaScriptEnabled(true);
        // add JavaScript interface for copy

        this.wAppInt = new WebAppInterface(context);
        addJavascriptInterface(wAppInt, "JSInterface");
        LinearLayout ll = new LinearLayout(context);
        webRes = new WebView(context);
        webRes.setBackgroundColor(Color.LTGRAY);
        //ww.loadData(res, "text/html", "utf-8");
        ll.addView(webRes);
        resultat = new PopupWindow(ll,500,250);
        resultat.setContentView(ll);

       // mDetector = new GestureDetectorCompat(context,this);
       // CustomActionModeCallback mActionModeCallback = new CustomActionModeCallback();
    }

  //  private ActionMode mActionMode;
  // private ActionMode.Callback mSelectActionModeCallback;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       // this.mDetector.onTouchEvent(event);
        pointTouche.set((int) event.getX(), (int) event.getY());

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

    String ResultatHtml = "";

    public void AfficheResRecherche(String res) {
       // Flexions flex = new Flexions(context);
       // String res = flex.rechercheHtmlDsTexte(texte, false, 250, 10);
        webRes.setVisibility(INVISIBLE);
        //ww.loadData(res, "text/html", "utf-8");
        webRes.loadDataWithBaseURL(null, res, "text/html", "utf-8", null);
        webRes.setWebViewClient(new WebViewClient() {

            public void onPageFinished(WebView view, String url) {
                // do your stuff here
                webRes.setVisibility(VISIBLE);
            }
        });

        if (pointTouche.y > this.getHeight() - 250) {
            pointTouche.y -= 150;
        }
        else {
            pointTouche.y += 250;
        }

        resultat.showAtLocation(this, 50, pointTouche.x, pointTouche.y);
        resVisible = true;
    }

    public void montreResRech() {
        resultat.showAtLocation(this, 50, pointTouche.x, pointTouche.y);
        resVisible = true;
    }
}