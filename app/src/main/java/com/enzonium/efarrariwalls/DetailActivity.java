package com.enzonium.efarrariwalls;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.enzonium.efarrariwalls.util.IabHelper;
import com.enzonium.efarrariwalls.util.IabResult;
import com.enzonium.efarrariwalls.util.Inventory;
import com.enzonium.efarrariwalls.util.Purchase;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by banjioyewole on 3/22/15.
 * Detail View for wallpapers that shows other associated wallpapers and details about the images
 *
 */                         //ActionBarActivity
public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemClickListener  {
    public final static String MONETIZATION = "com.enzonium.efarrariwalls.mHas";
    public final static String ON_RESUME_MAIN_SCROLL_POSITION = "com.enzonium.efarrariwalls.globalPoisiton";

    IabHelper mHelper;
    static final int IN_APP_BILLING_REQUEST_ID = 108;
    public final String TAG = "WallsHD.Detail";

    public static boolean mHasAdFree;
    private final static String AD_FREE = "efarrari";


    private WallpaperGridAdapter mAdapter;

    public static final String EXTRA_PARAM_ID = "detail:_id";
    public static final String EXTRA_PARAM_INDEX = "detail:_index";

    private ScaleImageView mFeaturedImageView;
    private ProgressBar mProgressBar;
    private TextView mImageTitle;
    private TextView mImageLocation;
    private TextView mImageDate;
    private TextView mImageSensor;

    private TextView mMoreFrom;

    private WallpaperInstance wallpaperInstance;
    SharedPreferences sp;




    private void windowManagement() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void monetizationManagement(){
        if(sp.getBoolean(MONETIZATION, false)){

            findViewById(R.id.adParent).setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(findViewById(R.id.grid).getLayoutParams());
            lp.setMargins(0,0,0,0);
            findViewById(R.id.grid).setLayoutParams(lp);
        }else{
            AdView mAdView = (AdView) findViewById(R.id.banner);
            AdRequest adRequest = new AdRequest.Builder().build();
            mAdView.loadAd(adRequest);

        }
    }



int globosDisco = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        windowManagement();
        super.onCreate(savedInstanceState);

        setContentView(R.layout.detailactivity_layout);

        sp = PreferenceManager.getDefaultSharedPreferences(this);

        // Retrieve the correct Item instance, using the ID provided in the Intent
        wallpaperInstance = WallpaperInstance.findById(getIntent().getIntExtra(EXTRA_PARAM_ID, 0));



        mFeaturedImageView = (ScaleImageView) findViewById(R.id.imageview_header);
        mProgressBar = (ProgressBar) findViewById(R.id.progress);

        Log.e(TAG, "wallpaper: " + wallpaperInstance.getName() + "  has full? " + (wallpaperInstance.getImage() != null) + ".");

        if(wallpaperInstance.getImage() == null) {

            if(wallpaperInstance.getThumb() != null) {
                Drawable bitmapDrawable = new BitmapDrawable(getResources(), wallpaperInstance.getThumb());

                mFeaturedImageView.setImageDrawable(bitmapDrawable);
//                Snackbar.make(findViewById(R.id.detailParent), "used Thumb!", Snackbar.LENGTH_LONG).setAction("NICE!", null).show();
            }else{
//                Snackbar.make(findViewById(R.id.detailParent), "No thumb available.", Snackbar.LENGTH_LONG).setAction(":/", null).show();

            }


//            mProgressBar.setVisibility(View.VISIBLE);
            int accentColor = getResources().getColor(R.color.accent);
            mProgressBar.getIndeterminateDrawable().setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);


            MySingleton volleySingleton = MySingleton.getInstance(DetailActivity.this);
            ImageLoader imageLoader = volleySingleton.getImageLoader();

            Log.e(TAG, wallpaperInstance.getImageUrl().substring(wallpaperInstance.getImageUrl().length()-5));
            imageLoader.get(wallpaperInstance.getImageUrl(), new ImageLoader.ImageListener() {

                    @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        Log.e(TAG, "isImmediate: "+ isImmediate);
                        if(isImmediate)return;
//                        Snackbar.make(findViewById(R.id.detailParent), "Globos Disco: "+ globosDisco++ +" !", Snackbar.LENGTH_SHORT).setAction("NICE!", null).show();



                        BitmapDrawable bitmapDrawable = new BitmapDrawable(getResources(), response.getBitmap());
                    mFeaturedImageView.setImageDrawable(bitmapDrawable);
                    wallpaperInstance.setImage(bitmapDrawable);
                        Log.e(TAG, "wallpaper: " + wallpaperInstance.getName() + "  has full? " + (wallpaperInstance.getImage() != null) + ".");

                    Animation animation = AnimationUtils.loadAnimation(DetailActivity.this, android.R.anim.fade_in);
                    mFeaturedImageView.startAnimation(animation);

                    mProgressBar.setVisibility(View.GONE);

                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    mProgressBar.setVisibility(View.GONE);
//                    Toast.makeText(DetailActivity.this, "OHNOES", Toast.LENGTH_LONG).show();

                    Drawable bitmapDrawable = new BitmapDrawable(getResources(), wallpaperInstance.getThumb());

                    mFeaturedImageView.setImageDrawable(bitmapDrawable);
                }
            });

        }else {
            mProgressBar.setVisibility(View.GONE);

            mFeaturedImageView.setImageDrawable(wallpaperInstance.getImage());
        }



        mImageTitle = (TextView) findViewById(R.id.imageTitle);
        mImageLocation = (TextView) findViewById(R.id.imageLocation);
        mImageDate = (TextView) findViewById(R.id.imageDate);
        mImageSensor = (TextView) findViewById(R.id.imageSensor);

        mMoreFrom = (TextView) findViewById(R.id.packname);

        TextView mSensorTitle = (TextView) findViewById(R.id.sensorTitle);
        TextView mDateTitle =  (TextView) findViewById(R.id.dateTitle);

        Typeface din = Typeface.createFromAsset(getAssets(), "din.ttf");
        Typeface reef = Typeface.createFromAsset(getAssets(), "reef.otf");

        mImageTitle.setTypeface(reef);
        mImageTitle.setTextColor(getResources().getColor(R.color.primary_text_color));
        mImageLocation.setTypeface(din);
        mImageLocation.setTextColor(getResources().getColor(R.color.dark_accent));
        mImageDate.setTypeface(din);
        mImageDate.setTextColor(getResources().getColor(R.color.secondary_text_color));
        mImageSensor.setTypeface(din);
        mImageSensor.setTextColor(getResources().getColor(R.color.secondary_text_color));

        mSensorTitle.setTypeface(din);
        mSensorTitle.setTextColor(getResources().getColor(R.color.secondary_text_color));

        mDateTitle.setTypeface(din);
        mDateTitle.setTextColor(getResources().getColor(R.color.secondary_text_color));

        mMoreFrom.setTypeface(din);
        mMoreFrom.setTextColor(getResources().getColor(R.color.primary_text_color));

        mImageTitle.setAllCaps(true);
        mSensorTitle.setAllCaps(true);
        mImageSensor.setAllCaps(true);
        mDateTitle.setAllCaps(true);
        mMoreFrom.setAllCaps(true);

        ImageView instaglyph, todownload, towallpaper;
        instaglyph = (ImageView) findViewById(R.id.toInstagram);
        todownload = (ImageView) findViewById(R.id.toDownload);
        towallpaper = (ImageView) findViewById(R.id.toWallpaper);
        instaglyph.setColorFilter(getResources().getColor(R.color.secondary_glyph_color));
        todownload.setColorFilter(getResources().getColor(R.color.secondary_glyph_color));
        towallpaper.setColorFilter(getResources().getColor(R.color.accent));


        //A back button that mimics the behavior of the Anroid Up-Button Pattern, but is implemented differently to achieve the intended design

        FrameLayout phonyBack = (FrameLayout) findViewById(R.id.phonyBack);
        phonyBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                onBackPressed();

                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra(MainActivity.RETURN_INDEX , getIntent().getExtras().getInt(EXTRA_PARAM_INDEX,0));

                    startActivity(intent);
                    finish();

            }
        });

        monetizationManagement();

        CustomHeightGridView mGridView = (CustomHeightGridView) findViewById(R.id.grid);
        mGridView.setOnItemClickListener(this);
        mAdapter = new WallpaperGridAdapter();
        mGridView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        loadItemDetails();


        instaglyph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                instagram();
            }
        });


        todownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToFile(loadFullSizeBitmap());
            }
        });

        towallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Uri imageUri  = setWall(loadFullSizeBitmap());

