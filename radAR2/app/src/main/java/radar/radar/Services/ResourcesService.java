package radar.radar.Services;

import android.content.Context;
import android.content.SharedPreferences;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.Okio;
import radar.radar.Models.Responses.UploadFileResponse;
import retrofit2.Response;

/**
 * Created by maleakhiw on 10/10/2017
 */

public class ResourcesService {
    Context context;
    ResourcesApi resourcesApi;
    int userID;
    String token;

    public ResourcesService(Context context, ResourcesApi resourcesApi) {
        this.context = context;
        this.resourcesApi = resourcesApi;
        userID = AuthService.getUserID(context);
        token = AuthService.getToken(context);
    }

    // http://www.codexpedia.com/android/retrofit-2-and-rxjava-for-file-downloading-in-android/
    // adapted for RxJava2

    private Observable<File> saveToDiskRxAndCache(final Response<ResponseBody> response, String fileID) {
        return saveToDiskRx(response).map(file -> {
            // copy to FileOutputStream
            FileOutputStream fos = context.openFileOutput(fileID, Context.MODE_PRIVATE);
            FileInputStream fileInputStream = new FileInputStream(file);

            int c = 0;
            byte[] buf = new byte[8192];

            while ((c = fileInputStream.read(buf, 0, buf.length)) > 0) {
                fos.write(buf, 0, c);
                fos.flush();
            }

            fos.close();
            System.out.println("stop");
            fileInputStream.close();

            return file;
        });
    }

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
                if (filename == null) {
                    filename = "temp";
                }
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

    /**
     * Gets a resource from the server.
     * @param resourceID resource to get
     * @param context Android Context, to load data from SharedPreferences and internal storage
     * @return
     */
    public Observable<File> getResource(String resourceID, Context context) {
        System.out.println(resourceID);
        int userID = AuthService.getUserID(context);
        String token = AuthService.getToken(context);
        return resourcesApi.getResource(userID, resourceID, token)
                .flatMap(responseBodyResponse -> saveToDiskRx(responseBodyResponse))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * Gets a resource from the server with caching enabled. Ideally only use these for images.
     * @param resourceID resource to get
     * @param context Android Context, to load data from SharedPreferences and internal storage
     * @return
     */
    public Observable<File> getResourceWithCache(String resourceID, Context context) {
        System.out.println(resourceID);

        // Restore preferences
        SharedPreferences prefs = context.getSharedPreferences("radar.radar", Context.MODE_PRIVATE);

        if (prefs.contains(resourceID)) {
            System.out.println("File has been cached");
            try {
                FileInputStream fis = context.openFileInput(resourceID);

                File tempFile = File.createTempFile("tmp", "suffix");
                FileOutputStream tempFileOutputStream = new FileOutputStream(tempFile);
                IOUtils.copy(fis, tempFileOutputStream);

                return Observable.just(tempFile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        int userID = AuthService.getUserID(context);
        String token = AuthService.getToken(context);
        return resourcesApi.getResource(userID, resourceID, token)
                .flatMap(responseBodyResponse -> saveToDiskRxAndCache(responseBodyResponse, resourceID))
                .map(file -> {
                    prefs.edit().putBoolean(resourceID, true).commit();
                    return file;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
