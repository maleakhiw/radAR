package radar.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.Gson;

import radar.radar.Presenters.FriendsPresenter;
import radar.radar.Views.FriendsView;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class FriendsActivity extends AppCompatActivity implements FriendsView {

    FriendsPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        presenter = new FriendsPresenter(this, new Retrofit.Builder()
                                                                .baseUrl("http://35.185.35.117/api/")
                                                                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                                                                .addConverterFactory(GsonConverterFactory.create())
                                                                .build());
    }

    @Override
    public void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_LONG);
    }

}
