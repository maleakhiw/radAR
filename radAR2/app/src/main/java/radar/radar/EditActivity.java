package radar.radar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.net.URI;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import radar.radar.Models.Android.CameraData;
import radar.radar.Models.Responses.Status;
import radar.radar.Models.Responses.UploadFileResponse;
import radar.radar.Services.ResourcesApi;
import radar.radar.Services.ResourcesService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditActivity extends AppCompatActivity {
    private ImageView preview;
    private Button pickImage;
    private Button upload;
    String[] mediaColumns = { MediaStore.Video.Media._ID };
    ProgressDialog progressDialog;
    String mediaPath;

    private ResourcesService resourcesService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Setup UI
        setupUI();

        // Setup progress dialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");

        // Initiate retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://radar.fadhilanshar.com/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create instance of the resource service
        ResourcesApi resourcesApi = retrofit.create(ResourcesApi.class);
        resourcesService = new ResourcesService(resourcesApi, this);

        // Setup onclick listener for button
        upload.setOnClickListener(view -> {
            int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                uploadFile();
            } else {    // PERMISSION_DENIED
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }

        });

        // Setup onclick listener for picking image
        pickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_PICK);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 0);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            // if request is cancelled, the result arrays are empty
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted!
                uploadFile();

            } else {
                // permission denied!
                // TODO show TextView in activity, say that permission was not granted
            }
        }
    }



    /** Method for getting the activity result since we expect result */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 0 && resultCode == RESULT_OK && null != data) {

                // Get the Image from data
                Uri selectedImage = data.getData();

//                mediaPath = selectedImage.getPath().substring(6);
                mediaPath = getRealPathFromURI(selectedImage);
                preview.setImageURI(selectedImage);

                Toast.makeText(this, mediaPath, Toast.LENGTH_SHORT).show();

//                Uri selectedImage = data.getData();
//                file = new File(selectedImage.getPath());
//
//                //                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
////                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
////
//
////                if (cursor != null) {
////                    int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
////                    cursor.moveToFirst();
////                    mediaPath = cursor.getString(columnIndex);
////                    cursor.close();
////                    Toast.makeText(this, mediaPath, Toast.LENGTH_SHORT).show();
////                }
////                else {
////                    Toast.makeText(this, "cursor null", Toast.LENGTH_SHORT).show();
////                }
//
//                // Set the Image in ImageView for Previewing the Media
//                preview.setImageURI(selectedImage);

            } else {
                Toast.makeText(this, "You haven't picked Image/Video", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }

    /** Setup UI */
    public void setupUI() {
        preview = findViewById(R.id.preview);
        pickImage = findViewById(R.id.pick_img);
        upload = findViewById(R.id.upload);
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
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

    /** Use for uploading file */
    private void uploadFile() {
        progressDialog.show(); // start the progress dialog

        // Used to multipart the file using okhttp3
        File file = new File(mediaPath);

        // Parsing any Media type file
        RequestBody requestBody = RequestBody.create(MediaType.parse("*/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
//        RequestBody filename = RequestBody.create(MediaType.parse("text/plain"), file.getName());

        // Make a request
        resourcesService.uploadFile(fileToUpload).subscribe(new Observer<UploadFileResponse>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(UploadFileResponse response) {
                progressDialog.dismiss();
                System.out.println(response.resourceID);
                // After we upload File, show success message
                Toast.makeText(EditActivity.this, "Successfully upload file", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Throwable e) {
                progressDialog.dismiss();
                // Display error message
                Log.d("ANJING", e.getMessage());
                Toast.makeText(EditActivity.this, "Go to on error.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onComplete() {

            }
        });

    }

    /** Getting path from uri */
    public String getImagePath(Uri uri){
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":")+1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

}
