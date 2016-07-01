package io.cogswell.pianojamsolo;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * PianoFragment is the class that implements the piano keyboard. It is
 * responsible for creating the displayable fragment and hooking it up
 * to the pub sub backend. The sounds and color changes of the piano are
 * controlled here as well. It also provides listeners for controls.
 */
public class PianoFragment extends Fragment {

    String defaultRoomName = "notes";

    private static final int COLOR_WHITE = Color.parseColor("#ffffff");
    private static final int COLOR_BLACK = Color.parseColor("#000000");

    private static final int COLOR_1  = Color.parseColor("#c02e1d");
    private static final int COLOR_2  = Color.parseColor("#d94e1f");
    private static final int COLOR_3  = Color.parseColor("#f16c20");
    private static final int COLOR_4  = Color.parseColor("#ef8b2c");
    private static final int COLOR_5  = Color.parseColor("#ecaa38");

    private static final int COLOR_6  = Color.parseColor("#ebc844");
    private static final int COLOR_7  = Color.parseColor("#a2b86c");
    private static final int COLOR_8  = Color.parseColor("#5ca793");
    private static final int COLOR_9  = Color.parseColor("#1395ba");
    private static final int COLOR_10 = Color.parseColor("#117899");
    private static final int COLOR_11 = Color.parseColor("#0f5b78");
    private static final int COLOR_12 = Color.parseColor("#0d3c55");

    private LocalPubSub pubsub = new LocalPubSub();
    private PublishInterface publisher = pubsub;
    private SubscribeInterface subscriber = pubsub;

    /**
     * KeyProfile is the class that represents a piano key.
     */
    private class KeyProfile {
        private String keyName;
        private int keyColor;
        private int activeColor;
        private Integer buttonId;
        private Button button;
        private int soundPoolId;

        /**
         * Constructor for KeyProfile
         *
         * @param keyName      the name of the key
         * @param keyColor     the color of the key when not pressed or playing
         * @param activeColor  the color of the key when pressed or playing
         * @param buttonId     the id of the button representing the key
         * @param soundId      the id of the sound the key plays
         * @throws Exception
         */
        public KeyProfile(String keyName, int keyColor, int activeColor, Integer buttonId, Integer soundId) throws Exception {
            this.keyName = keyName;
            this.keyColor = keyColor;
            this.activeColor = activeColor;
            this.buttonId = buttonId;
            this.button = (Button) rootView.findViewById(buttonId);
            if (soundPool!=null) {
                soundPoolId = soundPool.load(PianoFragment.this.getContext(), soundId, 1);
            }
        }

        public String getKeyName() { return keyName; }
        public int getKeyColor() { return keyColor; }
        public int getActiveColor() { return activeColor; }
        public Integer getButtonId() { return buttonId; }
        public Button getButton() { return button; }
    }

    private SoundPool soundPool=null;

    private HashMap<String, KeyProfile> nameToKey;
    View rootView;