//                String filename = wallpaperInstance.getName().replace(" ", "_").toLowerCase();
                Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                intent.setDataAndType(imageUri, "image/*");
                intent.putExtra("jpg", "image/*");
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.set_as)), 808);


            }
        });


        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnb95+HPnwZz5phwLeip7vYIB7NLMHsT15bvCm7Xlq8+82vVUoZCXFJ8sN0p7zEdjd7L5WCgNKoGdauP0q8+4fAEzb+lK7zRSIHAdZ0lH//7YR5pDz2lqFNOpxxz1CVvsUVpkQtYKuDvAu906MbPablqAYU8XxV1MbzUj/HV+vt5Cgkz3+/h3Z2m2MSHt3FUt1i6PKCsRv6cuH90EPbq8krEq+GfFxRlhKcNkojNaWbNFiVJef0FkoE6U3QnnU2hLZmTQkH/eg/fchDRWVg9cEGSDWFTKRZfNwnfJAtpLcyl7Z0yVuVl+Vz4iOTDyKik0G+KmSq+z7DfNLKTUnG0T9QIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.enableDebugLogging(false);

        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: " + result);
                    return;
                }
                // Hooray, IAB is fully set up. Now, let's get an inventory of
                // stuff we own.
                Log.d(TAG, "Setup successful. Sequencer. //Gutter Music, Querying inventory.");
                mHelper.queryInventoryAsync(mGotInventoryListener);
            }
        });


    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    /* Checks if external storage is available for read and write */
