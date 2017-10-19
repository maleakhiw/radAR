package radar.radar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.GroupDetailActivity;
import radar.radar.Models.Domain.Group;
import radar.radar.Presenters.GroupsListPresenter;
import radar.radar.R;
import radar.radar.RetrofitFactory;
import radar.radar.Services.ResourcesApi;
import radar.radar.Services.ResourcesService;
import retrofit2.Retrofit;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    ArrayList<Group> groups;
    Context context;
    GroupsListPresenter presenter;

    public GroupsAdapter(Context context, ArrayList<Group> groups, GroupsListPresenter presenter) {
        this.context = context;
        this.groups = groups;
        this.presenter = presenter;
    }

    public void setChatList(ArrayList<Group> groups) {
        this.groups = groups;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate custom layout
        View titleView = inflater.inflate(R.layout.row_chat, parent, false);

        // return a new VH instance
        return new ViewHolder(titleView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Group group = groups.get(position);

        // load stuff
        holder.chatName.setText(group.name);

        // Check type of group
        if (group.isTrackingGroup) {
            holder.chatType.setText("Tracking Group");
        } else {
            holder.chatType.setText("Group");
        }

        // load profile picture
        if (group.profilePicture != null) {

            holder.resourcesService.getResourceWithCache(group.profilePicture, holder.context).subscribe(new Observer<File>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(File file) {
                    System.out.println("Update profile picture for: " + group.name);
                    Picasso.with(holder.context).load(file).into(holder.img);
                    holder.profPicLoaded = true;
                }

                @Override
                public void onError(Throwable e) {

                }

                @Override
                public void onComplete() {

                }
            });
        } else {
            holder.img.setImageResource(R.mipmap.ic_launcher_round);
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void setGroupsList(ArrayList<Group> groups) {
        setChatList(groups);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements MenuItem.OnMenuItemClickListener, View.OnCreateContextMenuListener {
        TextView chatName;
        TextView chatType;
        ImageView img;
        boolean profPicLoaded;
        ResourcesService resourcesService;

        Context context;

        public ViewHolder(View itemView) {
            super(itemView);

            chatName = itemView.findViewById(R.id.row_chat_name);
            chatType = itemView.findViewById(R.id.row_last_message);
            img = itemView.findViewById(R.id.row_profile_picture);

            context = itemView.getContext();

            Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();
            resourcesService = new ResourcesService(context, retrofit.create(ResourcesApi.class));   // TODO move to factory, along with other instances of new UsersService


            // Setup on click listener on the view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // From the displayed friend list send information
                    Group group = groups.get(getAdapterPosition());
                    Intent intent = new Intent(context, GroupDetailActivity.class);
                    intent.putExtra("group", group);
//                    intent.putExtra("group", group);
//                    intent.putExtra("load", true);
                    context.startActivity(intent);
                }
            });

            itemView.setOnCreateContextMenuListener(this);  // handle long click for context menu
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case 1:
                    System.out.println(groups.get(getAdapterPosition()));
                    Group group = groups.get(getAdapterPosition());
                    if (group != null) {
                        presenter.deleteGroup(group.groupID);
                    } else {
                        Log.w("onMenuItemClick", "Group missing");
                    }
//                    presenter.deleteGroup(groups.get(getAdapterPosition()).groupID);
                    return true;
            }
            return false;
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//            menu.setHeaderTitle("Select Action");
            MenuItem delete = menu.add(Menu.NONE,1,1,"Delete group");   // TODO move to strings
            delete.setOnMenuItemClickListener(this);
        }
    }
}