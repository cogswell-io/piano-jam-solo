package io.cogswell.pianojamsolo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * AboutFragment is a class that implements the about modal view.
 */
public class AboutFragment extends AppCompatActivity {

    /**
     * Default constructor
     */
    public AboutFragment() {
        // Required empty public constructor
    }

    /**
     * Method for creating the AboutFragment View. This is called by the Android
     * system.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = (TextView)findViewById(R.id.aboutTextView);
        textView.setText(Html.fromHtml(getResources().getString(R.string.about_text)));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