//    public boolean isExternalStorageWritable() {
//        String state = Environment.getExternalStorageState();
//        return Environment.MEDIA_MOUNTED.equals(state);
//    }




    private boolean saveToFile (final Bitmap imageToFile){

        if(imageToFile == null)
            return false;


        String filename = wallpaperInstance.getName().replace(" ", "_").toLowerCase();

        File file;
        try {
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/EfarrariWalls";
            File dir = new File(file_path);
            if(dir.mkdirs())
                Log.d(TAG, "successful directory creation");
            file = new File(dir, filename + ".png");
            FileOutputStream fOut = new FileOutputStream(file);

            imageToFile.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        Snackbar.make(findViewById(R.id.detailParent), "Downloaded "+wallpaperInstance.getName() +"", Snackbar.LENGTH_LONG).setAction("Set Wallpaper", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri imageUri  = setWall(imageToFile);

                Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                intent.setDataAndType(imageUri, "image/*");
                intent.putExtra("jpg", "image/*");
                startActivityForResult(Intent.createChooser(intent,
                        getString(R.string.set_as)), 808);
            }
        }).setActionTextColor(getResources().getColor(R.color.accent)).show();

        return true;

    }


    private Uri setWall (Bitmap bmp){
        if(bmp ==null)return null;
        String filename = wallpaperInstance.getName().replace(" ", "_").toLowerCase();
        File file = null;
        try {
            String file_path = Environment.getExternalStorageDirectory().getAbsolutePath() +
                    "/EfarrariWalls";
            File dir = new File(file_path);
            if(dir.mkdirs())
                Log.d(TAG, "successful directory creation");
             file = new File(dir, filename + ".png");
            FileOutputStream fOut = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        }catch (Exception e) {
            e.printStackTrace();
        }
       return Uri.fromFile(file);

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

            Purchase plus = inventory.getPurchase(AD_FREE);
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





            if (purchase.getSku().equals(AD_FREE)) {
                Log.d(TAG, "Purchase is Ad Free; Congratulating user");
                mHasAdFree = true;
                sp.edit().putBoolean(MONETIZATION, mHasAdFree).apply();

                alert("Successful Purchase!", "Thanks! Ads will be gone the next time you load the app.");

            }


        }

        // }
    };



    void complain(String message) {
        Log.d(TAG, "IAB Error: " + message);
    }

    void alert(String title, String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setTitle(title);
        bld.setPositiveButton("OK", null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }






    private void loadItemDetails() {
        // Set the title TextView to the item's name and author
        //mImageTitle.setText(getString(R.string.image_header, wallpaperInstance.getName(), wallpaperInstance.getAuthor()));
        mImageTitle.setText(wallpaperInstance.getName());
        mImageLocation.setText(wallpaperInstance.getLocation());
        mImageDate.setText(wallpaperInstance.getDate());
        mImageSensor.setText(wallpaperInstance.getSensor());

        mMoreFrom.setText("More from " + wallpaperInstance.getPackName() + " Pack");

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();

    }

    Bitmap preSave = null;

//    /**
//     * Load the item's full-size image into our {@link android.widget.ImageView}.
//     */
//    private void loadFullSizeImage() {
//if(!loaded)
//        Picasso.with(mHeaderImageView.getContext())
//                .load(mItem.geImageUrl())
////                .noFade()
//                .into(mHeaderImageView, new Callback() {
//                    @Override
//                    public void onSuccess() {
//                        findViewById(R.id.scrollView).setScrollY(0);
//
//                    }
//
//                    @Override
//                    public void onError() {
//
//                    }
//                });
//
//
//    }

    private void loadThumbForGrid(final ScaleImageView toLoadInto, final WallpaperInstance wallpaperInstance){

        if(wallpaperInstance.getThumb() != null) {
            toLoadInto.setImageBitmap(wallpaperInstance.getThumb());
            return;
        }

        Target t = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                wallpaperInstance.setThumb(bitmap);
                toLoadInto.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(DetailActivity.this)
                .load(wallpaperInstance.getThumbnailUrl())
                .into(t);




    }

    private Bitmap loadFullSizeBitmap(){

        if(wallpaperInstance.getImage() != null)
            return wallpaperInstance.getImage().getBitmap();
        Target t = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                preSave = bitmap;
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        Picasso.with(DetailActivity.this)
                .load(wallpaperInstance.getImageUrl())
                .into(t);

        return preSave;
    }



    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        if(findViewById(R.id.scrollView)!=null) findViewById(R.id.scrollView).setScrollY(0);
super.onPostCreate(savedInstanceState);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
       if(findViewById(R.id.scrollView)!=null) findViewById(R.id.scrollView).setScrollY(0);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        // FontsOverride.setDefaultFont(this, "MONOSPACE", "avenir_l.ttf");

        WallpaperInstance wallpaperInstance = mAdapter.wallpaperInstances.get(position);//(ActiveItem) adapterView.getItemAtPosition(position);

//        view.findViewById(R.id.progress).setVisibility(View.VISIBLE);
//        new LoadDimensions().execute(view.findViewById(R.id.progress), wallpaperInstance.getImageUrl(), WallpaperInstance.getGlobalPosition(wallpaperInstance), wallpaperInstance);


        Intent nestedDetailIntent = new Intent(DetailActivity.this, DetailActivity.class);
            nestedDetailIntent.putExtra(DetailActivity.EXTRA_PARAM_INDEX, position);
            nestedDetailIntent.putExtra(DetailActivity.EXTRA_PARAM_ID, wallpaperInstance.getId());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            ActivityCompat.startActivity(DetailActivity.this, nestedDetailIntent, ActivityOptionsCompat.makeCustomAnimation(DetailActivity.this, R.anim.slide_in_right, R.anim.slide_out_left).toBundle());
        else
            startActivity(nestedDetailIntent);

    }


    /**
     * {@link android.widget.BaseAdapter} which displays items.
     */
    private class WallpaperGridAdapter extends BaseAdapter {

        ArrayList<WallpaperInstance> wallpaperInstances;
        WallpaperGridAdapter() {
            super();
            wallpaperInstances = WallpaperInstance.getPack(wallpaperInstance.getPackName());
            wallpaperInstances.remove(wallpaperInstance);
        }

        @Override
        public int getCount() {

            return wallpaperInstances.size();
            //wallpaperInstance.getCount(wallpaperInstance.getPackName());

        }

        @Override
        public WallpaperInstance getItem(int position) {
            return   wallpaperInstances.get(position);
        }

        @Override
        public long getItemId(int position) {
            return (long) position;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.grid_item, viewGroup, false);
            }

            final WallpaperInstance wallpaperInstance = getItem(position);
            // Load the thumbnail image
            ScaleImageView thumbImageView = (ScaleImageView) view.findViewById(R.id.imageview_item);
            loadThumbForGrid(thumbImageView, wallpaperInstance);

            int accentColor = getResources().getColor(R.color.accent);
            ((ProgressBar) view.findViewById(R.id.progress)).getIndeterminateDrawable().setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);

            return view;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(MONETIZATION, false)){
            getMenuInflater().inflate(R.menu.premium_ps, menu);

        }else {
            getMenuInflater().inflate(R.menu.menu_perfecto, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_follow) {

            instagram();

            return true;
        }

        if (id == R.id.action_adfree) {

            String payload = "";

            if (mHelper != null) mHelper.flagEndAsync();

            if(mHelper != null)
            mHelper.launchPurchaseFlow(DetailActivity.this, AD_FREE, IN_APP_BILLING_REQUEST_ID,
                    mPurchaseFinishedListener, payload);
            return true;

        }

        if (id == R.id.action_rate){


            googlePlay();


            return true;
        }

        if (id == R.id.action_share){

            share();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void googlePlay() {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse("market://details?id=" + getPackageName()));
        startActivity(i);

    }

    public void instagram() {

        String url = "http://www.instagram.com/banjioyewole";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);

    }

    public void share(){

        Intent i = new Intent(Intent.ACTION_MEDIA_SHARED);
        i.putExtra(Intent.EXTRA_TEXT, "Here's an app with some really cool wallpapers! http://goo.gl/ViTGwz");
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



}
