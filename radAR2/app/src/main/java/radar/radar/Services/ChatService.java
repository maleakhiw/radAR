package radar.radar.Services;

import android.content.Context;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Requests.NewChatRequest;
import radar.radar.Models.Responses.SendMessageResponse;
import radar.radar.Models.Responses.GetChatInfoResponse;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Models.Responses.MessageBody;
import radar.radar.Models.Responses.MessagesResponse;
import radar.radar.Models.Responses.NewChatResponse;


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
        return chatApi.getChats(userID, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<NewChatResponse> newChat(NewChatRequest newChatRequest) {
        // TODO: validation
        return chatApi.newChat(userID, token, newChatRequest)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<GetChatInfoResponse> getChatInfo(int chatID) {
        return chatApi.getChatInfo(userID, token,  chatID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable<MessagesResponse> getMessages(int chatID) {
        return chatApi.getMessages(userID, token, chatID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    // send message
    public Observable<SendMessageResponse> sendMessages(int chatID, MessageBody messageBody) {
        System.out.println("sendMessages");

        Observable<SendMessageResponse> sendMessageResponseObservable = chatApi.sendMessages(userID, token, chatID, messageBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        return sendMessageResponseObservable.map(message -> {
            System.out.println(message.sentMessage);
            return message;
        });
    }


}
