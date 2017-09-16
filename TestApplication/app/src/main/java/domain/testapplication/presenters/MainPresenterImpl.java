package domain.testapplication.presenters;

import android.content.Context;
import android.view.View;
import android.widget.ArrayAdapter;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;

import domain.testapplication.models.Friend;
import domain.testapplication.services.FriendsService;
import domain.testapplication.models.GetFriendsResponse;
import domain.testapplication.views.MainView;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by keyst on 8/09/2017.
 */

public class MainPresenterImpl implements MainPresenter {
    MainView mainView;
    FriendsService friendsService;

    public MainPresenterImpl(MainView mainView) {
        this.mainView = mainView;
        Retrofit friendsServiceRetrofit = new Retrofit.Builder()
                                                      .baseUrl("http://35.185.35.117/UMS/")
                                                      .addConverterFactory(GsonConverterFactory.create())
                                                      .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                                      .build();
        this.friendsService = new FriendsService(friendsServiceRetrofit);

        View.OnClickListener onClickListenerForListView = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("YO I AM CLICKED");
            }
        };

        mainView.setListViewOnClickListener(onClickListenerForListView);
    }

    @Override
    public void loadData() {
        this.friendsService.getFriends(1, "79")
                .subscribe(new Consumer<GetFriendsResponse>() {
                    @Override
                    public void accept(GetFriendsResponse getFriendsResponse) throws Exception {
                        System.out.println(getFriendsResponse.success);
                        mainView.changeLabelText("Hey we have data");

                        ArrayList<String> listOfFriends = new ArrayList<>();
                        for (int i=0; i<getFriendsResponse.friends.size(); i++) {
                            Friend friend = getFriendsResponse.friends.get(i);
                            listOfFriends.add(friend.getFirstName() + " " + friend.getLastName());
                        }

                        // create a new ArrayAdapter (to load data to a list)
                        // the ArrayAdapter loads the List of Strings to the List
                        ArrayAdapter<String> itemsArrayAdapter = new ArrayAdapter<>((Context) mainView, android.R.layout.simple_list_item_1, listOfFriends);
                        mainView.setListViewArrayAdapter(itemsArrayAdapter);

                    }
                });
    }
}
