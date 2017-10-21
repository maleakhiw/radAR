package radar.radar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.User;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Models.Responses.Status;
import radar.radar.R;
import radar.radar.RetrofitFactory;
import radar.radar.Services.AuthService;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import radar.radar.Services.ResourcesApi;
import radar.radar.Services.ResourcesService;
import radar.radar.UserDetailActivity;
import retrofit2.Retrofit;

/**
 * Adapter classes for edit group, changing group name, profile picture, etc
 */
public class EditGroupAdapter extends RecyclerView.Adapter<EditGroupAdapter.ViewHolder> {
    HashMap<Integer, User> friends;
    ArrayList<User> friendsList;
    Context context;
    Group group;

    /**
     * Constructor for EditGroup
     * @param context context from when the adapter called (activity)
     * @param friends hash map mapping friends
     * @param group instance of group
     */
    public EditGroupAdapter(Context context, HashMap<Integer, User> friends, Group group) {
        this.context = context;
        this.friends = friends;
        this.group = group;

        friendsList = new ArrayList<>();
        for (Object entry: friends.values()) {
            User user = (User) entry;
            friendsList.add(user);
        }
    }

    /**
     * Update the data/ hashmap
     * @param friends new hashmap to be updated
     */
    public void updateFriends(HashMap<Integer, User> friends) {
        this.friends = friends;

        friendsList = new ArrayList<>();
        for (Object entry: friends.values()) {
            User user = (User) entry;
            friendsList.add(user);
        }
    }

    @Override
    public EditGroupAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate custom layout
        View titleView = inflater.inflate(R.layout.row_friends_with_delete, parent, false);

        // return a new VH instance
        return new EditGroupAdapter.ViewHolder(titleView);
    }

    @Override
    public void onBindViewHolder(EditGroupAdapter.ViewHolder holder, int position) {
        User user = friendsList.get(position);

        if (!group.admins.contains(user.userID)) {
            holder.tvDelete.setVisibility(View.VISIBLE);
        }

        // load stuff
        holder.tvName.setText(user.firstName + " " + user.lastName);
        if (user.profileDesc == null) {
            holder.tvOnlineStatus.setText("Hello, I am using Radar!");
        } else {
            holder.tvOnlineStatus.setText(user.profileDesc);
        }

        if (user.userID == AuthService.getUserID(context)) {
            holder.tvDelete.setVisibility(View.GONE);
        }

        if (user.profilePicture != null) {
            if (!holder.profPicLoaded) {

                holder.resourcesService.getResourceWithCache(user.profilePicture, holder.context).subscribe(new Observer<File>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(File file) {
                        System.out.println("Update profile picture for: " + user.userID);
                        Picasso.with(holder.context).load(file).into(holder.profilePic);
                        holder.profPicLoaded = true;
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }
        } else {
            holder.profilePic.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    @Override
    public int getItemCount() {
        if (friends == null) {
            return 0;
        }
        return friends.size();
    }

    /**
     * Inner class for the view holder in EditGroupAdapter
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView tvName;
        TextView tvDelete;
        TextView tvOnlineStatus;

        boolean profPicLoaded = false;
        Context context;
        ResourcesService resourcesService;

        /**
         * Constructor for view holder
         * @param itemView view instances
         */
        public ViewHolder(View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.row_friends_profile_picture);
            tvName = itemView.findViewById(R.id.row_friends_name);
            tvOnlineStatus = itemView.findViewById(R.id.row_friends_online_status);

            context = itemView.getContext();

            Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();
            resourcesService = new ResourcesService(context, retrofit.create(ResourcesApi.class));

            tvDelete = itemView.findViewById(R.id.delete_TV);

            GroupsApi groupsApi = retrofit.create(GroupsApi.class);
            GroupsService groupsService = new GroupsService(context, groupsApi);

            tvDelete.setOnClickListener(view -> {
                User user = friendsList.get(getAdapterPosition());
                System.out.println(user.userID);
                groupsService.removeMember(group.groupID, user.userID).subscribe(new Observer<Status>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Status status) {
                        System.out.println(status);
                        if (status.success) {
                            groupsService.getGroup(group.groupID).subscribe(new Observer<GroupsResponse>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                }

                                @Override
                                public void onNext(GroupsResponse groupsResponse) {
                                    if (groupsResponse.success) {
                                        System.out.println("removed");
                                        group = groupsResponse.group;
                                        updateFriends(groupsResponse.group.usersDetails);
                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Member removed", Toast.LENGTH_SHORT).show();
                                    } else if (groupsResponse.errors.get(0) != null) {
                                        if (groupsResponse.errors.get(0).errorCode == 109) {
                                            Toast.makeText(context, "Cannot remove the group admin", Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onError(Throwable e) {
                                    e.printStackTrace();
                                    Toast.makeText(context, "Unexpected error", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println(e);
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });


            });


        }
    }
}
