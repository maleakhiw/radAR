package radar.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import radar.radar.Services.ResourcesApi;
import radar.radar.Services.ResourcesService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditActivity extends AppCompatActivity {
    private ImageView preview;
    private Button pickImage;
    private Button upload;

    private ResourcesService resourcesService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // Setup UI
        setupUI();

        // Initiate retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://radar.fadhilanshar.com/api/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Create instance of the resource service
        ResourcesApi resourcesApi = retrofit.create(ResourcesApi.class);
        resourcesService = new ResourcesService(resourcesApi, this);

        
    }

    /** Setup UI */
    public void setupUI() {
        preview = findViewById(R.id.preview);
        pickImage = findViewById(R.id.pick_img);
        upload = findViewById(R.id.upload);
    }


}
