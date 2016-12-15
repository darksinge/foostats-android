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


public class PlayerListFragment extends android.support.v4.app.Fragment implements PlayerRecyclerViewAdapter.OnListFragmentInteractionListener {

    public enum TeamSelector {
        BLUE_TEAM, RED_TEAM, RANDOM_TEAMS
    }

    private static String COLUMN_COUNT = "column_count";

    private View view;
    private boolean teamIsRandom;
    private TeamSelector currentTeamSelector;

    private int mColumnCount = 1;
    private OnFragmentInteractionListener mListener;
    private List<FPlayer> playerList;

    public PlayerListFragment() {}

    public static PlayerListFragment newInstance(OnFragmentInteractionListener listener, List<FPlayer> list, TeamSelector selector) {
        PlayerListFragment fragment = new PlayerListFragment();
        Bundle args = new Bundle();
        args.putInt(COLUMN_COUNT, list.size());
        fragment.setArguments(args);
        fragment.initialize(listener, selector, list);
        return fragment;
    }

    public void initialize(OnFragmentInteractionListener listener, TeamSelector selector, List<FPlayer> list) {
        this.mListener = listener;
        this.playerList = list;
        this.teamIsRandom = selector == TeamSelector.RANDOM_TEAMS;
        this.currentTeamSelector = selector;
    }

    public void setListFragmentCheckBoxEnabled(boolean isEnabled) {
        if (view instanceof RecyclerView)
            view.setEnabled(isEnabled);
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
        view = inflater.inflate(R.layout.fragment_player_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            recyclerView.setAdapter(new PlayerRecyclerViewAdapter(playerList, this, currentTeamSelector));
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    @Override
    public void onListFragmentInteraction(FPlayer player, TeamSelector selector, boolean didSelect) {
        if (mListener != null)
            mListener.onFragmentInteraction(player, selector, didSelect);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(FPlayer player, TeamSelector selector, boolean didSelect);
    }
}
