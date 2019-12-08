package com.example.facepen;

public class findfriends {
    public String fullname, profilestatus, profileimage;


    public findfriends() {
    }

    public findfriends(String fullname, String profilestatus, String profileimage) {
        this.fullname = fullname;
        this.profilestatus = profilestatus;
        this.profileimage = profileimage;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfilestatus() {
        return profilestatus;
    }

    public void setProfilestatus(String profilestatus) {
        this.profilestatus = profilestatus;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }
}
