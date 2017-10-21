package radar.radar.Services;


import android.content.Context;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Domain.MeetingPoint;
import radar.radar.Models.Requests.AddMembersBody;
import radar.radar.Models.Requests.NewChatRequest;
import radar.radar.Models.Requests.NewGroupBody;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Models.Responses.GroupsResponse;
import radar.radar.Models.Responses.NewChatResponse;
import radar.radar.Models.Responses.Status;
import radar.radar.Models.UpdateGroupBody;

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

    public Observable<GroupsResponse> newGroup(String name, ArrayList<Integer> participantUserIDs,
                                               MeetingPoint meetingPoint) {
        NewGroupBody newGroupBody = new NewGroupBody(name, participantUserIDs, meetingPoint);
        return groupsApi.newGroup(userID, newGroupBody, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<NewChatResponse> newChat(String name, ArrayList<Integer> participantUserIDs) {
        System.out.println("newChat");
        NewChatRequest newChatRequest = new NewChatRequest(name, participantUserIDs);
        return groupsApi.newChat(userID, newChatRequest, token)
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

    public Observable<Status> updateGroup(int groupID, UpdateGroupBody body) {
        return groupsApi.updateGroup(userID, groupID, token, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Status> deleteGroup(int groupID) {
        return groupsApi.leaveGroup(userID, token, groupID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<Status> removeMember(int groupID, int memberUserID) {
        return groupsApi.removeMember(userID, memberUserID, token, groupID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Adds users to the group.
     * @param groupID group in question
     * @param invitedUsers users to invite to the group
     * @return Observable<Status>
     */
    public Observable<Status> addMembers(int groupID, ArrayList<Integer> invitedUsers) {
        System.out.println(userID);
        System.out.println(groupID);
        return groupsApi.addMembers(userID, groupID, token, new AddMembersBody(invitedUsers))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
