package org.schulcloud.mobile.data.model.requestBodies;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class DeviceRequestTest {
    private static final String SERVICE = "SERVICE";
    private static final String NAME = "NAME";
    private static final String OS = "MAGIC";
    private static final String DEVICE_TOKEN = "UNKNOWN";
    private static final String TYPE = "YESTERDAY";
    private static final String TOKEN = "TRUE";

    private DeviceRequest deviceRequest;

    @Before
    public void setUp() throws Exception {
        deviceRequest = newDeviceRequest();
    }

    @Test
    public void testForConstructor() {
        assertEquals(deviceRequest.service, SERVICE);
        assertEquals(deviceRequest.name, NAME);
        assertEquals(deviceRequest.OS, OS);
        assertEquals(deviceRequest.device_token, DEVICE_TOKEN);
        assertEquals(deviceRequest.type, TYPE);
        assertEquals(deviceRequest.token, TOKEN);
    }

    public static DeviceRequest newDeviceRequest() {
        DeviceRequest deviceRequest = new DeviceRequest(SERVICE, TYPE, NAME, TOKEN, DEVICE_TOKEN, OS);

        return deviceRequest;
    }
}