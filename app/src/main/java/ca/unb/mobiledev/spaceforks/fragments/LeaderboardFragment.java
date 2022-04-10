package ca.unb.mobiledev.spaceforks.fragments;

import android.database.Cursor;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.ToggleButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.util.concurrent.ExecutorService;

import ca.unb.mobiledev.spaceforks.MainMenuActivity;
import ca.unb.mobiledev.spaceforks.R;
import ca.unb.mobiledev.spaceforks.database.DBManager;
import ca.unb.mobiledev.spaceforks.database.DatabaseHelper;
import ca.unb.mobiledev.spaceforks.game.GameView;

public class LeaderboardFragment extends Fragment {
    // _Definitions_
    private DBManager dbManager;
    private ExecutorService executor;
    private Handler handler;
    private ListView mListView;

    // _Constructors_
    public LeaderboardFragment() { }

    public static LeaderboardFragment newInstance(DBManager dbManager, ExecutorService executor, Handler handler) {
        LeaderboardFragment fragment = new LeaderboardFragment();
        fragment.setDBManager(dbManager);
        fragment.setExecutor(executor);
        fragment.setHandler(handler);
        return fragment;
    }

    // _View_Behaviour_
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout
        View view = inflater.inflate(R.layout.leaderboard_fragment, container, false);

        // Retrieve and set leaderboard listview
        mListView = view.findViewById(R.id.listview);
        setUpListView();

        // Back button behaviour
        FloatingActionButton backButton = view.findViewById(R.id.leaderboard_back_button);
        backButton.setOnClickListener( v -> {
            getParentFragmentManager().setFragmentResult("fromLeaderboardFragment", null);
        });

        // Return layout
        return view;
    }

    // _Private_Methods_
    private void setUpListView() {
        // Cursor query attributes
        final String[] FROM = {DatabaseHelper.PLAYER_NAME, DatabaseHelper.SCORE};
        final int[] TO = {R.id.name_textview, R.id.score_textview};

        executor.execute(() -> {
            // Perform background call to retrieve the records
            Cursor cursor = dbManager.listAllRecords();

            handler.post(() -> {
                // Update the UI with the results
                SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(),
                        R.layout.leaderboard_layout, cursor, FROM, TO, 0);
                adapter.notifyDataSetChanged();
                mListView.setAdapter(adapter);
            });
        });
    }

    private void setDBManager(DBManager dbManager) {
        this.dbManager = dbManager;
    }

    private void setExecutor(ExecutorService executor) {
        this.executor = executor;
    }

    private void setHandler(Handler handler) {
        this.handler = handler;
    }
}