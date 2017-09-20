package radar.radar;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import radar.radar.Services.AuthService;

public class LaunchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        String token = AuthService.getToken(this);
        System.out.println("HELLO");
        System.out.println(token);
        Intent intent;
        if (token != null) {
            intent = new Intent(this, HomeScreenActivity.class);
        } else {
            intent = new Intent(this, LoginActivity.class);
        }
        this.startActivity(intent);

    }
}
