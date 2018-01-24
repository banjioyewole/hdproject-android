package com.enzonium.efarrariwalls;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;


/**
 * Created by banjioyewole on 1/1/16.
 *
 * AdapterFFG (Adapter for Featured Fragement Grid) was created to support the transition to viewholder and recyclerview pattern.
 */

 class WallpaperRecyclerAdapter extends RecyclerView.Adapter<WallpaperRecyclerAdapter.ViewHolder>{
    private ArrayList<WallpaperInstance> mDataset;
    private Context applicationContext;

    private ImageLoader imageLoader;

    ArrayList<WallpaperInstance> getDataset() {
        return mDataset;
    }


    WallpaperRecyclerAdapter(ArrayList<WallpaperInstance> mDataset, Context applicationContext) {
        this.mDataset = mDataset;
        this.applicationContext = applicationContext;
        MySingleton volleySingleton = MySingleton.getInstance(applicationContext);
        imageLoader = volleySingleton.getImageLoader();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        ScaleImageView mScaleImageView;
        TextView mTextView;
        ViewHolder(View v) {
            super(v);
            mScaleImageView = (ScaleImageView) v.findViewById(R.id.imageview_item);
            mTextView =  (TextView) v.findViewById(R.id.imagename);

            int accentColor = v.getContext().getResources().getColor(R.color.accent);
            ((ProgressBar) v.findViewById(R.id.progress)).getIndeterminateDrawable().setColorFilter(accentColor, PorterDuff.Mode.SRC_IN);


        }
    }


    /**
     * Called when RecyclerView needs a new {@link ViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ViewHolder, int)}. Since it will be re-used to display different
     * items in the data set, it is a good idea to cache references to sub views of the View to
     * avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     * @see #onBindViewHolder(ViewHolder, int)
     */
    @Override
    public WallpaperRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.grid_item, parent, false);
        ViewHolder vh = new ViewHolder(view);

        Typeface type = Typeface.createFromAsset(applicationContext.getAssets(),"reef.otf");
        vh.mTextView.setTypeface(type);
        vh.mTextView.setAllCaps(true);

        return vh;
    }



    /**
     * Called by RecyclerView to display the data at the specified position. This method
     * should update the contents of the {@link ViewHolder#itemView} to reflect the item at
     * the given position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this
     * method again if the position of the item changes in the data set unless the item itself
     * is invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside this
     * method and should not keep a copy of it. If you need the position of an item later on
     * (e.g. in a click listener), use {@link ViewHolder#getAdapterPosition()} which will have
     * the updated adapter position.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {

        final WallpaperInstance wallpaperInstance = mDataset.get(position);
        holder.mTextView.setText(wallpaperInstance.getName());

        if(wallpaperInstance.getThumb() != null) {

            Drawable bitmapDrawable = new BitmapDrawable(applicationContext.getResources(), wallpaperInstance.getThumb());

            holder.mScaleImageView.setImageDrawable(bitmapDrawable);

        }else {
             imageLoader.get(wallpaperInstance.getThumbnailUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    Drawable bitmapDrawable = new BitmapDrawable(applicationContext.getResources(), response.getBitmap());
                    holder.mScaleImageView.setImageDrawable(bitmapDrawable);
                    wallpaperInstance.setThumb(response.getBitmap());

                    Animation animation = AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in);
                    holder.mScaleImageView.startAnimation(animation);
                }

                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


}
