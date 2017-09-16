package domain.testapplication.views;

import android.view.View;
import android.widget.ArrayAdapter;

/**
 * Created by keyst on 8/09/2017.
 */

public interface MainView {
    void changeLabelText(String labelText);
    String getLabelText();
    void setListViewOnClickListener(View.OnClickListener onClickListener);
    void setListViewArrayAdapter(ArrayAdapter<String> arrayAdapter);
}
