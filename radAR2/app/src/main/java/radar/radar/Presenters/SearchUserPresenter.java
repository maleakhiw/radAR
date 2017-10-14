package radar.radar.Presenters;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.Toast;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import radar.radar.Adapters.SearchAdapter;
import radar.radar.Models.Responses.UsersSearchResult;
import radar.radar.Services.UsersService;
import radar.radar.Views.SearchUserView;

/**
 * Presenter for SearchUserFragment that are used to store application logic related to search user
 */
public class SearchUserPresenter {
    private SearchUserView searchUserView;
    private UsersService usersService;

    /**
     * Constructor for this presenter class
     * @param searchUserView Instance of SearchUserView interface class
     * @param usersService service that has been instantiated
     */
    public SearchUserPresenter(SearchUserView searchUserView, UsersService usersService) {
        this.searchUserView = searchUserView;
        this.usersService = usersService;
    }

    /**
     * Perform user search based on name
     * @param query keyword for name that will be used to search
     */
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
                    searchUserView.bindAdapterToRecyclerView(usersSearchResult);
                } else {
                    searchUserView.showToast("No user with this username is found");
                }
            }

            @Override
            public void onError(Throwable e) {
                searchUserView.showToast("Internal Error. Failed to search user.");
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
