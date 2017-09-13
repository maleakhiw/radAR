package Adapter;

import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.gohool.recyclerview.recyclerview.DetailsActivity;
import com.gohool.recyclerview.recyclerview.R;

import java.util.List;

import Model.ListItem;

/**
 * Created by keyst on 9/09/2017.
 */

/** Adapter connects template listview, data model, card view individual elements layout */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private Context context;
    private List<ListItem> listItems; // An array like data structure consisting of list item

    public MyAdapter(Context context, List listItems) {
        this.context = context;
        this.listItems = listItems;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // make sure that the view from here can be used as an object that we can manipulate
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {
        ListItem item = listItems.get(position);
        holder.name.setText(item.getName());
        holder.description.setText(item.getDescription());
    }

    // Get the size of list items
    @Override
    public int getItemCount() {
        return listItems.size();
    }

    // Fetch the text view name and description and set of the view so that it can be viewed
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView name;
        public TextView description;

        public ViewHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            name = (TextView) itemView.findViewById(R.id.title);
            description = (TextView) itemView.findViewById(R.id.description);

        }

        @Override
        public void onClick(View v) {
            // Get the position of the row clicked or taped
            int position = getAdapterPosition();

            ListItem item = listItems.get(position);
            //Toast.makeText(context, item.getName(), Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, DetailsActivity.class); //the context will be main activity
            intent.putExtra("name", item.getName());
            intent.putExtra("description", item.getDescription());

            context.startActivity(intent);

        }
    }
}
