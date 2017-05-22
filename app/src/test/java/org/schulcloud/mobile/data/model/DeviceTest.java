package org.schulcloud.mobile.data.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeviceTest {
    private static final String _ID = "_ID";
    private static final String TOKEN = "TOKEN";
    private static final String TYPE = "MOBILE";
    private static final String SERVICE = "SERVICE";
    private static final String NAME = "NAME";
    private static final String OS = "MAGIC";
    private static final String STATE = "UNKNOWN";
    private static final String UPDATEDAT = "YESTERDAY";
    private static final String CREATEDAT = "NOW";
    private static final String ACTIVE = "TRUE";

    private Device device;

    @Before
    public void setUp() {
        device = createNewDevice();
    }

    @Test
    public void testGetProperties() {
        assertEquals(device.name, NAME);
        assertEquals(device._id, _ID);
        assertEquals(device.token, TOKEN);
        assertEquals(device.type, TYPE);
        assertEquals(device.service, SERVICE);
        assertEquals(device.OS, OS);
        assertEquals(device.state, STATE);
        assertEquals(device.updatedAt, UPDATEDAT);
        assertEquals(device.createdAt, CREATEDAT);
        assertEquals(device.active, ACTIVE);
    }

    public static Device createNewDevice() {
        Device device = new Device();
        device.name = NAME;
        device._id = _ID;
        device.token = TOKEN;
        device.type = TYPE;
        device.service = SERVICE;
        device.OS = OS;
        device.state = STATE;
        device.createdAt = CREATEDAT;
        device.updatedAt = UPDATEDAT;
        device.active = ACTIVE;

        return device;
    }

}