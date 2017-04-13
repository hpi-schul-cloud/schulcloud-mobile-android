package org.schulcloud.mobile.data.model;

import org.parceler.Parcel;

import io.realm.AccessTokenRealmProxy;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

@Parcel(implementations = { AccessTokenRealmProxy.class },
        value = Parcel.Serialization.BEAN,
        analyze = { AccessToken.class })
public class AccessToken extends RealmObject {

    @PrimaryKey
    public String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
