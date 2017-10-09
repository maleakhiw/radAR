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
import radar.radar.R;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by keyst on 8/10/2017.
 */

public class SearchUserFragment extends Fragment {
    private RecyclerView recyclerView;
    private EditText query;
    private UsersService usersService;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_user_fragment, container, false);

        recyclerView = view.findViewById(R.id.searchRecyclerView);
        query = view.findViewById(R.id.search_bar);

        // Create retrofit instance
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://radar.fadhilanshar.com/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        UsersApi usersApi = retrofit.create(UsersApi.class);
        usersService = new UsersService(getActivity(), usersApi);

        // When edit text is entered do search
        query.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    // if entered do search
                    doSearch(query.getText().toString());
                    return true;
                }
                return false;
            }
        });


        return view;
    }

    /** This search method will search the appropriate user using the user's query */
    public void doSearch(String query) {
        // By default search type is name
        usersService.searchForUsers(query, "name").subscribe(new Observer<UsersSearchResult>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UsersSearchResult usersSearchResult) {
                // After we get the search result, it will be array list of Users
                // We need to somehow display this user result to a recycler view
                if (usersSearchResult.results.size() != 0) {
                    SearchAdapter searchAdapter = new SearchAdapter(getActivity(), usersSearchResult.results);
                    recyclerView.setAdapter(searchAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    searchAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "No user with this name found.", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(getActivity(), "Error occurred.", Toast.LENGTH_LONG).show();

            }

            @Override
            public void onComplete() {

            }
        });
    }
}