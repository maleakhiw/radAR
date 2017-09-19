package radar.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HomeScreenActivity extends AppCompatActivity {
    private TextView home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        setupUI();
    }

    public void setupUI() {
        home = (TextView) findViewById(R.id.home);
    }
}
