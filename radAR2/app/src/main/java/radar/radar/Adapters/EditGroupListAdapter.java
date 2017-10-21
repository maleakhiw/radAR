package radar.radar.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import radar.radar.Models.Android.UserWithCheckbox;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Domain.User;
import radar.radar.R;


public class EditGroupListAdapter extends RecyclerView.Adapter<EditGroupListAdapter.ViewHolder> {

    ArrayList<UserWithCheckbox> users;
    Group group;

    public EditGroupListAdapter(ArrayList<UserWithCheckbox> users, Group group) {
        this.users = users;
        this.group = group;
    }

    public void setUsers(ArrayList<UserWithCheckbox> users) {
        this.users = new ArrayList<>();

        // filter users already in the group
        for (UserWithCheckbox user: users) {
            if (!group.members.contains(user.userID)) {
                this.users.add(user);
            }
        }
        
        notifyDataSetChanged(); // to simplify things, put notifyDataSetChanged() in setters
                                // to avoid repetitive calls from the Activity
    }

    public ArrayList<UserWithCheckbox> getUsers() {
        return users;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.row_user_checkbox, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        User user = users.get(position);

        holder.nameTextView.setText(user.firstName + " " + user.lastName);

        holder.checkBox.setOnClickListener(view -> {
//            Integer pos = (Integer) holder.checkBox.getTag();
            UserWithCheckbox userEntry = users.get(position);
            if (userEntry.isChecked) {
                userEntry.isChecked = false;
            } else {
                userEntry.isChecked = true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView imageView;
        TextView nameTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.user_checkbox);
            imageView = itemView.findViewById(R.id.user_row_imageView);
            nameTextView = itemView.findViewById(R.id.user_textView);

        }
    }
}
