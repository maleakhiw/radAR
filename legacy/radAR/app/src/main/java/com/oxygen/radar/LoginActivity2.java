package com.oxygen.radar;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.oxygen.radar.Presenters.LoginPresenter;
import com.oxygen.radar.Views.LoginView;

public class LoginActivity2 extends AppCompatActivity implements LoginView {

    LoginPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        presenter = new LoginPresenter(this);
    }

    @Override
    public void showTextToUser(String text) {
        // TODO stub
        System.out.println(text);
    }


}
