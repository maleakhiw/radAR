package radar.radar;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import radar.radar.Adapters.EditGroupAdapter;
import radar.radar.Models.Domain.Group;
import radar.radar.Models.Responses.Status;
import radar.radar.Models.Responses.UploadFileResponse;
import radar.radar.Models.UpdateGroupBody;
import radar.radar.Services.GroupsApi;
import radar.radar.Services.GroupsService;
import radar.radar.Services.PathHelper;
import radar.radar.Services.ResourcesApi;
import radar.radar.Services.ResourcesService;
import radar.radar.Services.UsersService;
import retrofit2.Retrofit;

public class EditGroupActivity extends AppCompatActivity implements EditGroupView {

    private RecyclerView recyclerView;
    private Group group;

    private EditText groupName;
    private ImageView picture;
    private TextView deleteGroup;

    private GroupsService groupsService;
    private ResourcesService resourcesService;
    private UsersService usersService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_group);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(v -> onBackPressed()); // enable back button

        // TODO handle rotation changes
        loadGroupDataFromIntent();

        setupRecyclerView();

        picture = findViewById(R.id.profile_picture);
        picture.setOnClickListener(view -> {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            startActivityForResult(galleryIntent, 0);
        });

        Retrofit retrofit = RetrofitFactory.getRetrofitBuilder().build();
        GroupsApi groupsApi = retrofit.create(GroupsApi.class);
        groupsService = new GroupsService(this, groupsApi);
        resourcesService = new ResourcesService(this, retrofit.create(ResourcesApi.class));

        // load existing image
        resourcesService.getResourceWithCache(group.profilePicture, this).subscribe(file -> Picasso.with(this).load(file).into(picture), error -> System.out.println(error));

        deleteGroup = findViewById(R.id.delete_group);
        deleteGroup.setOnClickListener(view -> {
           groupsService.deleteGroup(group.groupID).subscribe(new Observer<Status>() {
               @Override
               public void onSubscribe(Disposable d) {

               }

               @Override
               public void onNext(Status status) {
                   if (status.success) {
                       finish();
                   }
               }

               @Override
               public void onError(Throwable e) {

               }

               @Override
               public void onComplete() {

               }
           });
        });
    }

    private void loadGroupDataFromIntent() {
        Intent intent = getIntent();
        group = (Group) intent.getSerializableExtra("group");
        if (group != null) {
            System.out.println(group);
        }

        setTitle(group.name);

        groupName = findViewById(R.id.group_name_edit_text);

    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.group_members_recyclerView);
        EditGroupAdapter groupMembersAdapter = new EditGroupAdapter(this, group.usersDetails, group);  // getContext becomes getActivity inside a fragment
        recyclerView.setAdapter(groupMembersAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));   // layout manager to position items
        groupMembersAdapter.notifyDataSetChanged();
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

                if (groupName.getText().toString().trim().length() > 0) {
                    name = groupName.getText().toString();
                }

                if (mediaPath != null) {    // want to update profile pic
                    String finalName = name;
                    uploadFile().map(response -> {
                        System.out.println(response.success);
                        return response;
                    })
                    .switchMap(response -> groupsService.updateGroup(group.groupID, new UpdateGroupBody(finalName, response.resourceID)))
                    .subscribe(statusObserver);

                } else if (name != null) {  // only update name
                    groupsService.updateGroup(group.groupID, new UpdateGroupBody(name, null)).subscribe(statusObserver);
                }

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    String mediaPath;

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
                    picture.setImageURI(resultUri);
                    mediaPath = PathHelper.getRealPathFromURI(getContentResolver(), resultUri);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                }
            }

        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
        }
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

interface EditGroupView {

}

class EditGroupPresenter {
    EditGroupView view;
    Group group;

    public EditGroupPresenter(EditGroupView view, Group group) {
        this.view = view;
        this.group = group;
    }

    public void loadData() {

    }


}
