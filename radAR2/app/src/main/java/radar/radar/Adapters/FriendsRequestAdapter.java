package radar.radar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Models.Responses.FriendRequest;
import radar.radar.Models.Responses.Status;
import radar.radar.R;
import radar.radar.SearchResultActivity;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by keyst on 28/09/2017.
 */

public class FriendsRequestAdapter extends RecyclerView.Adapter<FriendsRequestAdapter.ViewHolder> {
    ArrayList<FriendRequest> friendRequests;
    Context context;

    // Useful for retrofit
    private UsersService usersService;
    private RecyclerView recyclerView;

    public FriendsRequestAdapter(Context context, ArrayList<FriendRequest> friendRequests) {
        this.context = context;
        this.friendRequests = friendRequests;

        // Create retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://35.185.35.117/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsersApi usersApi = retrofit.create(UsersApi.class);
        usersService = new UsersService(usersApi, context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate custom layout
        View titleView = inflater.inflate(R.layout.row_friend_request, parent, false);

        // return a new VH instance
        return new ViewHolder(titleView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FriendRequest friendRequest = friendRequests.get(position);

        // load stuff
        holder.tvName.setText(friendRequest.firstName + " " + friendRequest.lastName);
    }

    @Override
    public int getItemCount() {
        return friendRequests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        TextView tvName;
        Button accept;
        Button decline;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.searchImageID);
            tvName = itemView.findViewById(R.id.row_input_name);
            accept = itemView.findViewById(R.id.accept);
            decline = itemView.findViewById(R.id.decline);

            // If accept clicked then request is accepted
            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Accept friend request
                    FriendRequest friendRequest = friendRequests.get(getAdapterPosition());
                    processFriendRequest(friendRequest.requestID, UsersService.REQUEST_ACTION.ACCEPT);
                }
            });

            decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Decline friend request
                    FriendRequest friendRequest = friendRequests.get(getAdapterPosition());
                    processFriendRequest(friendRequest.requestID, UsersService.REQUEST_ACTION.DECLINE);
                }
            });
        }
    }

    public void processFriendRequest(int requestID, UsersService.REQUEST_ACTION request_action) {
        usersService.respondToFriendRequest(requestID, request_action).subscribe(new Observer<Status>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Status status) {
                // If success
                if (status.success) {
                    Toast.makeText(context, "Successfully process friend request", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(context, SearchResultActivity.class);
                    context.startActivity(intent);
                }
                else {
                    Toast.makeText(context, "Failure process friend request", Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context, "Failure confirm friend request", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {

            }
        });

    }

}