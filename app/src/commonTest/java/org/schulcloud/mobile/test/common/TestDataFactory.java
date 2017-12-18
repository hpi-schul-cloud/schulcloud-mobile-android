package org.schulcloud.mobile.test.common;

import org.schulcloud.mobile.data.model.CurrentUser;
import org.schulcloud.mobile.data.model.Device;
import org.schulcloud.mobile.data.model.Event;
import org.schulcloud.mobile.data.model.Homework;
import org.schulcloud.mobile.data.model.RealmString;
import org.schulcloud.mobile.data.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.realm.RealmList;

/**
 * Factory class that makes instances of data models with random field values.
 * The aim of this class is to help setting up test fixtures.
 */
public class TestDataFactory {

    public static String randomUuid() {
        return UUID.randomUUID().toString();
    }

    public static User makeUser(String uniqueSuffix) {
        User u = new User();
        u.set_id(randomUuid());
        u.setLastName(uniqueSuffix);
        u.setEmail(uniqueSuffix);
        u.setFirstName(uniqueSuffix);
        return u;
    }
    public static CurrentUser makeCurrentUser(String uniqueSuffix,
            boolean permissionHomeworkCreate) {
        CurrentUser u = new CurrentUser();
        u.set_id(randomUuid());
        u.setLastName(uniqueSuffix);
        u.setEmail(uniqueSuffix);
        u.setFirstName(uniqueSuffix);
        if (permissionHomeworkCreate)
            u.setPermissions(
                    new RealmList<>(new RealmString(CurrentUser.PERMISSION_HOMEWORK_CREATE)));
        return u;
    }

    public static List<User> makeListUsers(int number) {
        List <User> users = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            users.add(makeUser(String.valueOf(i)));
        }
        return users;
    }

    public static Homework makeHomework(String uniqueSuffix) {
        Homework h = new Homework();

        return h;
    }

    public static List<Homework> makeListHomework(int number) {
        List <Homework> homeworks = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            homeworks.add(makeHomework(String.valueOf(i)));
        }
        return homeworks;
    }

    public static Event makeEvent(String uniqueSuffix) {
        Event e = new Event();

        return e;
    }

    public static List<Event> makeListEvents(int number) {
        List <Event> events = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            events.add(makeEvent(String.valueOf(i)));
        }
        return events;
    }


    public static Device makeDevice(String uniqueSuffix) {
        Device d = new Device();

        return d;
    }

    public static List<Device> makeListDevices(int number) {
        List <Device> devices = new ArrayList<>();
        for (int i = 0; i < number; i++) {
            devices.add(makeDevice(String.valueOf(i)));
        }
        return devices;
    }

}