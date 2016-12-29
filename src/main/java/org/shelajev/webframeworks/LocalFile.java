package org.shelajev.webframeworks;

import java.io.Serializable;

/**
 * Created by u016272 on 28/06/2016.
 */

public class LocalFile implements Serializable  {

    private String text="";
    private Integer origin=0;
    private Integer index=0;
    private String artist="";
    private String title="";
    private String computer="";
    private String user="";
    private Boolean online=true;

    public LocalFile() {
    }

    public LocalFile(String s,String computer) {
        this.artist=s.split("-")[0];
        if(s.indexOf("-")>-1)this.title=s.split("-")[1];
        this.text=s;
        this.computer=computer;
        this.user=computer;
        this.index=0;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComputer() {
        return computer;
    }

    public void setComputer(String computer) {
        this.computer = computer;
    }

    public String getText() {
        return text;
    }

    public Integer getOrigin() {
        return origin;
    }

    public void setOrigin(Integer origin) {
        this.origin = origin;
    }

    public void setText(String text) {
        this.text= text;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

  public String getUser() {
    return user;
  }

  public Boolean getOnline() {
    return online;
  }

  public void setOnline(Boolean online) {
    this.online = online;
  }

  public void setUser(String user) {
    this.user = user;
  }
}
