package ca.unb.mobiledev.spaceforks.fragments;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import ca.unb.mobiledev.spaceforks.GameActivity;
import ca.unb.mobiledev.spaceforks.MainMenuActivity;
import ca.unb.mobiledev.spaceforks.R;
import ca.unb.mobiledev.spaceforks.game.GameView;

public class PauseMenuFragment extends Fragment {
    // _Definitions_
    public static final String TAG = "PauseMenuFragment";
    private GameView gameView;

    // _Constructors_
    public PauseMenuFragment() { }

    public static PauseMenuFragment newInstance(GameView gameView) {
        PauseMenuFragment fragment = new PauseMenuFragment();
        fragment.setGameView(gameView);
        return fragment;
    }

    // _View_Behaviour_
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate layout
        View view = inflater.inflate(R.layout.pause_menu_fragment, container, false);
        Log.d(TAG, "Pause menu inflated!!!");

        // Resume button behaviour
        Button resumeBtn = view.findViewById(R.id.resume_button_pause);
        resumeBtn.setOnClickListener( v -> {
            Log.d(TAG, "Resume button pressed!!!");
            gameView.gameResume();
        });

        // Resume button behaviour
        Button restartBtn = view.findViewById(R.id.restart_button_pause);
        restartBtn.setOnClickListener( v -> {
            Log.d(TAG, "Restart button pressed!!!");
            Intent intent = new Intent(getActivity(), GameActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        });

        // Resume button behaviour
        Button quitBtn = view.findViewById(R.id.quit_button_pause);
        quitBtn.setOnClickListener( v -> {
            Log.d(TAG, "Quit button pressed!!!");
            Intent intent = new Intent(getActivity(), MainMenuActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        // Return layout
        return view;
    }

    private void setGameView(GameView gameView) {
        this.gameView = gameView;
    }
}