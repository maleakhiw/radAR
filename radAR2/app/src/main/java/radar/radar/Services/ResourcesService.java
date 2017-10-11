package radar.radar.Services;

import android.content.Context;

import java.io.File;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import radar.radar.Models.Responses.Status;
import radar.radar.Models.Responses.UploadFileResponse;
import retrofit2.Response;
import retrofit2.http.Multipart;

/**
 * Created by maleakhiw on 10/10/2017
 */

public class ResourcesService {
    Context context;
    ResourcesApi resourcesApi;
    int userID;
    String token;

    public ResourcesService(ResourcesApi resourcesApi, Context context) {
        this.context = context;
        this.resourcesApi = resourcesApi;
        userID = AuthService.getUserID(context);
        token = AuthService.getToken(context);
    }

    // http://www.codexpedia.com/android/retrofit-2-and-rxjava-for-file-downloading-in-android/
    // adapted for RxJava2

    private Observable<File> saveToDiskRx(final Response<ResponseBody> response) {
        // take a response and transform it into an observable which emits when the file is saved
        // to disk
        return Observable.create(emitter -> {
            System.out.println("saveToDiskRx");
            try {
                // get header and filename from response
                String header = response.headers().get("Content-Type");
                System.out.println(header);
                String filename = response.headers().get("filename");
//                String filename = header.replace("attachment; filename=", "");
                System.out.println(filename);

                // create a temp file
                // TODO randomly generate filename to avoid conflicts.
                // TODO store saved files in a local dir. (sdcard/radAR/chats/{chatID}/{resID}
                File file = File.createTempFile(filename, null, context.getCacheDir());

                BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
                bufferedSink.writeAll(response.body().source());
                bufferedSink.close();

                emitter.onNext(file);
                emitter.onComplete();


            } catch (IOException e) {
                e.printStackTrace();
                emitter.onError(e);
            }
        });

    }

    /** This method is used to upload resource to server */
    public Observable<UploadFileResponse> uploadFile(MultipartBody.Part filePart) {
        Observable<UploadFileResponse> uploadFileObservable = resourcesApi.uploadResource(userID, token, filePart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        return uploadFileObservable;

    }

    public Observable<File> getResource(String resourceID) {
        System.out.println(resourceID);
        int userID = AuthService.getUserID(context);
        String token = AuthService.getToken(context);
        return resourcesApi.getResource(userID, resourceID, token)
                .flatMap(responseBodyResponse -> saveToDiskRx(responseBodyResponse))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


//    private Observer<File> handleResult() {
//        return new Observer<File>() {
//
//            @Override
//            public void onError(Throwable e) {
//                e.printStackTrace();
////                Log.d(TAG, "Error " + e.getMessage());
//            }
//
//            @Override
//            public void onComplete() {
////                Log.d(TAG, "onCompleted");
//            }
//
//            @Override
//            public void onSubscribe(Disposable d) {
//
//            }
//
//            @Override
//            public void onNext(File file) {
////                Log.d(TAG, "File downloaded to " + file.getAbsolutePath());
//            }
//        };
//    }
}
