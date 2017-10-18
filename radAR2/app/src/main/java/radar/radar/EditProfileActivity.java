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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import radar.radar.Models.Responses.Status;
import radar.radar.Models.Responses.UploadFileResponse;
import radar.radar.Models.UpdateGroupBody;
import radar.radar.Models.UpdateProfileBody;
import radar.radar.Services.AuthService;
import radar.radar.Services.ResourcesApi;
import radar.radar.Services.ResourcesService;
import radar.radar.Services.UsersApi;
import radar.radar.Services.UsersService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditProfileActivity extends AppCompatActivity {
    private ImageView preview;
    private EditText nameET;
    private EditText email;
    private EditText description;
    private TextView upload;
    ProgressDialog progressDialog;
    String mediaPath;

    private ResourcesService resourcesService;
    UsersService usersService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Setup UI
        setupUI();

        setTitle(getString(R.string.edit_profile));


        // Enable back action bar
//        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayHomeAsUpEnabled(true);

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

        resourcesService.getResourceWithCache(UsersService.getProfilePictureResID(this), this).subscribe(file -> Picasso.with(this).load(file).into(preview),
                System.out::println);

        usersService = new UsersService(this, retrofit.create(UsersApi.class));

        // Setup onclick listener for picking image
        preview.setOnClickListener(view -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 0);
        });

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed()); // enable back button

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Observer<Status> statusObserver = new Observer<Status>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Status status) {
                // TODO loading bar
                if (status.success) {
                    System.out.println("Updated profile picture");
                    finish();
                }
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.done:
                String name = null;
                String emailStr = null;
                String descStr = null;

                if (nameET.getText().toString().trim().length() > 0) {
                    name = nameET.getText().toString();
                }
                if (email.getText().toString().trim().length() > 0) {
                    emailStr = email.getText().toString();
                }
                if (description.getText().toString().trim().length() > 0) {
                    descStr = description.getText().toString();
                }

                if (mediaPath != null) {    // want to update profile pic
                    String finalName = name;
                    String finalEmailStr = emailStr;
                    String finalDescStr = descStr;
                    uploadFile().map(response -> {
                        System.out.println(response.success);
                        return response;
                    })
                            .switchMap(response -> usersService.updateProfile(new UpdateProfileBody(finalName, finalEmailStr, finalDescStr, response.resourceID)))
                            .subscribe(statusObserver);

                } else if (name != null) {  // only update name
                    usersService.updateProfile(new UpdateProfileBody(name, emailStr, descStr,null))
                            .subscribe(statusObserver);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
        nameET = findViewById(R.id.name);
        email = findViewById(R.id.email);
        description = findViewById(R.id.description_text);

        // For now setup the name, email using default
        nameET.setText(AuthService.getFirstName(this) + " " + AuthService.getLastName(this));
        email.setText(AuthService.getEmail(this));
        description.setText("");
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
    private Observable<UploadFileResponse> uploadFile() {
        if (mediaPath != null) {
            // Used to multipart the file using okhttp3
            File file = new File(mediaPath);

            // Parsing any Media type file
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
            MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), requestBody);

            return resourcesService.uploadFile(fileToUpload);
        }

        return null;
    }

}
