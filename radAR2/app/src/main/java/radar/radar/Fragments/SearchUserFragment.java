package radar.radar.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.SearchAdapter;
import radar.radar.Models.Responses.UsersSearchResult;
import radar.radar.Presenters.SearchUserPresenter;
import radar.radar.R;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import radar.radar.Views.SearchUserView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Class representing fragments that have design and functionality to search another users on the
 * application.
 */
public class SearchUserFragment extends Fragment implements SearchUserView {
    /** User interface variable */
    private RecyclerView recyclerView;
    private EditText query;

    /** Presenter and service */
    private UsersService usersService;
    private SearchUserPresenter searchUserPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_user_fragment, container, false);

        // Setup UI
        setupUI(view);

        // Create retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://radar.fadhilanshar.com/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsersApi usersApi = retrofit.create(UsersApi.class);
        usersService = new UsersService(getActivity(), usersApi);

        // Initiate the presenter
        searchUserPresenter = new SearchUserPresenter(this, usersService);

        // When edit text is entered do search
        query.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // if entered do search
                    searchUserPresenter.doSearch(query.getText().toString());
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    /**
     * Connecting UI element with the java class
     */
    public void setupUI(View view) {
        recyclerView = view.findViewById(R.id.searchRecyclerView);
        query = view.findViewById(R.id.search_bar);
    }

    /**
     * Displaying message in the form of toast to user
     * @param message message to be sent to user screen in toast
     */
    @Override
    public void showToast(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Displaying search result and bind adapter to the recycler view
     * @param usersSearchResult representing the response when searching user
     */
    @Override
    public void bindAdapterToRecyclerView(UsersSearchResult usersSearchResult) {
        SearchAdapter searchAdapter = new SearchAdapter(getActivity(), usersSearchResult.results);
        recyclerView.setAdapter(searchAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        searchAdapter.notifyDataSetChanged();
    }

}