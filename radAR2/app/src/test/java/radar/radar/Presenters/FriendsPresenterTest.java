package radar.radar.Presenters;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import java.net.SocketTimeoutException;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Adapters.FriendsAdapter;
import radar.radar.Models.Requests.SignUpRequest;
import radar.radar.Models.Responses.AuthResponse;
import radar.radar.Models.Responses.FriendsResponse;
import radar.radar.Models.Responses.User;
import radar.radar.Services.AuthApi;
import radar.radar.Services.AuthService;
import radar.radar.Services.FriendsApi;
import radar.radar.Views.FriendsView;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;

/**
 * Created by kenneth on 20/9/17.
 */
public class FriendsPresenterTest {
    FriendsPresenter presenter;
    AuthService authService;
    AuthApi mockAuthApi;
    Context mockContext;
    SharedPreferences mockSharedPrefs;
    SharedPreferences.Editor mockEditor;

    Context friendsViewAsContext;
    FriendsView friendsView;

    FriendsAdapter friendsAdapter;

    @BeforeClass
    public static void setupClass() {
        // set all schedulers to trampoline scheduler
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                __ -> Schedulers.trampoline());
        RxJavaPlugins.setIoSchedulerHandler(
                scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(
                scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setNewThreadSchedulerHandler(
                scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(
                scheduler -> Schedulers.trampoline());

    }

    @Before
    public void setUp() throws Exception {
        // mock the Retrofit API
//        mockAuthApi = Mockito.mock(AuthApi.class);

        // mock Android's SharedPreferences
        mockSharedPrefs = Mockito.mock(SharedPreferences.class);
        mockEditor = Mockito.mock(SharedPreferences.Editor.class);
        Mockito.when(mockSharedPrefs.edit()).thenReturn(mockEditor);
        Mockito.when(mockEditor.putString(anyString(), anyString())).thenReturn(mockEditor);
        Mockito.when(mockEditor.putInt(anyString(), anyInt())).thenReturn(mockEditor);
        Mockito.when(mockSharedPrefs.getString(anyString(), anyString())).thenReturn("fakeToken");
        Mockito.when(mockSharedPrefs.getInt(anyString(), anyInt())).thenReturn(79);

        // mock friendsView as a FriendsView and Context (for SharedPrefs)
        friendsViewAsContext = Mockito.mock(Context.class, Mockito.withSettings().extraInterfaces(FriendsView.class));
        friendsView = (FriendsView) friendsViewAsContext;
        Mockito.when(friendsViewAsContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockSharedPrefs);

        // mock the RecyclerView adapter
        friendsAdapter = Mockito.mock(FriendsAdapter.class);

//        AuthResponse authResponse = new AuthResponse();
//        authResponse.success = true;
//        authResponse.errors = new ArrayList<>();
//        authResponse.token = "someString";
//        authResponse.userID = 3141592;
//
//        Mockito.when(mockAuthApi.signUp(Mockito.any(SignUpRequest.class))).
//                thenReturn(Observable.just(authResponse));
//        authService = new AuthService(mockAuthApi, mockContext);

    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void loadFriends() throws Exception {
        FriendsApi friendsApi = Mockito.mock(FriendsApi.class);

        // mock returned data, simulate success scenario
        ArrayList<User> friends = new ArrayList<>();
        friends.add(new User(1, "user1", "John", "Doe", "", "I'm a mock user!"));
        friends.add(new User(2, "krusli", "Kenneth", "Aloysius", "", "I'm a dev!"));
        FriendsResponse friendsResponse = new FriendsResponse(friends);
        friendsResponse.success = true;

        Mockito.when(friendsApi.getFriends(anyInt(), anyString()))
                .thenReturn(Observable.just(friendsResponse));
        presenter = new FriendsPresenter(friendsView, friendsApi);
        Mockito.verify(friendsView).bindAdapterToRecyclerView(any());
    }

    @Test
    public void loadFriendsError() throws Exception {
        // simulate failure scenario
        FriendsApi friendsApi = Mockito.mock(FriendsApi.class);

        // mock returned data, simulate failure scenario
        ArrayList<User> friends = new ArrayList<>();
        FriendsResponse friendsResponse = new FriendsResponse(friends);
        friendsResponse.success = true;

        Observable<FriendsResponse> throwsError = Observable.just(friendsResponse)
                .map(friendsResponse1 -> { throw new SocketTimeoutException(); });
        Mockito.when(friendsApi.getFriends(anyInt(), anyString()))
                .thenReturn(throwsError);
        presenter = new FriendsPresenter(friendsView, friendsApi);
        Mockito.verify(friendsView).showToast(anyString());
    }

}