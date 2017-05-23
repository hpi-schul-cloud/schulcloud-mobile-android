package org.schulcloud.mobile.data.model.responseBodies;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class DeviceResponseTest {
    private static final String TYPE = "LUKE";
    private static final String ID = "VADER";

    private DeviceResponse deviceResponse;

    @Before
    public void setUp() throws Exception {
        deviceResponse = createDeviceResponse();
    }

    @Test
    public void testGetProperties() {
        assertEquals(deviceResponse.type, TYPE);
        assertEquals(deviceResponse.id, ID);
    }

    public static DeviceResponse createDeviceResponse() {
        DeviceResponse deviceResponse = new DeviceResponse();
        deviceResponse.type = TYPE;
        deviceResponse.id = ID;

        return deviceResponse;
    }

}