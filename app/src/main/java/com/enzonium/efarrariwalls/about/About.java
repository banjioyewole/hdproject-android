package com.enzonium.efarrariwalls.about;

import android.graphics.Typeface;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.enzonium.efarrariwalls.FontsOverride;
import com.enzonium.efarrariwalls.R;

public class About extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FontsOverride.setDefaultFont(this, "SERIF", "reef.otf");

        setContentView(R.layout.about);

        TextView toolbarText = (TextView) findViewById(R.id.toolbar_text);
        Typeface medium = Typeface.createFromAsset(getAssets(), "robotomedium.ttf");
        toolbarText.setTypeface(medium);
        toolbarText.setAllCaps(true);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((Toolbar) findViewById(R.id.toolbar)).findViewById(R.id.icon).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavUtils.navigateUpFromSameTask(About.this);
            }
        });

        for (int i=0; i<((LinearLayout)findViewById(R.id.containx)).getChildCount(); i++){
            ((TextView)((FrameLayout)((LinearLayout)((LinearLayout)findViewById(R.id.containx)).getChildAt(i)).getChildAt(0)).getChildAt(0)).setTextSize(18f);
            ((TextView)((FrameLayout)((LinearLayout)((LinearLayout)findViewById(R.id.containx)).getChildAt(i)).getChildAt(1)).getChildAt(0)).setTextSize(14f);

        }

//        TODO Up Button; sans -> reef; title size modulations; add to menu
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
