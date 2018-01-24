package com.enzonium.efarrariwalls;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.GridView;

/**
 * Created by banjioyewole on 4/4/15.
 */
public class CustomHeightGridView extends GridView {


    public CustomHeightGridView(Context context) {
        super(context);
    }


    public CustomHeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomHeightGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // HACK!  TAKE THAT ANDROID!
        //if (isExpanded()) {
        // Calculate entire height by providing a very large height hint.
        // View.MEASURED_SIZE_MASK represents the largest height possible.
        int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);

        ViewGroup.LayoutParams params = super.getLayoutParams();
        params.height = getMeasuredHeight();
    }
}
