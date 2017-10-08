package radar.radar.Services;


import android.content.Context;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.MeetingPoint;
import radar.radar.Models.Requests.NewGroupBody;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Models.Responses.Status;

public class GroupsService {
    GroupsApi groupsApi;
    int userID;
    String token;

    public GroupsService(Context context, GroupsApi groupsApi) {
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

    public Observable<Status> updateMeetingPoint(int groupID, MeetingPoint meetingPoint) {
        return groupsApi.updateMeetingPoint(userID, groupID, token, meetingPoint)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}