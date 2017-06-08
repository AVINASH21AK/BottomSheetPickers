package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;

/**
 * Dialog to type in a time.
 */
public class NumberPadTimePickerDialog extends AlertDialog implements INumberPadTimePicker.View {

    private final NumberPadTimePicker mTimePicker;
    private final INumberPadTimePicker.Presenter mPresenter;

    public NumberPadTimePickerDialog(@NonNull Context context) {
        super(context);

        mTimePicker = new NumberPadTimePicker(context);
        mPresenter = new NumberPadTimePickerPresenter(this);
        mTimePicker.setOnBackspaceClickListener(new OnBackspaceClickListener(mPresenter));
        setView(mTimePicker);

        setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }

    @Override
    public void setNumberKeysEnabled(int start, int end) {
        mTimePicker.setNumberKeysEnabled(start, end);
    }

    @Override
    public void setBackspaceEnabled(boolean enabled) {
        mTimePicker.setBackspaceEnabled(enabled);
    }

    @Override
    public void updateTimeDisplay(CharSequence time) {
        mTimePicker.updateTimeDisplay(time);
    }

    @Override
    public void updateAmPmDisplay(CharSequence ampm) {
        mTimePicker.updateAmPmDisplay(ampm);
    }

    @Override
    public void setOkButtonEnabled(boolean enabled) {
        getButton(BUTTON_POSITIVE).setEnabled(enabled);
    }

    @Override
    public void setAmPmDisplayVisible(boolean visible) {
        mTimePicker.setAmPmDisplayVisible(visible);
    }

    @Override
    public void setAmPmDisplayIndex(int index) {
        mTimePicker.setAmPmDisplayIndex(index);
    }

    private static class OnBackspaceClickListener implements View.OnClickListener {
        private final INumberPadTimePicker.Presenter mPresenter;

        OnBackspaceClickListener(INumberPadTimePicker.Presenter presenter) {
            mPresenter = presenter;
        }

        @Override
        public void onClick(View v) {
            mPresenter.onBackspaceClick();
        }
    }
}
