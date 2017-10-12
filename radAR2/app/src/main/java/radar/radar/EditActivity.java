package radar.radar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
    private EditText name;
    private EditText email;
    private EditText description;
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

        Context that = this;
        resourcesService.getResource("ff7978b8e2b882321e0d1f03c7d2972e").subscribe(new Observer<File>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(File file) {
                System.out.println(file);
                Picasso.with(that).load(file).into(preview);
            }

            @Override
            public void onError(Throwable e) {
                System.out.println(e);
            }

            @Override
            public void onComplete() {

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

                mediaPath = getRealPathFromURI(selectedImage);
                preview.setImageURI(selectedImage);

                Toast.makeText(this, mediaPath, Toast.LENGTH_SHORT).show();

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
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

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
                System.out.println(e.getMessage());
                Toast.makeText(EditActivity.this, "Go to on error.", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onComplete() {

            }
        });

    }

}
