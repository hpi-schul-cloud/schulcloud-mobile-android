package org.schulcloud.mobile;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.schulcloud.mobile.data.datamanagers.EventDataManager;
import org.schulcloud.mobile.data.datamanagers.NotificationDataManager;
import org.schulcloud.mobile.data.datamanagers.UserDataManager;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.test.common.TestDataFactory;
import org.schulcloud.mobile.ui.settings.SettingsMvpView;
import org.schulcloud.mobile.ui.settings.SettingsPresenter;
import org.schulcloud.mobile.ui.settings.devices.DevicesPresenter;
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
    UserDataManager mMockUserDataManager;
    @Mock
    EventDataManager mMockEventDataManager;
    @Mock
    NotificationDataManager mMockNotificationDataManager;
    private SettingsPresenter mSettingsPresenter;
    private DevicesPresenter mDevicesPresenter;

    @Before
    public void setUp() {
        mSettingsPresenter = new SettingsPresenter(mMockUserDataManager,mMockEventDataManager,
                mMockNotificationDataManager);
        doReturn(Single.just(false))
                .when(mMockUserDataManager)
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
                .when(mMockEventDataManager)
                .getEvents();

        mSettingsPresenter.loadEvents(false);
        verify(mMockSettingsMvpView).connectToCalendar(events, false);
        verify(mMockSettingsMvpView, never()).showEventsEmpty();
    }

    @Test
    public void loadEventsReturnsEmptyList() {
        doReturn(Observable.just(Collections.emptyList()))
                .when(mMockEventDataManager)
                .getEvents();

        mSettingsPresenter.loadEvents(false);
        verify(mMockSettingsMvpView).showEventsEmpty();
        verify(mMockSettingsMvpView, never()).connectToCalendar(anyListOf(Event.class), eq(false));
    }

    @Test
    public void loadEventsFails() {
        doReturn(Observable.error(new RuntimeException()))
                .when(mMockEventDataManager)
                .getEvents();

        mSettingsPresenter.loadEvents(false);
        verify(mMockSettingsMvpView, never()).showEventsEmpty();
        verify(mMockSettingsMvpView, never()).connectToCalendar(anyListOf(Event.class), eq(false));
    }

    @Test
    public void loadDevices() {
        List<Device> devices = TestDataFactory.makeListDevices(10);
        doReturn(Observable.just(devices))
                .when(mMockNotificationDataManager)
                .getDevices();

        mDevicesPresenter.loadDevices();
        verify(mMockSettingsMvpView).showDevices(devices);
    }

    @Test
    public void loadDevicesFails() {
        doReturn(Observable.error(new RuntimeException()))
                .when(mMockNotificationDataManager)
                .getDevices();

        mSettingsPresenter.loadDevices();
    }

}
