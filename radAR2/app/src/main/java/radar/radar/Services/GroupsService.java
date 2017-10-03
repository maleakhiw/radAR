package radar.radar.Services;


import android.content.Context;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Requests.NewGroupBody;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Models.Responses.GroupsResponse;

public class GroupsService {
    Context context;
    GroupsApi groupsApi;
    int userID;
    String token;

    public GroupsService(Context context, GroupsApi groupsApi) {
        this.context = context;
        this.groupsApi = groupsApi;
        userID = AuthService.getUserID(context);
        token = AuthService.getToken(context);
    }

    public Observable<GroupsResponse> newGroup(String name, ArrayList<Integer> participantUserIDs) {
        System.out.println("newGroup");
        NewGroupBody newGroupBody = new NewGroupBody(name, participantUserIDs);
        return groupsApi.newGroup(userID, newGroupBody, token)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<GroupsResponse> getGroup(int groupID) {
        return groupsApi.getGroup(userID, groupID, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<GetChatsResponse> getGroups() {
        System.out.println(userID);

        return groupsApi.getGroupIDs(userID, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
