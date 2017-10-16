package radar.radar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import radar.radar.GroupDetailActivity;
import radar.radar.Models.Domain.Group;
import radar.radar.R;

/**
 * Created by kenneth on 3/10/17.
 */
public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {

    ArrayList<Group> groups;
    Context context;

    public GroupsAdapter(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
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
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public void setGroupsList(ArrayList<Group> groups) {
        setChatList(groups);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView chatName;
        TextView chatType;

        public ViewHolder(View itemView) {
            super(itemView);

            chatName = itemView.findViewById(R.id.row_chat_name);
            chatType = itemView.findViewById(R.id.row_last_message);

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
        }
    }
}