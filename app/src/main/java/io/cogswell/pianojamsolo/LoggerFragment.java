package io.cogswell.pianojamsolo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * LoggerFragment is a class that implements the log view.
 */
public class LoggerFragment extends Fragment {
    private static Fragment frag;
    public static TextView textViewLoggingTarget;

    /**
     * Default constructor
     */
    public LoggerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    public void onResume () {
        super.onResume();
        TabLayoutActivity.highlight(false);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        // Capture tab change events: isVisibleToUser==true means the tab is visible.
        if (isVisibleToUser) {
            TabLayoutActivity.highlight(false);
        }
    }

    /**
     * Method for creating the LoggerFragment View. This is called by the Android
     * system.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_logger_fragment, container, false);
        frag = this;

        textViewLoggingTarget = (TextView)view.findViewById(R.id.logText);
        textViewLoggingTarget.setMovementMethod(new ScrollingMovementMethod());
        textViewLoggingTarget.append(".\n");

        return view;
    }

    private static int LOG_TRIM_THRESHOLD = 1572864; // 1.5 megabyte
    private static int LOG_TRIM_SIZE = 1048576; // 1 megabyte

    /**
     * Method to log an error.
     *
     * @param message  The message to log.
     */
    public static void logError(String message) {
        log(message, false, true);
    }

    /**
     * Method to log a non-error.
     *
     * @param message  The message to log.
     */
    public static void log(final String message) {
        log(message, false, false);
    }

    private static void log(final String message, final boolean fromLogger, final boolean isError) {
        if (frag !=null && frag.getActivity()!=null) {
            frag.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    try {

                        CharSequence text = textViewLoggingTarget.getText();

                        if (text.length() >= LOG_TRIM_THRESHOLD) {
                            System.out.println("trimming log.");

                            LoggerFragment.textViewLoggingTarget.setText(text.subSequence(text.length() - LOG_TRIM_SIZE, text.length()));
                            LoggerFragment.textViewLoggingTarget.append("Log trimmed." + "\n");
                        }

                        LoggerFragment.textViewLoggingTarget.append(message + "\n");

                        if (isError) {
                            TabLayoutActivity.highlight(true);
                        }
                    } catch (Throwable error) {
                        // Don't want an infinite chain of failures
                        if (!fromLogger) {
                            log(message + "\n" + ExceptionUtils.formatStackTrace(error), true, isError);
                        }
                    }
                }
            });
        } else {
            Log.d("LoggerFragment", "Could not log the following to the UI: "+message);
        }
    }
}
