package ca.unb.mobiledev.spaceforks.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ca.unb.mobiledev.spaceforks.MainMenuActivity;
import ca.unb.mobiledev.spaceforks.R;

public class OptionsMenuFragment extends Fragment {
    // _Definitions_
    private static final String ARG_TILT = MainMenuActivity.TILT_ENABLED_KEY;
    private static final String ARG_AUDIO = MainMenuActivity.AUDIO_ENABLED_KEY;
    private static final String ARG_MUSIC = MainMenuActivity.MUSIC_ENABLED_KEY;
    private static final String ARG_PRACTICE = MainMenuActivity.PRACTICE_ENABLED_KEY;
    private boolean tiltControlsEnabled;
    private boolean audioEffectsEnabled;
    private boolean musicEnabled;
    private boolean practiceModeEnabled;

    // _Constructors_
    public OptionsMenuFragment() { }

    public static OptionsMenuFragment newInstance(boolean tiltEnabled, boolean audioEnabled,
                                                  boolean musicEnabled, boolean practiceEnabled) {
        OptionsMenuFragment fragment = new OptionsMenuFragment();
        Bundle args = new Bundle();
        args.putBoolean(ARG_TILT, tiltEnabled);
        args.putBoolean(ARG_AUDIO, audioEnabled);
        args.putBoolean(ARG_MUSIC, musicEnabled);
        args.putBoolean(ARG_PRACTICE, practiceEnabled);
        fragment.setArguments(args);
        return fragment;
    }

    // _OnCreate_
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Retrieve instantiation parameters
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tiltControlsEnabled = getArguments().getBoolean(ARG_TILT);
            audioEffectsEnabled = getArguments().getBoolean(ARG_AUDIO);
            musicEnabled = getArguments().getBoolean(ARG_MUSIC);
            practiceModeEnabled = getArguments().getBoolean(ARG_PRACTICE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.options_menu_fragment, container, false);

        // Retrieve game settings and initialize button states
        ToggleButton toggle1 = view.findViewById(R.id.toggle1);
        toggle1.setChecked(tiltControlsEnabled);
        ToggleButton toggle2 = view.findViewById(R.id.toggle2);
        toggle2.setChecked(audioEffectsEnabled);
        ToggleButton toggle3 = view.findViewById(R.id.toggle3);
        toggle3.setChecked(musicEnabled);
        ToggleButton toggle4 = view.findViewById(R.id.toggle4);
        toggle4.setChecked(practiceModeEnabled);

        // Toggle button 1 behaviour
        toggle1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tiltControlsEnabled = true;
                } else {
                    tiltControlsEnabled = false;
                }
            }
        });

        // Toggle button 2 behaviour
        toggle2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    audioEffectsEnabled = true;
                } else {
                    audioEffectsEnabled = false;
                }
            }
        });

        // Toggle button 3 behaviour
        toggle3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    musicEnabled = true;
                } else {
                    musicEnabled = false;
                }
            }
        });

        // Toggle button 4 behaviour
        toggle4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    practiceModeEnabled = true;
                } else {
                    practiceModeEnabled = false;
                }
            }
        });

        // Back button behaviour
        FloatingActionButton backButton = view.findViewById(R.id.options_back_button);
        backButton.setOnClickListener( v -> {
            Bundle result = new Bundle();
            result.putBoolean(ARG_TILT, tiltControlsEnabled);
            result.putBoolean(ARG_AUDIO, audioEffectsEnabled);
            result.putBoolean(ARG_MUSIC, musicEnabled);
            result.putBoolean(ARG_PRACTICE, practiceModeEnabled);
            getParentFragmentManager().setFragmentResult("fromOptionsMenuFragment", result);
        });

        // Return layout
        return view;
    }
}