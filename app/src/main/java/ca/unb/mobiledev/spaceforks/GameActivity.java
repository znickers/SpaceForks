package ca.unb.mobiledev.spaceforks;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

import ca.unb.mobiledev.spaceforks.game.GameView;


public class GameActivity extends AppCompatActivity {
    // _Definitions_

    // _onCreate()_
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set window
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        getSupportActionBar().hide();

        // Set view
        GameView gameView = new GameView(this);
        gameView.setFragmentManager(getSupportFragmentManager());
        setContentView(R.layout.game_activity_layout);
        FrameLayout frame = findViewById(R.id.game_frame);
        frame.addView(gameView);
    }
}