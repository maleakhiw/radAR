package radar.radar;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v13.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import radar.radar.Models.Responses.UploadFileResponse;
import radar.radar.Services.AuthService;
import radar.radar.Services.ResourcesApi;
import radar.radar.Services.ResourcesService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView preview;
    private EditText name;
    private EditText email;
    private EditText description;
    private TextView upload;
    ProgressDialog progressDialog;
    String mediaPath;

    private ResourcesService resourcesService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Setup UI
        setupUI();

        // Enable back action bar
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

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
        resourcesService = new ResourcesService(this, resourcesApi);

        // Setup onclick listener for picking image
        preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, 0);
            }
        });

        // Setup onclick listener for upload
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Uploading the image
                int permissionCheck = ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                    uploadFile();
                } else {    // PERMISSION_DENIED
                    ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                }
            }
        });

        Context that = this;
        resourcesService.getResource("ff7978b8e2b882321e0d1f03c7d2972e", this).subscribe(new Observer<File>() {
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


    /** Method that are used for the back */
    public boolean onOptionsItemSelected(MenuItem item){
        finish();
        return true;
    }

    /** Permission for getting image from gallery and uploading it */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == 1) {
            // if request is cancelled, the result arrays are empty
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted!
                uploadFile();

            } else {
                // permission denied!
                Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
            }
        }
    }



    /** Method for getting the activity result since we expect result */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == 0 && resultCode == RESULT_OK) {
                // Get the Image from data
                Uri selectedImage = data.getData();

                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity(selectedImage)
                        .setAspectRatio(1, 1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(this);
            }

            // Process the cropped image
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    preview.setImageURI(resultUri);
                    mediaPath = getRealPathFromURI(resultUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }

    }

    /** Setup UI */
    public void setupUI() {
        preview = findViewById(R.id.preview);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        description = findViewById(R.id.description_text);
        upload = findViewById(R.id.upload);

        // For now setup the name, email using default
        name.setText(AuthService.getFirstName(this) + " " + AuthService.getLastName(this));
        email.setText(AuthService.getEmail(this));
        description.setText("Hello, I am using Radar!");
    }

    /** Get real path from gallery that are used to initiate a new file */
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

        if (mediaPath != null) {
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
                    Toast.makeText(EditProfileActivity.this, "Successfully upload file", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onError(Throwable e) {
                    progressDialog.dismiss();
                    // Display error message
                    System.out.println(e.getMessage());
                    Toast.makeText(EditProfileActivity.this, "Go to on error.", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onComplete() {

                }
            });
        } else {
            // TODO warn
            Log.w("uploadFile", "file is null");
        }


    }

}
