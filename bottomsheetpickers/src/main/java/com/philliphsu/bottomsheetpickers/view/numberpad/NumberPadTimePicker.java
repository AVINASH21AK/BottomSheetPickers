package com.philliphsu.bottomsheetpickers.view.numberpad;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.IntDef;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.philliphsu.bottomsheetpickers.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

// TODO: Declare an attribute with format="reference" to allow a style resource to be specified.
class NumberPadTimePicker extends LinearLayout implements INumberPadTimePicker.View {
    /**
     * Color attributes defined in our {@code Context}'s theme.
     *
     * Used only in {@link #LAYOUT_BOTTOM_SHEET} to set the default colors for the FAB in code,
     * rather than in XML because themed colors can only be resolved in code below API 23.
     */
    private static final int[] ATTRS_FAB_COLORS = { R.attr.colorButtonNormal, R.attr.colorAccent };
    /**
     * Attributes defined in our {@code Context}'s theme. The same base color is used
     * for both the disabled and enabled states, except the disabled state will apply
     * the alpha value specified in the {@code disabledAlpha} attribute.
     *
     * Used to set the default text color of the number keys in code, rather than in XML
     * because themed colors can only be resolved in code below API 23.
     */
    private static final int[] ATTRS_NUMBER_KEYS_TEXT_COLOR = { android.R.attr.disabledAlpha,
            R.attr.colorPrimary };

    /** States for making ColorStateLists. */
    private static final int[][] ENABLED_STATES = {{-android.R.attr.state_enabled}, {}};

    /** Option to layout this view for use in an alert dialog. */
    static final int LAYOUT_ALERT = 1;
    /** Option to layout this view for use in a bottom sheet dialog. */
    static final int LAYOUT_BOTTOM_SHEET = 2;

    @IntDef({LAYOUT_ALERT, LAYOUT_BOTTOM_SHEET})
    @Retention(RetentionPolicy.SOURCE)
    @interface NumberPadTimePickerLayout {}

    /** Option to always show the FAB. */
    static final int SHOW_FAB_ALWAYS = 0;
    /** Option to only show the FAB when the inputted sequence makes a valid time. */
    static final int SHOW_FAB_VALID_TIME = 1;

    @IntDef({SHOW_FAB_ALWAYS, SHOW_FAB_VALID_TIME})
    @Retention(RetentionPolicy.SOURCE)
    @interface ShowFabPolicy {}

    private NumberPadView mNumberPad;
    private LinearLayout mHeaderLayout;
    private TextView mTimeDisplay;
    private TextView mAmPmDisplay;
    private View mBackspace;
    private @Nullable View mOkButton;

    private @NumberPadTimePickerLayout int mLayout;
    private @ShowFabPolicy int mShowFabPolicy;
    private boolean mAnimateFabIn;

    public NumberPadTimePicker(Context context) {
        this(context, null);
    }

    public NumberPadTimePicker(Context context, AttributeSet attrs) {
        this(context, attrs, 0 /*TODO: Pass our attribute here. This contains a reference to a style resource.*/);
    }

