package com.example.craigblackburn.foostats;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class PlayerRecyclerViewAdapter extends RecyclerView.Adapter<PlayerRecyclerViewAdapter.ViewHolder> {

    private int teamSelector;
    private final List<FPlayer> mPlayers;
    private final PlayerListFragment.OnListFragmentInteractionListener mListener;

    public PlayerRecyclerViewAdapter(List<FPlayer> list, int teamSelector, PlayerListFragment.OnListFragmentInteractionListener listener) {
        this.mPlayers = list;
        this.mListener = listener;
        this.teamSelector = teamSelector;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_player, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mPlayer = mPlayers.get(position);
        holder.mCheckboxView.setChecked(false);
        holder.mNameView.setText(mPlayers.get(position).getName());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mPlayer);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mPlayers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final CheckBox mCheckboxView;
        public final TextView mNameView;
        public FPlayer mPlayer;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCheckboxView = (CheckBox) view.findViewById(R.id.checkbox);
            mNameView = (TextView) view.findViewById(R.id.content);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mNameView.getText() + "'";
        }
    }
}
