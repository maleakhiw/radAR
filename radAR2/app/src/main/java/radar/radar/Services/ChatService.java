package radar.radar.Services;

import android.content.Context;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Requests.NewChatRequest;
import radar.radar.Models.Responses.GetChatInfoResponse;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Models.Responses.MessagesResponse;
import radar.radar.Models.Responses.NewChatResponse;

/**
 * Created by kenneth on 20/9/17.
 */

public class ChatService {
    Context context;
    ChatApi chatApi;
    int userID;
    String token;

    public ChatService(Context context, ChatApi chatApi) {
        this.context = context;
        this.chatApi = chatApi;
        userID = AuthService.getUserID(context);
        token = AuthService.getToken(context);
    }

    public Observable<GetChatsResponse> getChats() {
        return chatApi.getChats(userID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<NewChatResponse> newChat(NewChatRequest newChatRequest) {
        // TODO: validation
        return chatApi.newChat(userID, newChatRequest)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<GetChatInfoResponse> getChatInfo(int chatID) {
        return chatApi.getChatInfo(userID, chatID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MessagesResponse> getMessages(int chatID) {
        return chatApi.getMessages(userID, chatID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
