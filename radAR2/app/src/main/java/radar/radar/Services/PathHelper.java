package radar.radar.Services;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by kenneth on 18/10/17.
 */

public class PathHelper {
    /** Get real path from gallery that are used to initiate a new file */
    public static String getRealPathFromURI(ContentResolver resolver, Uri contentURI) {
        String result;
        Cursor cursor = resolver.query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        System.out.println(result);
        return result;
    }
}
