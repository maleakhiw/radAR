package radar.radar.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import radar.radar.R;

/**
 * Created by keyst on 8/10/2017.
 */

public class PendingRequestsFragment extends Fragment {
    private static final String TAG = "SearchUserFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pending_requests_fragment, container, false);
        return view;
    }
}