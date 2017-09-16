package com.gohool.tipcalculator.tipcalculator.presenters;

import android.content.Context;
import android.view.View;
import android.widget.SeekBar;

import com.gohool.tipcalculator.tipcalculator.R;
import com.gohool.tipcalculator.tipcalculator.views.MainView;

/**
 * Created by keyst on 8/09/2017.
 */

public class MainPresenterImpl implements MainPresenter {

    private MainView view;
    public MainPresenterImpl(final MainView mainView) {
        this.view = view;

        // initialiase seekbar text
        mainView.setSeekbarText(R.string.initial_percentage);
        mainView.setSeekbarVisibility(View.VISIBLE);

        // set onClickListener
        mainView.setCalculateBtnOnClickListener((View.OnClickListener) view);

        // set seekBarOnChangeListener for the Tip Bar (so it can react to changes)
        mainView.setTipPercentageOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // When the progress change, display the percentage progress to the percentage
                mainView.setSeekbarText(progress + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        mainView.newAlertDialog();

    }
}
