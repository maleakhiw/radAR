package radar.radar.Services;

import android.content.Context;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Requests.NewChatRequest;
import radar.radar.Models.Responses.SendMessageResponse;
import radar.radar.Models.Responses.GetChatInfoResponse;
import radar.radar.Models.Responses.GetChatsResponse;
import radar.radar.Models.Responses.MessageBody;
import radar.radar.Models.Responses.MessagesResponse;
import radar.radar.Models.Responses.NewChatResponse;

/**
 * Layer of abstraction which methods will call ChatApi and used for chat functionality
 */
public class ChatService {
    Context context;
    ChatApi chatApi;
    int userID;
    String token;

    /**
     * Constructor
     * @param context context of the activity that needs the service
     * @param chatApi instances of chatApi
     */
    public ChatService(Context context, ChatApi chatApi) {
        this.context = context;
        this.chatApi = chatApi;
        userID = AuthService.getUserID(context);
        token = AuthService.getToken(context);
    }

    /**
     * Getting chat ids by polling every time units
     * @param pollingPeriod time ellapse before automatically call
     * @return Observable<GetChatsResponse>
     */
    public Observable<GetChatsResponse> getChats(int pollingPeriod) {
        return Observable.create(emitter -> {
            Observable.interval(pollingPeriod, TimeUnit.MILLISECONDS)
                    .subscribe(tick -> {
                        chatApi.getChats(userID, token)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<GetChatsResponse>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onNext(GetChatsResponse getChatsResponse) {
                                emitter.onNext(getChatsResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                emitter.onError(e);
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                    });
        });
    }

    /**
     * Default method for getting chat
     * @return Observable<GetChatsResponse>
     */
    public Observable<GetChatsResponse> getChats() {
        return chatApi.getChats(userID, token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Generate new chat
     * @param newChatRequest chat request data model
     * @return Observable<NewChatResponse>
     */
    public Observable<NewChatResponse> newChat(NewChatRequest newChatRequest) {
        return chatApi.newChat(userID, token, newChatRequest)
                      .subscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Getting information of a particular chat id
     * @param chatID chat id of a particular chat which we will extract the information
     * @return Observable<GetChatInfoResponse>
     */
    public Observable<GetChatInfoResponse> getChatInfo(int chatID) {
        return chatApi.getChatInfo(userID, token,  chatID)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Polls the server for new messages
     * @param chatID chat to get messages for
     * @param pollingPeriod time in between requests in milliseocnds
     * @return Observable<MessagesResponse>
     */
    public Observable<MessagesResponse> getMessages(int chatID, int pollingPeriod) {
        return Observable.create(emitter -> {
            Observable.interval(pollingPeriod, TimeUnit.MILLISECONDS)
            .subscribe(tick -> {
                chatApi
                .getMessages(userID, token, chatID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MessagesResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(MessagesResponse messagesResponse) {
                        emitter.onNext(messagesResponse);
                    }

                    @Override
                    public void onError(Throwable e) {
                        emitter.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
            });
        });
    }

    /**
     * Sending message to particular chat id
     * @param chatID chat which we will send message to
     * @param messageBody body of the message
     * @return Observable<SendMessageResponse>
     */
    public Observable<SendMessageResponse> sendMessages(int chatID, MessageBody messageBody) {
        Observable<SendMessageResponse> sendMessageResponseObservable = chatApi.sendMessages(userID, token, chatID, messageBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        return sendMessageResponseObservable.map(message -> {
            System.out.println(message.sentMessage);
            return message;
        });
    }


}
