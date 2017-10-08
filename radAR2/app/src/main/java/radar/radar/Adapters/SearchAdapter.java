package radar.radar.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import radar.radar.Models.Domain.User;
import radar.radar.R;
import radar.radar.UserDetailActivity;

/**
 * Created by keyst on 28/09/2017.
 */

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    ArrayList<User> users;
    Context context;

    public SearchAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // inflate custom layout
        View titleView = inflater.inflate(R.layout.row_search, parent, false);

        // return a new VH instance
        return new ViewHolder(titleView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);

        // load stuff
        holder.tvName.setText(user.firstName + " " + user.lastName);
        holder.tvUsername.setText(" @" + user.username);
        holder.description.setText("Hello, I am using Radar!");
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProfilePic;
        TextView tvName;
        TextView tvUsername;
        TextView description;

        public ViewHolder(View itemView) {
            super(itemView);
            ivProfilePic = itemView.findViewById(R.id.searchImageID);
            tvName = itemView.findViewById(R.id.row_input_name);
            tvUsername = itemView.findViewById(R.id.row_search_username);
            description = itemView.findViewById(R.id.row_description);

            // Setup on click listener on the view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // From the displayed friend list send information
                    User user = users.get(getAdapterPosition());

                    Intent intent = new Intent(context, UserDetailActivity.class);
                    intent.putExtra("user", user);
                    context.startActivity(intent);

                }
            });
        }
    }
}