    public NumberPadTimePicker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @TargetApi(21)
    public NumberPadTimePicker(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);
    }

    // TODO: Apply the style resource, either the one contained in defStyleAttr or defStyleRes itself.
    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        setOrientation(VERTICAL);

        final TypedArray timePickerAttrs = context.obtainStyledAttributes(attrs,
                R.styleable.BSP_NumberPadTimePicker, defStyleAttr, defStyleRes);
        final @NumberPadTimePickerLayout int layout = retrieveLayout(timePickerAttrs);

        final @LayoutRes int layoutRes = layout == LAYOUT_BOTTOM_SHEET
                ? R.layout.bsp_bottomsheet_numberpad_time_picker
                : R.layout.bsp_numberpad_time_picker;
        inflate(context, layoutRes, this);

        mLayout = layout;
        mNumberPad = (NumberPadView) findViewById(R.id.bsp_numberpad_time_picker_view);
        mHeaderLayout = (LinearLayout) findViewById(R.id.bsp_input_time_container);
        mTimeDisplay = (TextView) findViewById(R.id.bsp_input_time);
        mAmPmDisplay = (TextView) findViewById(R.id.bsp_input_ampm);
        mBackspace = findViewById(R.id.bsp_backspace);
        mOkButton = findViewById(R.id.bsp_ok_button);

        if (mLayout == LAYOUT_BOTTOM_SHEET && mOkButton instanceof FloatingActionButton) {
            final ColorStateList fabBackgroundColor = retrieveFabBackgroundColor(
                    timePickerAttrs, context);
            // If we could not create a default ColorStateList, then just leave the current
            // color as is.
            if (fabBackgroundColor != null) {
                // If we don't make this cast, this would call the base method in View,
                // which requires API 21.
                ((FloatingActionButton) mOkButton).setBackgroundTintList(fabBackgroundColor);
            }

            final boolean animateFabIn = retrieveAnimateFabIn(timePickerAttrs);
            final @ShowFabPolicy int showFabPolicy = retrieveShowFab(timePickerAttrs);
            // For the FAB to actually animate in, it cannot be visible initially.
            mOkButton.setVisibility(animateFabIn || showFabPolicy == SHOW_FAB_VALID_TIME
                    ? INVISIBLE : VISIBLE);

            mAnimateFabIn = animateFabIn;
            mShowFabPolicy = showFabPolicy;

        }

        timePickerAttrs.recycle();
    }

    @Override
    public void setNumberKeysEnabled(int start, int end) {
        mNumberPad.setNumberKeysEnabled(start, end);
    }

    @Override
    public void setBackspaceEnabled(boolean enabled) {
        mBackspace.setEnabled(enabled);
    }

    @Override
    public void updateTimeDisplay(CharSequence time) {
        mTimeDisplay.setText(time);
    }

    @Override
    public void updateAmPmDisplay(CharSequence ampm) {
        mAmPmDisplay.setText(ampm);
    }

    @Override
    public void setAmPmDisplayVisible(boolean visible) {
        mAmPmDisplay.setVisibility(visible ? VISIBLE : GONE);
    }

    @Override
    public void setAmPmDisplayIndex(int index) {
        if (index != 0 && index != 1) {
            throw new IllegalArgumentException("Index of AM/PM display must be 0 or 1. index == " + index);
        }
        if (index == 1) return;
        mHeaderLayout.removeViewAt(1);
        mHeaderLayout.addView(mAmPmDisplay, 0);
    }

    @Override
    public void setLeftAltKeyText(CharSequence text) {
        mNumberPad.setLeftAltKeyText(text);
    }

    @Override
    public void setRightAltKeyText(CharSequence text) {
        mNumberPad.setRightAltKeyText(text);
    }

    @Override
    public void setLeftAltKeyEnabled(boolean enabled) {
        mNumberPad.setLeftAltKeyEnabled(enabled);
    }

    @Override
    public void setRightAltKeyEnabled(boolean enabled) {
        mNumberPad.setRightAltKeyEnabled(enabled);
    }

    @Deprecated
    @Override
    public void setHeaderDisplayFocused(boolean focused) {
        // Do nothing.
    }

    @Nullable
    View getOkButton() {
        return mOkButton;
    }

    @NumberPadTimePickerLayout
    int getLayout() {
        return mLayout;
    }

    boolean isAnimateFabIn() {
        checkLayoutIsBottomSheet(mLayout);
        return mAnimateFabIn;
    }

    @ShowFabPolicy
    int getShowFabPolicy() {
        checkLayoutIsBottomSheet(mLayout);
        return mShowFabPolicy;
    }

    void setOnBackspaceClickListener(OnClickListener l) {
        mBackspace.setOnClickListener(l);
    }

    void setOnBackspaceLongClickListener(OnLongClickListener l) {
        mBackspace.setOnLongClickListener(l);
    }

    void setOnNumberKeyClickListener(OnClickListener l) {
        mNumberPad.setOnNumberKeyClickListener(l);
    }

    void setOnAltKeyClickListener(OnClickListener l) {
        mNumberPad.setOnAltKeyClickListener(l);
    }

    private static void checkLayoutIsBottomSheet(int layout) {
        if (layout != LAYOUT_BOTTOM_SHEET) {
            throw new UnsupportedOperationException("Layout must be LAYOUT_BOTTOM_SHEET to call this method");
        }
    }

    @Nullable
    private static ColorStateList retrieveFabBackgroundColor(
            TypedArray timePickerAttrs, Context context) {
        ColorStateList fabBackgroundColor = timePickerAttrs.getColorStateList(
                R.styleable.BSP_NumberPadTimePicker_bsp_fabBackgroundColor);
        if (fabBackgroundColor == null) {
            // Set default ColorStateList.
            // Themed color attributes in a ColorStateList defined in XML cannot be resolved
            // correctly below API 23, so we can only do this in code.

            // We must create a different TypedArray here rather than use the previous instance
            // because it was configured to only retrieve values for NumberPadTimePicker
            // attributes.
            final TypedArray themedColors = context.obtainStyledAttributes(ATTRS_FAB_COLORS);
            // The first argument in this set of calls is the index of the attribute in the
            // attributes array for which we would like to get the resolved value.
            // The second argument is the default value to return if a value for the attribute
            // could not be found.
            final int disabledColor = themedColors.getColor(0, 0);
            final int defaultColor = themedColors.getColor(1, 0);
            themedColors.recycle();
            if (disabledColor != 0 && defaultColor != 0) {
                fabBackgroundColor = makeColorStateList(disabledColor, defaultColor);
            }
        }
        return fabBackgroundColor;
    }

    @Nullable
    private static ColorStateList retrieveNumberKeysTextColor(TypedArray timePickerAttrs, Context context) {
        ColorStateList textColor = timePickerAttrs.getColorStateList(
                R.styleable.BSP_NumberPadTimePicker_bsp_numberKeysTextColor);
        if (textColor == null) {
            // Set default ColorStateList.
            // Themed color attributes in a ColorStateList defined in XML cannot be resolved
            // correctly below API 23, so we can only do this in code.

            // We must create a different TypedArray here rather than use the previous instance
            // because it was configured to only retrieve values for NumberPadTimePicker
            // attributes.
            final TypedArray themedColors = context.obtainStyledAttributes(
                    ATTRS_NUMBER_KEYS_TEXT_COLOR);
            // The first argument in this set of calls is the index of the attribute in the
            // attributes array for which we would like to get the resolved value.
            // The second argument is the default value to return if a value for the attribute
            // could not be found.
            final float disabledAlpha = themedColors.getFloat(0, 0.5f);
            final int defaultColor = themedColors.getColor(1, 0);
            themedColors.recycle();
            if (defaultColor != 0) {
                // https://developer.android.com/reference/android/content/res/ColorStateList.html
                // Items in a ColorStateList (defined in XML) can specify an 'android:alpha' attribute
                // to modify the base color's opacity. The item's overall color is calculated by
                // multiplying the base color's alpha channel by the alpha value.
                // This is the equivalent in code.
                final int disabledColor = Color.argb((int) (Color.alpha(defaultColor) * disabledAlpha),
                        Color.red(defaultColor), Color.green(defaultColor), Color.blue(defaultColor));
                textColor = makeColorStateList(disabledColor, defaultColor);
            }
        }
        return textColor;
    }

    @NonNull
    private static ColorStateList makeColorStateList(int disabledColor, int enabledColor) {
        return new ColorStateList(ENABLED_STATES, new int[] { disabledColor, enabledColor });
    }

    private static boolean retrieveAnimateFabIn(TypedArray timePickerAttrs) {
        return timePickerAttrs.getBoolean(R.styleable.BSP_NumberPadTimePicker_bsp_animateFabIn, false);
    }

    @NumberPadTimePickerLayout
    private static int retrieveLayout(TypedArray timePickerAttrs) {
        final int layout = timePickerAttrs.getInt(
                R.styleable.BSP_NumberPadTimePicker_bsp_numberPadTimePickerLayout, LAYOUT_ALERT);
        switch (layout) {
            case LAYOUT_ALERT:
            case LAYOUT_BOTTOM_SHEET:
                return layout;
            default:
                return LAYOUT_ALERT;
        }
    }

    @ShowFabPolicy
    private static int retrieveShowFab(TypedArray timePickerAttrs) {
        final int policy = timePickerAttrs.getInt(
                R.styleable.BSP_NumberPadTimePicker_bsp_showFab, SHOW_FAB_ALWAYS);
        switch (policy) {
            case SHOW_FAB_ALWAYS:
            case SHOW_FAB_VALID_TIME:
                return policy;
            default:
                return SHOW_FAB_ALWAYS;
        }
    }
}
