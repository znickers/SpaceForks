package ca.unb.mobiledev.spaceforks;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ca.unb.mobiledev.spaceforks.database.DBManager;
import ca.unb.mobiledev.spaceforks.fragments.HighScoreFragment;
import ca.unb.mobiledev.spaceforks.fragments.LeaderboardFragment;
import ca.unb.mobiledev.spaceforks.fragments.OptionsMenuFragment;

public class MainMenuActivity extends AppCompatActivity {
    // _Definitions_
    // Tags and Keys
    public static final String TAG = "MainMenuActivity";
    public static final String OPTIONS_FRAGMENT_TAG = "OptionsMenuFragment";
    public static final String LEADERBOARD_FRAGMENT_TAG = "LeaderboardFragment";
    public static final String PREFS_FILENAME = "SpaceForksAppPrefs";
    public static final String TILT_ENABLED_KEY = "tiltControlsEnabled";
    public static final String AUDIO_ENABLED_KEY = "audioEffectsEnabled";
    public static final String MUSIC_ENABLED_KEY = "musicEnabled";
    public static final String PRACTICE_ENABLED_KEY = "practiceModeEnabled";
    public static final String PREVNAME_KEY = "prevName";

    // Game Settings
    public static boolean tiltControlsEnabled;
    public static boolean audioEffectsEnabled;
    public static boolean musicEnabled;
    public static boolean practiceModeEnabled;

    // Other
    public static String prevName;
    Button startButton;
    Button optionsButton;
    Button leaderboardButton;
    TextView tvTitle;

    // Utils
    public static SharedPreferences prefs;
    private DBManager dbManager;
    private ExecutorService executor;
    private Handler handler;

    // _onCreate()_
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();

        // Initialize layout
        setContentView(R.layout.main_menu);
        startButton = findViewById(R.id.start_button);
        optionsButton = findViewById(R.id.options_button);
        leaderboardButton = findViewById(R.id.leaderboard_button);
        tvTitle = findViewById(R.id.title_header);

        // Initialize database, executor, and handler services
        dbManager = new DBManager(this);
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        // Retrieve shared preferences
        initSharedPreferences();

        // Set long press listener for title header
        tvTitle.setOnLongClickListener( v -> {
            // Toggle practice mode
            practiceModeEnabled = !practiceModeEnabled;
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(PRACTICE_ENABLED_KEY, practiceModeEnabled);
            editor.apply();

            // Notify player of practice mode state
            Toast toast;
            if (practiceModeEnabled) {
                toast = Toast.makeText(MainMenuActivity.this, "Practice Mode Enabled", Toast.LENGTH_SHORT);
            } else {
                toast = Toast.makeText(MainMenuActivity.this, "Practice Mode Disabled", Toast.LENGTH_SHORT);
            }
            toast.show();

            return true;
        });

        // Start button behaviour
        startButton.setOnClickListener( v -> {
            Intent intent = new Intent(this, GameActivity.class);
            startActivity(intent);
        });

        // Options button behaviour
        optionsButton.setOnClickListener( v -> {
            disableButtons();
            replaceFragment(OptionsMenuFragment.newInstance(tiltControlsEnabled, audioEffectsEnabled,
                            musicEnabled, practiceModeEnabled), OPTIONS_FRAGMENT_TAG);
            Log.d(TAG, "Start options menu!!!");
         });

        // Leaderboard button behaviour
        leaderboardButton.setOnClickListener( v -> {
            disableButtons();
            replaceFragment(LeaderboardFragment.newInstance(dbManager, executor, handler),
                            LEADERBOARD_FRAGMENT_TAG);
            Log.d(TAG,"Start leaderboard menu!!!");
        });

        // Listen for options menu fragment results
        getSupportFragmentManager().setFragmentResultListener("fromOptionsMenuFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(String requestKey, Bundle bundle) {
                // Retrieve updated settings from fragment
                tiltControlsEnabled = bundle.getBoolean(TILT_ENABLED_KEY);
                audioEffectsEnabled = bundle.getBoolean(AUDIO_ENABLED_KEY);
                musicEnabled = bundle.getBoolean(MUSIC_ENABLED_KEY);
                practiceModeEnabled = bundle.getBoolean(PRACTICE_ENABLED_KEY);

                // Update shared preferences
                SharedPreferences.Editor editor = prefs.edit();
                editor.putBoolean(TILT_ENABLED_KEY, tiltControlsEnabled);
                editor.putBoolean(AUDIO_ENABLED_KEY, audioEffectsEnabled);
                editor.putBoolean(MUSIC_ENABLED_KEY, musicEnabled);
                editor.putBoolean(PRACTICE_ENABLED_KEY, practiceModeEnabled);
                editor.apply();

                // Remove options menu fragment
                removeFragment(getSupportFragmentManager().findFragmentByTag(OPTIONS_FRAGMENT_TAG));
                enableButtons();
            }
        });

        // Listen for leaderboard fragment results
        getSupportFragmentManager().setFragmentResultListener("fromLeaderboardFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(String requestKey, Bundle bundle) {
                // Remove options menu fragment
                removeFragment(getSupportFragmentManager().findFragmentByTag(LEADERBOARD_FRAGMENT_TAG));
                enableButtons();
            }
        });
    }

    // _OnDestroy()_
    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }

    // _Public_Methods_
    public static String getPrevName() {
        return prevName;
    }

    // _Private_Methods_
    private void initSharedPreferences() {
        // Retrieve shared preferences object
        prefs = getSharedPreferences(PREFS_FILENAME, Context.MODE_PRIVATE);

        // Retrieve game settings (defaults are second argument)
        tiltControlsEnabled = prefs.getBoolean(TILT_ENABLED_KEY, true);
        audioEffectsEnabled = prefs.getBoolean(AUDIO_ENABLED_KEY, true);
        musicEnabled = prefs.getBoolean(MUSIC_ENABLED_KEY, true);
        practiceModeEnabled = prefs.getBoolean(PRACTICE_ENABLED_KEY, false);
        prevName = prefs.getString(PREVNAME_KEY, "SpaceCadet");

        // Perform first-time setup
        boolean firstStart = prefs.getBoolean("firstStart", true);
        if (firstStart) {
            // Populate leaderboard
            addHighScore("P. Quill", Integer.toString(1000));
            addHighScore("H. Solo", Integer.toString(100));
            addHighScore("M. Watney", Integer.toString(10));

            // Do not perform again
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstStart", false);
            editor.apply();
        }
    }

    private void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container1, fragment, tag);
        fragmentTransaction.commit();
    }

    private void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }

    private void addHighScore(String player, String score) {
        executor.execute(() -> {
            // Perform background call to save the record
            dbManager.insertRecord(player, score);
        });
    }

    private void deleteHighScore(int id) {
        executor.execute(() -> {
            // Perform background call to remove the record
            dbManager.deleteRecord(id);
        });
    }

    private void disableButtons() {
        startButton.setEnabled(false);
        optionsButton.setEnabled(false);
        leaderboardButton.setEnabled(false);
    }

    private void enableButtons() {
        startButton.setEnabled(true);
        optionsButton.setEnabled(true);
        leaderboardButton.setEnabled(true);
    }
}