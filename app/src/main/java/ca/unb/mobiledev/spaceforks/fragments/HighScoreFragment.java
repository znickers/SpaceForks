package ca.unb.mobiledev.spaceforks.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ca.unb.mobiledev.spaceforks.R;

public class HighScoreFragment extends Fragment {
    // _Definitions_
    private static final String ARG_GAMESCORE = "gamescore";
    private static final String ARG_PREVNAME = "prevName";
    private String gamescore;
    private String prevName;

    // _Constructors_
    public HighScoreFragment() { }

    public static HighScoreFragment newInstance(String gamescore, String prevName) {
        HighScoreFragment fragment = new HighScoreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_GAMESCORE, gamescore);
        args.putString(ARG_PREVNAME, prevName);
        fragment.setArguments(args);
        return fragment;
    }

    // _OnCreate_
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Retrieve instantiation parameters
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            gamescore = getArguments().getString(ARG_GAMESCORE);
            prevName = getArguments().getString(ARG_PREVNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.highscore_fragment, container, false);

        // Set score and previous name
        TextView tvScore = view.findViewById(R.id.gamescore_header);
        EditText editName = view.findViewById(R.id.player_name_edit);
        tvScore.setText(gamescore);
        editName.setText(prevName);

        // Submit button behaviour
        Button submitBtn = view.findViewById(R.id.submit_button);
        submitBtn.setOnClickListener( v -> {
            Bundle result = new Bundle();
            result.putString("nameKey",editName.getText().toString());
            getParentFragmentManager().setFragmentResult("fromHighScoreFragment",result);
        });

        // Return layout
        return view;
    }
}