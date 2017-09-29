package radar.radar.Services;


import retrofit2.http.POST;

public interface LocationApi {
    @POST("accounts/{userID}/location")
    Observable<UpdateLocationResponse> updateLocation(@Path(value = ))


}
