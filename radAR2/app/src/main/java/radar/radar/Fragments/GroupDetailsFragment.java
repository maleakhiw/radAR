package radar.radar.Fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import radar.radar.GroupDetailsLifecycleListener;
import radar.radar.R;

/**
 * Created by kenneth on 3/10/17.
 */

public class GroupDetailsFragment extends Fragment {
    TextView mainTextView;
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

        mainTextView = rootView.findViewById(R.id.group_detail_textview);

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