    /**
     * Method for creating the PianoFragment View. This is called by the Android
     * system during launch of the app.
     */
    @Override
    @TargetApi(21)
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            soundPool = new SoundPool.Builder().setMaxStreams(15).setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()).build();
        } catch (Throwable t) {
            LoggerFragment.logError("Your phone does not support the sound api in this app.");
        }
        rootView = inflater.inflate(R.layout.activity_main, container, false);

        AudioManager audioManager =
                (AudioManager)PianoFragment.this.getActivity().getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

        ImageView cogswellIcon = (ImageView) getActivity().findViewById(R.id.cogswellIcon);

        cogswellIcon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cogswell.io"));
                startActivity(browserIntent);
                return true;
            }
        });

        try {
            nameToKey = new LinkedHashMap<>();

            KeyProfile[] keys = new KeyProfile[]{
                    new KeyProfile("C", COLOR_WHITE, COLOR_6, R.id.cKeyButton, R.raw.piano_mf_c4),
                    new KeyProfile("C#", COLOR_BLACK, COLOR_1, R.id.cSharpKeyButton, R.raw.piano_mf_db4),
                    new KeyProfile("D", COLOR_WHITE, COLOR_7, R.id.dKeyButton, R.raw.piano_mf_d4),
                    new KeyProfile("D#", COLOR_BLACK, COLOR_2, R.id.dSharpKeyButton, R.raw.piano_mf_eb4),
                    new KeyProfile("E", COLOR_WHITE, COLOR_8, R.id.eKeyButton, R.raw.piano_mf_e4),
                    new KeyProfile("F", COLOR_WHITE, COLOR_9, R.id.fKeyButton, R.raw.piano_mf_f4),
                    new KeyProfile("F#", COLOR_BLACK, COLOR_3, R.id.fSharpKeyButton, R.raw.piano_mf_gb4),
                    new KeyProfile("G", COLOR_WHITE, COLOR_10, R.id.gKeyButton, R.raw.piano_mf_g4),
                    new KeyProfile("G#", COLOR_BLACK, COLOR_4, R.id.gSharpKeyButton, R.raw.piano_mf_ab4),
                    new KeyProfile("A", COLOR_WHITE, COLOR_11, R.id.aKeyButton, R.raw.piano_mf_a4),
                    new KeyProfile("A#", COLOR_BLACK, COLOR_5, R.id.aSharpKeyButton, R.raw.piano_mf_bb4),
                    new KeyProfile("B", COLOR_WHITE, COLOR_12, R.id.bKeyButton, R.raw.piano_mf_b4)
            };

            for (KeyProfile key : keys) {
                eventOnKeyPress(key.getButtonId(), key);
                nameToKey.put(key.getKeyName(), key);
            }
        } catch (Exception e) {
            Logging.error("Unable to initialize media", e);
        }

        final Button aboutButton = (Button) getActivity().findViewById(R.id.aboutButton);

        final View popupView = inflater.inflate(R.layout.about_fragment, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(popupView);
        TextView popupTextView = (TextView) popupView.findViewById(R.id.aboutTextView);
        popupTextView.setText(Html.fromHtml(getResources().getString(R.string.about_text)));
        popupTextView.setMovementMethod(LinkMovementMethod.getInstance());
        popupTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        // When you click the about button, display a popup.
        aboutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (popupWindow.isShowing()) {
                    popupWindow.dismiss();
                } else {
                    popupWindow.showAsDropDown(aboutButton, 0, 0);
                }
            }
        });

        sub(defaultRoomName);

        return rootView;
    }

    private void sendKeyEvent(KeyProfile key) {
        try {
            final String room = ((EditText) getActivity().findViewById(R.id.userSelectedRoomName)).getText().toString();
            publisher.publish(room, key.getKeyName(), new Callback<String>() {
                        @Override
                        public void call(String response) {
                            if (!response.equals(room)) {
                                Logging.error("Event send failed after publishing.");
                            }
                        }
                    });
        } catch (final Throwable t) {
            Logging.error("Event Send Failed while creating request", t);
        }
    }

    private View.OnTouchListener makeTouchListener(final KeyProfile key) {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Button keyButton = (Button) v;
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                            Logging.info("Sending Key Event: " + key.getKeyName());
                            playColor(key.getKeyName());
                            sendKeyEvent(key);

                        break;
                    case MotionEvent.ACTION_UP:
                            keyButton.setBackgroundColor(key.getKeyColor());
                        break;
                }

                return false;
            }
        };
    }

    private void eventOnKeyPress(int id, KeyProfile key) {
        Button button = (Button) rootView.findViewById(id);
        if (button != null)
            button.setOnTouchListener(makeTouchListener(key));
    }

    private void playSound(String nodeName) {
        KeyProfile key = nameToKey.get(nodeName);
        if (soundPool != null) {
            soundPool.play(key.soundPoolId, 1, 1, 1, 0, 1);
        }
    }

    private void playColor(String nodeName) {
        KeyProfile key = nameToKey.get(nodeName);
        if (key.getButton()!=null) {
            key.getButton().setBackgroundColor(key.getActiveColor());
            final Handler handler = new Handler();
            final KeyProfile keyFinal = key;
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    keyFinal.getButton().setBackgroundColor(keyFinal.getKeyColor());
                }
            }, 500);
        }
    }

    private void sub(final String newRoomName){
        subscriber.subscribe(
                newRoomName,
                new Callback<String>() {
                    @Override
                    public void call(final String key) {
                        getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                Logging.info("Received Key Message: ' Message: '" + key + "'");

                                playSound(key);
                                playColor(key);
                            }
                        });
                    }
                },
                new Callback<String>() {
                    @Override
                    public void call(String room) {
                        if(room.equals(newRoomName)) {
                            Logging.info("Joined room " + room);
                        } else {
                            Logging.info("Failed to join room " + room);
                        }
                    }
                }
        );
    }
}
