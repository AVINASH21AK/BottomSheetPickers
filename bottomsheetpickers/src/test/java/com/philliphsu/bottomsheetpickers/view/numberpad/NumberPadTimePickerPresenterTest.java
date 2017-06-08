package com.philliphsu.bottomsheetpickers.view.numberpad;

import com.philliphsu.bottomsheetpickers.view.LocaleModel;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class NumberPadTimePickerPresenterTest {
    static final int MODE_12HR = 0;
    static final int MODE_24HR = 1;

    private final INumberPadTimePicker.View[] mViews = new INumberPadTimePicker.View[2];
    private final INumberPadTimePicker.Presenter[] mPresenters = new INumberPadTimePicker.Presenter[2];

    @Mock
    private LocaleModel mLocaleModel;

    @Before
    public final void setup() {
        // Inject mocks annotated with the @Mock annotation.
        MockitoAnnotations.initMocks(this);
        setupMockViews();
        setupPresenters();
    }

    @Test
    public void verifyViewEnabledStatesForEmptyState() {
        mPresenters[MODE_12HR].onCreate(NumberPadTimePickerState.EMPTY);
        verify(mViews[MODE_12HR]).setNumberKeysEnabled(1, 10);
        verify(mViews[MODE_12HR]).setBackspaceEnabled(false);
        // Assuming no initial text for the time display, there is no need to have to call this.
        verify(mViews[MODE_12HR], times(0)).updateTimeDisplay(null /* value doesn't matter */);
        verify(mViews[MODE_12HR]).updateAmPmDisplay(null);
        verify(mViews[MODE_12HR]).setAmPmDisplayVisible(true);
        verify(mViews[MODE_12HR]).setAmPmDisplayIndex(mLocaleModel.isAmPmWrittenBeforeTime() ? 0 : 1);
        /* Verify setLeftAltKeyText() and setRightAltKeyText() manually. */
        verify(mViews[MODE_12HR]).setLeftAltKeyEnabled(false);
        verify(mViews[MODE_12HR]).setRightAltKeyEnabled(false);
        verify(mViews[MODE_12HR]).setHeaderDisplayFocused(true);

        mPresenters[MODE_24HR].onCreate(NumberPadTimePickerState.EMPTY);
        verify(mViews[MODE_24HR]).setNumberKeysEnabled(0, 10);
        verify(mViews[MODE_24HR]).setBackspaceEnabled(false);
        verify(mViews[MODE_24HR], times(0)).updateTimeDisplay(null /* value doesn't matter */);
        verify(mViews[MODE_24HR], times(0)).updateAmPmDisplay(null /* value doesn't matter */);
        verify(mViews[MODE_24HR]).setAmPmDisplayVisible(false);
        verify(mViews[MODE_24HR], times(0)).setAmPmDisplayIndex(0 /* value doesn't matter */);
        /* Verify setLeftAltKeyText() and setRightAltKeyText() manually. */
        verify(mViews[MODE_24HR]).setLeftAltKeyEnabled(false);
        verify(mViews[MODE_24HR]).setRightAltKeyEnabled(false);
        verify(mViews[MODE_24HR]).setHeaderDisplayFocused(true);
    }

    @Test
    public void clickOnNumberKey_UpdatesTimeDisplay() {
        mPresenters[MODE_12HR].onNumberKeyClick("1");
        verify(mViews[MODE_12HR]).updateTimeDisplay("1");
    }

    INumberPadTimePicker.View getView(int mode) {
        return mViews[mode];
    }

    INumberPadTimePicker.Presenter getPresenter(int mode) {
        return mPresenters[mode];
    }

    Class<? extends INumberPadTimePicker.View> getViewClass() {
        return INumberPadTimePicker.View.class;
    }

    INumberPadTimePicker.Presenter createPresenter(INumberPadTimePicker.View view,
                                                   LocaleModel localeModel,
                                                   boolean is24HourMode) {
        return new NumberPadTimePickerPresenter(view, localeModel, is24HourMode);
    }

    private void setupMockViews() {
        initMockView(MODE_12HR);
        initMockView(MODE_24HR);
    }

    private void setupPresenters() {
        initPresenter(MODE_12HR);
        initPresenter(MODE_24HR);
    }

    private void initMockView(int mode) {
        if (mViews[mode] == null) {
            mViews[mode] = mock(getViewClass());
        }
    }

    private void initPresenter(int mode) {
        if (mPresenters[mode] == null) {
            mPresenters[mode] = createPresenter(mViews[mode], mLocaleModel, mode == MODE_24HR);
        }
    }
}
