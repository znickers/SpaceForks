package ca.unb.mobiledev.spaceforks;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TableRow;
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

public class GameOverActivity extends AppCompatActivity {
    // _Definitions_
    public static final String TAG = "GameOverActivity";
    public static final String FRAGMENT_TAG = "HighScoreFragment";
    public static final String PREVNAME_KEY = "prevName";
    private DBManager dbManager;
    private ExecutorService executor;
    private Handler handler;
    public String newName;
    public int highscore;
    public int gamescore;

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
        setContentView(R.layout.gameover_menu);

        // Initialize executor, handler, and database services
        executor = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());
        dbManager = new DBManager(this);

        //Retrieve highscore
        TextView tvHighScore = findViewById(R.id.highscore);
        getHighScore(tvHighScore); // Stores int in highscore variable and updates textview

        // Retrieve gamescore
        Intent startIntent = getIntent();
        gamescore = startIntent.getIntExtra("gamescore",0);

        // Set gamescore textview
        TextView tvScore = findViewById(R.id.score);
        tvScore.setText(Integer.toString(gamescore));

        // Trigger highscore fragment
        TextView tvGameOver = findViewById(R.id.gameover_header);
        TableRow highscoreRow = findViewById(R.id.highscore_row);
        TableRow scoreRow = findViewById(R.id.score_row);
        tvGameOver.setVisibility(View.INVISIBLE);
        highscoreRow.setVisibility(View.INVISIBLE);
        scoreRow.setVisibility(View.INVISIBLE);
        replaceFragment(HighScoreFragment.newInstance(Integer.toString(gamescore), MainMenuActivity.getPrevName()));

        // Retry button behaviour
        Button retryButton = findViewById(R.id.retry_button);
        retryButton.setOnClickListener( v -> {
            Intent intent = new Intent(this, GameActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Quit button behaviour
        Button quitButton = findViewById(R.id.quit_button);
        quitButton.setOnClickListener( v -> {
            Intent intent = new Intent(this, MainMenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Listen for name from fragment
        getSupportFragmentManager().setFragmentResultListener("fromHighScoreFragment", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(String requestKey, Bundle bundle) {
                // Retrieve leaderboard name entry from fragment
                newName = bundle.getString("nameKey");
                SharedPreferences.Editor editor = MainMenuActivity.prefs.edit();
                MainMenuActivity.prevName = newName;
                editor.putString(PREVNAME_KEY, newName);
                editor.apply();

                // Update leaderboard database in new thread
                addItem(newName, Integer.toString(gamescore));

                // Restore gameover layout
                removeFragment(getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG));
                tvGameOver.setVisibility(View.VISIBLE);
                highscoreRow.setVisibility(View.VISIBLE);
                scoreRow.setVisibility(View.VISIBLE);

                // Notify player that leaderboard has been updated
                Toast toast = Toast.makeText(GameOverActivity.this, "Leaderboard Updated", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    // _OnDestroy()_
    @Override
    public void onDestroy() {
        super.onDestroy();
        dbManager.close();
    }

    //_Private_Methods_
    private void getHighScore(TextView textView) {
        executor.execute(() -> {
            int hiscore = dbManager.getHighScore();
            handler.post(() -> {
                textView.setText(Integer.toString(hiscore));
                highscore = hiscore;
            });
        });
    }

    private void addItem(String player, String score) {
        executor.execute(() -> {
            // Perform background call to save the record
            dbManager.insertRecord(player, score);
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container2, fragment, FRAGMENT_TAG);
        fragmentTransaction.commit();
    }

    private void removeFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();
    }
}

