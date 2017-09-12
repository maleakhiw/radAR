package com.gohool.tipcalculator.tipcalculator.views;

import android.view.View;
import android.widget.SeekBar;

/**
 * Created by keyst on 8/09/2017.
 */

public interface MainView {
    void newAlertDialog();

    void setSeekbarText(int text);

    void setSeekbarText(String text);

    void setSeekbarVisibility(int visibility);

    void setCalculateBtnOnClickListener(View.OnClickListener onClickListener);

    void setTipPercentageOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener onSeekBarChangeListener);
}
