package radar.radar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Domain.User;
import radar.radar.R;
import radar.radar.RetrofitFactory;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import radar.radar.Services.ResourcesApi;
import radar.radar.Services.ResourcesService;
import radar.radar.UserDetailActivity;
import retrofit2.Retrofit;

/**
 * Adapter for FriendsActivity that are used to display friend list of a user
 */
public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    ArrayList<User> friends;
    Context context;

    /**
     * Constructor for FriendsAdapter
     * @param context context of the activity which call this class
     * @param friends array list of users
     */
    public FriendsAdapter(Context context, ArrayList<User> friends) {
        this.context = context;
        this.friends = friends;
    }

    /**
     * Used to update the friend list of the adapter
     * @param friends new friend list for this adapter
     */
    public void updateFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate custom layout
        View titleView = inflater.inflate(R.layout.row_friends_large, parent, false);

        // return a new VH instance
        return new ViewHolder(titleView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = friends.get(position);

        // load stuff
        holder.tvName.setText(user.firstName + " " + user.lastName);
        if (user.profileDesc == null) {
            holder.tvOnlineStatus.setText("Hello, I am using Radar!");
        } else {
            holder.tvOnlineStatus.setText(user.profileDesc);
        }

        if (user.profilePicture != null) {
            if (!holder.profPicLoaded) {
                // TODO inject service using method from Activity
//            System.out.println(position);
//            System.out.println(group.name);
//            System.out.println(group.profilePicture);

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profilePic;
        TextView tvName;
        TextView tvOnlineStatus;

        boolean profPicLoaded = false;
        Context context;
        ResourcesService resourcesService;

        public ViewHolder(View itemView) {
            super(itemView);
            
            context = itemView.getContext();

            Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();
            resourcesService = new ResourcesService(context, retrofit.create(ResourcesApi.class));
            
            profilePic = itemView.findViewById(R.id.row_friends_profile_picture);
            tvName = itemView.findViewById(R.id.row_friends_name);
            tvOnlineStatus = itemView.findViewById(R.id.row_friends_online_status);

            // Setup on click listener on the view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // From the displayed friend list send information
                    User user = friends.get(getAdapterPosition());

                    Intent intent = new Intent(itemView.getContext(), UserDetailActivity.class);
                    intent.putExtra("user", user);
                    context.startActivity(intent);

                }
            });
        }
    }
}
