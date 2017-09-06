package com.oxygen.radar.Services;

import com.oxygen.radar.Models.GroupDetails;
import com.oxygen.radar.Requests.RMSGetGroupInfoRequest;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by kenneth on 6/9/17.
 */

public interface GroupManagementSystemApi {
    @POST("getGroupInfo")
    Observable<GroupDetails> getGroupInfo(@Body RMSGetGroupInfoRequest body);
}
