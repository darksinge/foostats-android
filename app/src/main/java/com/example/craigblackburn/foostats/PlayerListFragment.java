package com.example.craigblackburn.foostats;

import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class PlayerListFragment extends Fragment {

    public enum TeamSelector {
        BLUE_TEAM, RED_TEAM
    }

    private static String COLUMN_COUNT = "column_count";

    private boolean teamIsRandom;
    private TeamSelector currentTeamSelector;

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<FPlayer> playerList;

    public PlayerListFragment() {}

    public static PlayerListFragment newInstance(int columnCount, boolean isRandom, TeamSelector selector) {
        PlayerListFragment fragment = new PlayerListFragment();
        Bundle args = new Bundle();
        args.putInt(COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        fragment.initialize(isRandom, selector);
        return fragment;
    }

    public void initialize(boolean isRandom, TeamSelector selector) {
        try {
            this.playerList = FPlayer.find();
        } catch (Exception e) {
            e.printStackTrace();
            this.playerList = new ArrayList<>();
        }

        this.teamIsRandom = isRandom;
        this.setCurrentTeamSelector(selector);
        mListener = new OnListFragmentInteractionListener() {
            @Override
            public void onListFragmentInteraction(FPlayer player) {

            }
        };
    }

    public int getCurrentTeamSelector() {
        if (teamIsRandom) {
            return -1;
        } else if (currentTeamSelector == TeamSelector.BLUE_TEAM) {
            return 0;
        } else {
            return 1;
        }
    }

    public void setCurrentTeamSelector(TeamSelector selector) {
        currentTeamSelector = selector;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_player_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new PlayerRecyclerViewAdapter(playerList, getCurrentTeamSelector(), mListener));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(FPlayer player);
    }
}
