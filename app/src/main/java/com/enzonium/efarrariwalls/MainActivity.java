package com.enzonium.efarrariwalls;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.enzonium.efarrariwalls.about.About;
import com.enzonium.efarrariwalls.util.IabHelper;
import com.enzonium.efarrariwalls.util.IabResult;
import com.enzonium.efarrariwalls.util.Inventory;
import com.enzonium.efarrariwalls.util.Purchase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "WallsHD.Main";

    SharedPreferences sp;
    public final static String MONETIZATION = "com.enzonium.efarrariwalls.mHas";
    public final static String RETURN_INDEX = "com.enzonium.efarrariwalls.globalPoisiton";

    //In app billing specific globals
    public static boolean mHasAdFree;
    private final static int IN_APP_BILLING_REQUEST_ID = 108;
    private final static String IN_APP_BILLING_PURCHASE_ID_AD_FREE = "efarrari";
    IabHelper mHelper;

    //Search specific globals
    EditText searchBox;
    TextWatcher mTextWatcher;


    //RecyclerView specific globals
    protected RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    Toolbar mToolbarGrid;
    AsynchronousSearchHandler activeQuery;

    //There are global variables above other code chunks.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainactivity_layout);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        monetizationManagement();
        setLayoutManager();

        mAdapter = new WallpaperRecyclerAdapter(WallpaperInstance.activeData, this);

        new LoadingWallpaperContent().execute();

        contentLoadingFrame = (RelativeLayout) findViewById(R.id.contentloading);
        resonantOpaque = (ImageView) findViewById(R.id.resonant_opaque);
        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        Typeface medium = Typeface.createFromAsset(getAssets(), "robotomedium.ttf");
        toolbarText.setTypeface(medium);
        mToolbarGrid = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbarGrid);

        searchBox = ((EditText)findViewById(R.id.searchBox));

        prepListeners();

        searchBox.addTextChangedListener(mTextWatcher);


        //In App billing Setup

        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnb95+HPnwZz5phwLeip7vYIB7NLMHsT15bvCm7Xlq8+82vVUoZCXFJ8sN0p7zEdjd7L5WCgNKoGdauP0q8+4fAEzb+lK7zRSIHAdZ0lH//7YR5pDz2lqFNOpxxz1CVvsUVpkQtYKuDvAu906MbPablqAYU8XxV1MbzUj/HV+vt5Cgkz3+/h3Z2m2MSHt3FUt1i6PKCsRv6cuH90EPbq8krEq+GfFxRlhKcNkojNaWbNFiVJef0FkoE6U3QnnU2hLZmTQkH/eg/fchDRWVg9cEGSDWFTKRZfNwnfJAtpLcyl7Z0yVuVl+Vz4iOTDyKik0G+KmSq+z7DfNLKTUnG0T9QIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(false);
        if(mHelper != null)
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }

                Log.d(TAG, "Setup successful. Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });


    }


    /**
     * Dispatch onResume() to fragments.  Note that for better inter-operation
     * with older versions of the platform, at the point of this call the
     * fragments attached to the activity are <em>not</em> resumed.  This means
     * that in some cases the previous state may still be saved, not allowing
     * fragment transactions that modify the state.  To correctly interact
     * with fragments in their proper state, you should instead override
     * {@link #onResumeFragments()}.
     */
    @Override
    protected void onResume() {
        super.onResume();

//        int scrollToPosition = getIntent().getIntExtra(Supreme.ON_RESUME_MAIN_SCROLL_POSITION,-1);
//
//        if(scrollToPosition != -1) {
//            mRecyclerView.scrollToPosition(scrollToPosition);
//            Log.d(TAG, "onResume: " + scrollToPosition);
//        }

    }

    private void setLayoutManager(){
        if( getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT|| getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT){
            mLayoutManager = new LinearLayoutManager(getApplicationContext());
        }
        if( getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE|| getScreenOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE) {
//            mLayoutManager = new StaggeredGridLayoutManager(2 , StaggeredGridLayoutManager.VERTICAL);
            mLayoutManager = new LinearLayoutManager(getApplicationContext());

        }
        mRecyclerView.setLayoutManager(mLayoutManager);
    }


    protected void prepListeners() {


        //creates new text watcher to refresh search results every keystroke
        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


                if (activeQuery == null || activeQuery.isRunning == null || !activeQuery.isRunning) {

                    activeQuery = new AsynchronousSearchHandler();
                    activeQuery.execute("" + s);

                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        };

        mRecyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int pos) {

                        WallpaperInstance wallpaperInstance = ((WallpaperRecyclerAdapter) mAdapter).getDataset().get(pos);

//                        view.findViewById(R.id.progress).setVisibility(View.VISIBLE);

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(searchBox.getWindowToken(), 0);

                        Intent toDetailIntent = new Intent(MainActivity.this, DetailActivity.class);
                        toDetailIntent.putExtra(DetailActivity.EXTRA_PARAM_ID, wallpaperInstance.getId());
                        toDetailIntent.putExtra(DetailActivity.EXTRA_PARAM_INDEX, pos);


                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                            ActivityCompat.startActivity(MainActivity.this, toDetailIntent, ActivityOptionsCompat.makeCustomAnimation(MainActivity.this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle());
                        else
                            startActivity(toDetailIntent);

                    }

                }));

        searchBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {


                if (activeQuery == null) {
                    activeQuery = new AsynchronousSearchHandler();
                }
                if (activeQuery.isRunning == null)
                    activeQuery = new AsynchronousSearchHandler();
                if (!activeQuery.isRunning)
                    activeQuery.execute(v.getText().toString().toLowerCase().trim());


                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if(getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null)
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                return false;


            }

        });
    }



    void alert(String title, String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setTitle(title);
        bld.setPositiveButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }


    private void monetizationManagement() {
        if (sp.getBoolean(MONETIZATION, false)) {

            findViewById(R.id.banner).setVisibility(View.GONE);
            findViewById(R.id.adContainer).setVisibility(View.GONE);
            findViewById(R.id.linearContainer).requestLayout();


        } else {
            AdView mAdView = (AdView) findViewById(R.id.banner);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);
        }

        if (!sp.getBoolean("FIRST_TIME", false)) {

            alert("Welcome to @efarrari HD!", "We've worked hard to cultivate this gallery and we hope you enjoy. Don't forget to rate and comment so we can make our app even better!");
            sp.edit().putBoolean("FIRST_TIME", true).apply();

        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + ","
                + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...


            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");
            if (result.isFailure()) {
                complain("Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            Purchase plus = inventory.getPurchase(IN_APP_BILLING_PURCHASE_ID_AD_FREE);
            mHasAdFree = (plus != null && verifyDeveloperPayload(plus));
            Log.d(TAG, " "
                    + (mHasAdFree ? "User has gone Ad Free!"
                    : "User has not gone Ad Free."));



            sp.edit().putBoolean(MONETIZATION, mHasAdFree).apply();


            monetizationManagement();


            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
        }

    };


    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        //We no longer generate purchases;
        return p != null;
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: "
                    + purchase);
            if (result.isFailure()) {


                complain("Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");





            if (purchase.getSku().equals(IN_APP_BILLING_PURCHASE_ID_AD_FREE)) {
                Log.d(TAG, "Purchase is Ad Free; Congratulating user");
                alert("Successful Purchase!", "Thanks! Ads will be gone the next time you load the app.");
                mHasAdFree = true;

                sp.edit().putBoolean(MONETIZATION, mHasAdFree).apply();


            }


            }

       // }
    };

    void complain(String message) {
        Log.d(TAG, "IAB Error: " + message);
    }



    int getHeight() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager)getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }



    protected int getScreenOrientation() {
        int rotation = getWindowManager().getDefaultDisplay().getRotation();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        int orientation;
        // if the device's natural orientation is portrait:
        if ((rotation == Surface.ROTATION_0
                || rotation == Surface.ROTATION_180) && height > width ||
                (rotation == Surface.ROTATION_90
                        || rotation == Surface.ROTATION_270) && width > height) {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                default:
                    Log.d(TAG, "Unknown screen orientation. Defaulting to " +
                            "portrait.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
            }
        }
        // if the device's natural orientation is landscape or if the device
        // is square:
        else {
            switch(rotation) {
                case Surface.ROTATION_0:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
                case Surface.ROTATION_90:
                    orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                    break;
                case Surface.ROTATION_180:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                    break;
                case Surface.ROTATION_270:
                    orientation =
                            ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                    break;
                default:
                    Log.d(TAG, "Unknown screen orientation. Defaulting to " +
                            "landscape.");
                    orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                    break;
            }
        }

        return orientation;
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(sp.getBoolean(MONETIZATION, false) ? R.menu.menu_perfecto_premium:R.menu.menu_perfecto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        switch (id) {

            case R.id.action_adfree:

                String payload = "";
                if (mHelper != null) {
                    mHelper.flagEndAsync();
                    mHelper.launchPurchaseFlow(MainActivity.this, IN_APP_BILLING_PURCHASE_ID_AD_FREE, IN_APP_BILLING_REQUEST_ID,
                            mPurchaseFinishedListener, payload);
                }
                return true;


            case R.id.action_search:
                searchView(true);
                return true;


            case R.id.action_follow:
                instagram();
                return true;

            case R.id.action_share:
                share();
                return true;

            case R.id.action_rate:
                googlePlay();
                return true;

            case R.id.action_about:
                startActivity(new Intent(MainActivity.this, About.class));
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    private void searchView(boolean enableSearchView) {

        mToolbarGrid.getMenu().clear();


        if(enableSearchView) {

            searchBox.requestFocus();

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(searchBox, InputMethodManager.SHOW_FORCED);


            mToolbarGrid.setNavigationIcon(R.drawable.back_arrow);

            mToolbarGrid.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchView(false);
                }
            });


        }else{

            mToolbarGrid.setNavigationIcon(null);



            getMenuInflater().inflate(sp.getBoolean(MONETIZATION, false) ? R.menu.menu_perfecto_premium:R.menu.menu_perfecto, mToolbarGrid.getMenu());

            //resets data source
            mAdapter = new WallpaperRecyclerAdapter(WallpaperInstance.activeData, MainActivity.this);
            mRecyclerView.setAdapter(mAdapter);

        }

        findViewById(R.id.searchLayout).setVisibility(enableSearchView ? View.VISIBLE:View.GONE);
        findViewById(R.id.aestheticsLayout).setVisibility(enableSearchView ? View.GONE:View.VISIBLE);


    }

    public void googlePlay() {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("market://details?id=" + getPackageName()));
        startActivity(i);

    }

    public void instagram() {

        String url = "http://www.instagram.com/efarrari";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    public void share(){

        Intent i = new Intent(Intent.ACTION_SEND);
        i.putExtra(Intent.EXTRA_TEXT, "Check out @Efarrari HD, http://goo.gl/ViTGwz");
        i.setType("plain/text");
        startActivity(i);

    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "******onDestroy");
        // unregisterReceiver(mStateReceiver);
        super.onDestroy();
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null)
            mHelper.dispose();

        mHelper = null;

        finish();
    }


    /*
    *@showLoadingAnimation()
    *Delays loading anmiation for 1 second; if the content loading is completed before the 1 second, the animation can be blocked by updating global variable delayedIndicator to false.
    */
    private boolean delayedIndicator = false;


    public void showLoadingAnimation(){
        delayedIndicator = true;
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(delayedIndicator){
                    findViewById(R.id.contentloading).setVisibility(View.VISIBLE);
                    startAnim();
                }
            }
        }, 1000);}

    ObjectAnimator alphaResonant;
    ImageView resonantOpaque;

    void startAnim(){

        alphaResonant = ObjectAnimator.ofFloat(resonantOpaque, "alpha", 0f, 1f, 0f);

        alphaResonant.setRepeatMode(ValueAnimator.RESTART);
        alphaResonant.setRepeatCount(ValueAnimator.INFINITE);
        alphaResonant.setDuration(1500);
        alphaResonant.start();

    }

    void stopAnim() {

        if(alphaResonant != null)
            alphaResonant.cancel();

        alphaResonant = ObjectAnimator.ofFloat(resonantOpaque, "alpha", resonantOpaque.getAlpha(), 0f);
        alphaResonant.setDuration((long)(400*(resonantOpaque.getAlpha())));
        alphaResonant.start();

    }

    Snackbar noConnection;
    RelativeLayout contentLoadingFrame;

    void showSnack() {

        noConnection =
                Snackbar.with(this)
                        .type(SnackbarType.SINGLE_LINE)
                        .text("Connection Error.")

                        .actionLabel("RETRY")
                        .actionColor(Color.parseColor("#fac400"))
                        .duration(Snackbar.SnackbarDuration.LENGTH_INDEFINITE)
                        .actionListener(new ActionClickListener() {
                            @Override
                            public void onActionClicked(Snackbar snackbar) {
                                resetContentLoading();
                                new LoadingWallpaperContent().execute();
                                snackbar.dismiss();
                            }
                        });
        noConnection.setBackgroundColor(Color.parseColor("#000000"));

        ColorDrawable cd = new ColorDrawable();
        cd.setColor(getResources().getColor(android.R.color.black));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            noConnection.setBackground(cd);
        SnackbarManager.show(noConnection, contentLoadingFrame);

    }

    void resetContentLoading(){
        findViewById(R.id.contentloading).setVisibility(View.VISIBLE);
        findViewById(R.id.noWalls).setVisibility(View.GONE);

    }

    private static final String TAG_POST_ID = "_id";
    private static final String TAG_THUMB = "thumb";
    private static final String TAG_IMAGE = "url";
    private static final String TAG_PACK = "pack";
    private static final String TAG_SENSOR = "sensor";
    private static final String TAG_NAME = "name";
    private static final String TAG_DATE = "date";
    private static final String TAG_LOCATION = "location";


    //Asynchronous content loading class.
    private class LoadingWallpaperContent extends AsyncTask<Void, Object, Boolean> {

        @Override
        protected void onPreExecute() {
            Log.d(TAG, "in Pre Execute");
            if(WallpaperInstance.activeData == null || WallpaperInstance.activeData.size() <= 0)
            WallpaperInstance.activeData = new ArrayList<>();
            mAdapter = new WallpaperRecyclerAdapter(WallpaperInstance.activeData, getApplicationContext());

            showLoadingAnimation();

            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Object... values) {
            mAdapter.notifyDataSetChanged();
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Void... arg0) {
            if(WallpaperInstance.activeData == null || WallpaperInstance.activeData.size() <= 0) {


                JSONArray jsonArray = NetworkAbstractionLayer.getJSONArrayfromURL(NetworkAbstractionLayer.SOURCE_ROUTE);
                try {

                    if (jsonArray == null) return false;
                    Log.e(TAG, jsonArray.toString());
                    for (int i = jsonArray.length() - 1; i >= 0; i--) {
                        JSONObject wallpaperJSONObj = jsonArray.getJSONObject(i);
                        //TODO: opt string for all for upload fault tolerence;
                        String id = wallpaperJSONObj.getString(TAG_POST_ID);
                        String thumb = wallpaperJSONObj.optString(TAG_THUMB);
                        String image = wallpaperJSONObj.optString(TAG_IMAGE);
                        String pack = wallpaperJSONObj.getString(TAG_PACK);
                        String sensor = wallpaperJSONObj.getString(TAG_SENSOR);
                        String name = wallpaperJSONObj.getString(TAG_NAME);
                        String date = wallpaperJSONObj.getString(TAG_DATE);
                        String location = wallpaperJSONObj.getString(TAG_LOCATION);

                        WallpaperInstance activeItem = new WallpaperInstance(pack, name, location, date, sensor, image, thumb, id);
                        WallpaperInstance.activeData.add(activeItem);
                        publishProgress(i, activeItem);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mAdapter = new WallpaperRecyclerAdapter(WallpaperInstance.activeData, getApplicationContext());
                mRecyclerView.setAdapter(mAdapter);
                mRecyclerView.scrollToPosition(getIntent().getIntExtra(DetailActivity.ON_RESUME_MAIN_SCROLL_POSITION,0));

            delayedIndicator = false;


            if(mAdapter.getItemCount()==0 && result){
                //no walls - leave logo; no reload snack
                findViewById(R.id.noWalls).setVisibility(View.VISIBLE);
                stopAnim();

            }
            else if(mAdapter.getItemCount()==0 && !result){
                //loading failure - leave logo; show snack
                findViewById(R.id.noWalls).setVisibility(View.GONE);
//                findViewById(R.id.progress).setVisibility(View.GONE);
                stopAnim();
                showSnack();

            }
            else if(mAdapter.getItemCount()>0){
                //walls - hide contentloading aggregate
                stopAnim();
                findViewById(R.id.contentloading).setVisibility(View.GONE);
            }
            stopAnim();

            Log.d(TAG, "in postexecute Status: "+result);

        }


    }

//TODO            return NetworkAbstractionLayer.getBitmapFromURL((String) params[1]);



    private class AsynchronousSearchHandler extends AsyncTask<String, Object, Boolean> {

        Boolean isRunning = false;


        ArrayList<WallpaperInstance> queryResponse = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isRunning = true;

        }

        @Override
        protected Boolean doInBackground(String... params) {

            //maps letters to lowercase and removes leading and trailing spaces from user query.
            params[0] = params[0].toLowerCase().trim();


            if (params[0].length() > 0) {
                for (Searchable11 searchable : WallpaperInstance.activeData) {
                    Log.d(TAG + ".search", searchable.getSearchString());
                    if (searchable.getSearchString().toLowerCase().contains(params[0].toLowerCase()))
                        queryResponse.add((WallpaperInstance) searchable);
                }
            }
            return true;
        }


        @Override
        protected void onPostExecute(final Boolean success) {
            super.onPostExecute(success);


            if (success) {

                mAdapter = new WallpaperRecyclerAdapter(queryResponse, MainActivity.this);

                if (mRecyclerView != null)
                    mRecyclerView.setAdapter(mAdapter);

                Log.d(TAG + ".search", "INCLUDED ELEMENTS");

                for(Searchable11 element:queryResponse)
                    Log.d(TAG + ".search", element.getSearchString());
            }

            isRunning = null;

        }
    }


    }
