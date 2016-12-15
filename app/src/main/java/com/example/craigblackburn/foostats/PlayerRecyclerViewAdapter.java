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

    private final List<FPlayer> mPlayers;
    private final OnListFragmentInteractionListener mListener;
    private PlayerListFragment.TeamSelector selector;

    public PlayerRecyclerViewAdapter(List<FPlayer> list, OnListFragmentInteractionListener listener, PlayerListFragment.TeamSelector selector) {
        this.mPlayers = list;
        this.mListener = listener;
        this.selector = selector;
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

        holder.mCheckboxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mPlayer, selector, holder.mCheckboxView.isChecked());
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    if (holder.mPlayer == null) {
                        try {
                            holder.mPlayer = FPlayer.find().get(0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    mListener.onListFragmentInteraction(holder.mPlayer, selector, holder.mCheckboxView.isChecked());
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

    interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(FPlayer player, PlayerListFragment.TeamSelector selector, boolean didSelect);
    }
}
