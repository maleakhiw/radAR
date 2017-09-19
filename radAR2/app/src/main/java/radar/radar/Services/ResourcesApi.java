package radar.radar.Services;

import radar.radar.Models.Responses.Status;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

/**
 * Created by kenneth on 18/9/17.
 */

public interface ResourcesApi {
    // https://stackoverflow.com/a/38891018
    /*
    // Upload file like this
    File file = // initialize file here

    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), RequestBody.create(MediaType.parse("image/*"), file));

    Call<MyResponse> call = api.uploadAttachment(filePart);
     */
    @Multipart
    @POST("accounts/{userID}/resources")
    Observable<Status> uploadResource(@Path(value = "userID", encoded = true) int userID, @Header("token") String token, @Part MultipartBody.Part filePart);

    // 404 not found for invalid resource will call Subscribers' onError() method
    // https://stackoverflow.com/questions/31126793/download-and-write-a-file-with-retrofit-and-rxjava
    // http://www.codexpedia.com/android/retrofit-2-and-rxjava-for-file-downloading-in-android/
    // https://futurestud.io/tutorials/retrofit-2-how-to-download-files-from-server
    @Streaming
    @GET("accounts/{userID}/resources/{resourceID}")
    Observable<Response<ResponseBody>> getResource(@Path(value = "userID", encoded = true) int userID, @Path(value = "resourceID", encoded = true) String resourceID, @Header("token") String token);
}
