package com.example.craigblackburn.foostats;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Index;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;


@Entity
public class FBUser {

    @Id
    @Index(unique = true)
    private Long facebookId;
    @Property(nameInDb = "access_token")
    private String accessToken;
    @Property(nameInDb = "email")
    private String email;

    public FBUser() {}

    public FBUser(String id, String token, String email) {
        this.facebookId = Long.valueOf(id);
        this.accessToken = token;
        this.email = email;
    }

    @Generated(hash = 1276802293)
    public FBUser(Long facebookId, String accessToken, String email) {
        this.facebookId = facebookId;
        this.accessToken = accessToken;
        this.email = email;
    }

    public Long getId() {
        return this.facebookId;
    }

    public String getFacebookId() {
        return String.valueOf(facebookId);
    }

    public String getAccessToken() {
        return this.accessToken;
    }

    public String getEmail() {
        return this.email;
    }

    public void setFacebookId(String id) {
        this.facebookId = Long.valueOf(id);
    }

    public void setAccessToken(String token) {
        this.accessToken = token;
    }

    public void setEmail(String name) {
        this.email = name;
    }

    public String toString() {
        return "Email: " + this.email
                + "\nAccess Token: " + this.accessToken
                + "\nFacebook ID: " + this.facebookId;
    }

    public void setFacebookId(Long facebookId) {
        this.facebookId = facebookId;
    }

}
