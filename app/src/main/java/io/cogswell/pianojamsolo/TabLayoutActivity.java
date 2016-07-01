package io.cogswell.pianojamsolo;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * TabLayoutActivity is a class that implements the functionality
 * of the tabs.
 */
public class TabLayoutActivity extends AppCompatActivity {
    public static void highlight(boolean isHighlighting) {

        View circleView = (View) activity.findViewById(R.id.circle);

        GradientDrawable bgShape = (GradientDrawable)circleView.getBackground();
        //bgShape.setColor(isHighlighting ? Color.rgb(99, 0, 0) : Color.BLACK);
        bgShape.setColor(isHighlighting ? Color.RED : Color.BLACK);
    }

    private static TabLayoutActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_layout);

        activity = this;
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        ViewPager pager = (ViewPager) findViewById(R.id.pager);
        TabsPagerAdapter adapter = new TabsPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);
    }
}
