package domain.testapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import domain.testapplication.presenters.MainPresenter;
import domain.testapplication.presenters.MainPresenterImpl;
import domain.testapplication.views.MainView;

public class MainActivity extends AppCompatActivity implements MainView {

    MainPresenter presenter;

    TextView mainViewTV;
    ListView mainViewLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainViewTV = (TextView) findViewById(R.id.mainViewTV);
        mainViewLV = (ListView) findViewById(R.id.mainViewLV);

        presenter = new MainPresenterImpl(this);

        presenter.loadData();
    }

    @Override
    public void changeLabelText(String labelText) {
        if (mainViewTV != null) {
            mainViewTV.setText(labelText);
        }
    }

    @Override
    public String getLabelText() {
        if (mainViewTV != null) {
            return (String) mainViewTV.getText();
        } else {
            return null;
        }
    }

    @Override
    public void setListViewOnClickListener(View.OnClickListener onClickListener) {
        if (mainViewLV != null) {
            mainViewLV.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void setListViewArrayAdapter(ArrayAdapter<String> arrayAdapter) {
        if (mainViewLV != null) {
            mainViewLV.setAdapter(arrayAdapter);
        }
    }
}
