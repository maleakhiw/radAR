package radar.radar.Services;

import io.reactivex.Observable;
import radar.radar.Models.Requests.NewChatRequest;
import radar.radar.Models.Responses.SendMessageResponse;
import radar.radar.Models.Responses.GetChatInfoResponse;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Models.Responses.MessageBody;
import radar.radar.Models.Responses.MessagesResponse;
import radar.radar.Models.Responses.NewChatResponse;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by keyst on 20/9/17.
 */

public interface ChatApi {
    @GET("accounts/{userID}/chats")
    Observable<GetChatsResponse> getChats(@Path(value="userID", encoded=true) int userID, @Header("token") String token);

    @POST("accounts/{userID}/chats")
    Observable<NewChatResponse> newChat(@Path(value="userID", encoded=true) int userID, @Header("token") String token, @Body NewChatRequest body);

    @GET("accounts/{userID}/chats/{chatID}")
    Observable<GetChatInfoResponse> getChatInfo(@Path(value="userID", encoded=true) int userID, @Header("token") String token,  @Path(value="chatID", encoded=true) int chatID);

    @POST("accounts/{userID}/chats/{chatID}/messages")
    Observable<SendMessageResponse> sendMessages(@Path(value="userID", encoded=true) int userID, @Header("token") String token, @Path(value="chatID", encoded=true) int chatID, @Body MessageBody messageBody);

    @GET("accounts/{userID}/chats/{chatID}/messages")
    Observable<MessagesResponse> getMessages(@Path(value="userID", encoded=true) int userID, @Header("token") String token, @Path(value="chatID", encoded=true) int chatID);
}
