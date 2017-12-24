package org.schulcloud.mobile;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.schulcloud.mobile.data.DataManager;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.test.common.TestDataFactory;
import org.schulcloud.mobile.ui.settings.SettingsMvpView;
import org.schulcloud.mobile.ui.settings.SettingsPresenter;
import org.schulcloud.mobile.util.RxSchedulersOverrideRule;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Single;

import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class SettingsPresenterTest {

    @Rule
    public final RxSchedulersOverrideRule mOverrideSchedulersRule = new RxSchedulersOverrideRule();
    @Mock
    SettingsMvpView mMockSettingsMvpView;
    @Mock
    DataManager mMockDataManager;
    private SettingsPresenter mSettingsPresenter;

    @Before
    public void setUp() {
        mSettingsPresenter = new SettingsPresenter(mMockDataManager);
        doReturn(Single.just(false))
                .when(mMockDataManager)
                .isInDemoMode();
        mSettingsPresenter.attachView(mMockSettingsMvpView);
    }

    @After
    public void tearDown() {
        mSettingsPresenter.detachView();
    }

    @Test
    public void loadEventsReturnsEvents() {
        List<Event> events = TestDataFactory.makeListEvents(10);
        doReturn(Observable.just(events))
                .when(mMockDataManager)
                .getEvents();

        mSettingsPresenter.loadEvents(false);
        verify(mMockSettingsMvpView).connectToCalendar(events, false);
        verify(mMockSettingsMvpView, never()).showEventsEmpty();
    }

    @Test
    public void loadEventsReturnsEmptyList() {
        doReturn(Observable.just(Collections.emptyList()))
                .when(mMockDataManager)
                .getEvents();

        mSettingsPresenter.loadEvents(false);
        verify(mMockSettingsMvpView).showEventsEmpty();
        verify(mMockSettingsMvpView, never()).connectToCalendar(anyListOf(Event.class), eq(false));
    }

    @Test
    public void loadEventsFails() {
        doReturn(Observable.error(new RuntimeException()))
                .when(mMockDataManager)
                .getEvents();

        mSettingsPresenter.loadEvents(false);
        verify(mMockSettingsMvpView, never()).showEventsEmpty();
        verify(mMockSettingsMvpView, never()).connectToCalendar(anyListOf(Event.class), eq(false));
    }

    @Test
    public void loadDevices() {
        List<Device> devices = TestDataFactory.makeListDevices(10);
        doReturn(Observable.just(devices))
                .when(mMockDataManager)
                .getDevices();

        mSettingsPresenter.loadDevices();
        verify(mMockSettingsMvpView).showDevices(devices);
    }

    @Test
    public void loadDevicesFails() {
        doReturn(Observable.error(new RuntimeException()))
                .when(mMockDataManager)
                .getDevices();

        mSettingsPresenter.loadDevices();
    }

}
