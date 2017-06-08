package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.view.View;

import com.philliphsu.bottomsheetpickers.R;

import static com.philliphsu.bottomsheetpickers.view.Preconditions.checkNotNull;

public class BottomSheetNumberPadTimePickerDialog extends BottomSheetDialog {

    private final NumberPadTimePickerDialogViewDelegate mViewDelegate;

    public BottomSheetNumberPadTimePickerDialog(@NonNull Context context,
                                                @Nullable OnTimeSetListener listener,
                                                boolean is24HourMode) {
        this(context, 0, listener, is24HourMode);
    }

    // TODO: In your other Dialog subclasses, make sure to overload a constructor that
    // takes a theme parameter, just like this class did. Also be sure to correct
    // any usages of Context as it relates to theming, i.e. use getContext() instead
    // of the provided Context.
    public BottomSheetNumberPadTimePickerDialog(@NonNull Context context, @StyleRes int theme,
                                                @Nullable OnTimeSetListener listener,
                                                boolean is24HourMode) {
        // TODO: Assuming you will create an attribute that would allow clients to provide
        // a reference to a style resource in which it specifies how this Dialog should be
        // styled, you should resolve the provided theme and pass that up to super. You can
        // follow the chain of construction through any of our base classes to write an appropriate
        // resolveDialogTheme() method.
        super(context, theme);
        // This is inflated via the LayoutInflater from this Dialog's Window, which was created
        // with this Dialog's Context. If the Dialog's Context is themed, then the hierarchy
        // inflated is sure to be themed appropriately.
        final View root = getLayoutInflater().inflate(
                R.layout.bsp_bottomsheet_numberpad_time_picker_dialog, null);
        final NumberPadTimePicker timePicker = (NumberPadTimePicker)
                root.findViewById(R.id.bsp_time_picker);
        final View okButton = checkNotNull(timePicker.getOkButton());
        mViewDelegate = new NumberPadTimePickerDialogViewDelegate(this,
                // Prefer getContext() over the provided Context because the Context
                // that the Dialog runs in may not be the same as the provided Context.
                // Follow the chain of construction starting from the call to super().
                // The penultimate call is
                /**    {@link android.app.Dialog#Dialog(Context, int)} */
                // and if we run through its implementation, we eventually see
                /**    {@link android.view.ContextThemeWrapper} */
                // is used as the Dialog's Context. It is a "context wrapper that allows
                // you to modify or replace the theme of the wrapped context", and it
                // works by applying the specified theme on top of the base context's theme.
                getContext(), timePicker, okButton, listener, is24HourMode);
        setContentView(root);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewDelegate.getPresenter().onOkButtonClick();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewDelegate.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Bundle onSaveInstanceState() {
        return mViewDelegate.onSaveInstanceState(super.onSaveInstanceState());
    }

    @Override
    protected void onStop() {
        super.onStop();
        mViewDelegate.onStop();
    }
}
