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

    void loadPDF2(final String filePath) {
        // loadUrl("file:///android_asset/pdfviewer/index.html?file=" + filePath);
        loadUrl("file:///android_asset/pdfviewer/web/viewer.html");
    }
    void loadPDF(final String filePath) {
        String vp2 = "compressed.tracemonkey-pldi-09.pdf";
        GestionFichiers gf = new GestionFichiers(context);
        String viewerhtml = gf.chargeFichiersAssets("pdfviewer/web","viewer","html");
        viewerhtml = viewerhtml.replace("DEFAULT_URL = 'compressed.tracemonkey-pldi-09.pdf'","DEFAULT_URL = '" + filePath + "'");

        loadDataWithBaseURL("file:///android_asset/pdfviewer/web/viewer.html",viewerhtml,"text/html", "utf-8", null);

    }


    void getAct(Activity act) {
        mActivity = act;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }


}