package radar.radar.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import radar.radar.Adapters.FriendsAdapter;
import radar.radar.ChatActivity;
import radar.radar.GroupDetailsLifecycleListener;
import radar.radar.Models.Group;
import radar.radar.Models.User;
import radar.radar.R;
import radar.radar.Services.AuthService;

/**
 * Created by kenneth on 3/10/17.
 */

public class GroupDetailsFragment extends Fragment {
    TextView nameTextView;
    TextView mainTextView;
    RecyclerView recyclerView;
    FriendsAdapter friendsAdapter;

    GroupDetailsLifecycleListener listener;

    public void setListener(GroupDetailsLifecycleListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // The last two arguments ensure LayoutParams are inflated
        // properly.
        View rootView = inflater.inflate(
                R.layout.fragment_group_details, container, false);
        Bundle args = getArguments();

        Group group = (Group) args.getSerializable("group");

        nameTextView = rootView.findViewById(R.id.fragment_group_details_name);
        nameTextView.setText(group.name);

        mainTextView = rootView.findViewById(R.id.group_detail_textview);
        mainTextView.setText("Members");

        recyclerView = rootView.findViewById(R.id.group_details_members_recyclerView);
        friendsAdapter = new FriendsAdapter(getActivity(), new ArrayList<>());  // getContext becomes getActivity inside a fragment
        recyclerView.setAdapter(friendsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        friendsAdapter.updateFriends(group.usersDetails);

        FloatingActionButton fab = rootView.findViewById(R.id.group_details_fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            intent.putExtra("group", group);
            intent.putExtra("user", group.usersDetails.get(AuthService.getUserID(getActivity())));
            intent.putExtra("load", true);

            startActivity(intent);
        });

        // notify main activity that we have done initiating
        listener.onSetUp(this);

        return rootView;
    }

    public void setMainTextView(String text) {
        if (mainTextView != null) {
            mainTextView.setText(text);
        }
    }
}
