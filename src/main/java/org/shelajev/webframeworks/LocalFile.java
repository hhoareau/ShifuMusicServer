package org.shelajev.webframeworks;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v2_2;

import java.io.File;
import java.io.IOException;
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
    private String path="";

    public LocalFile() {
    }

    public LocalFile(File f, String computer,String user) {
        try {
            ID3v1 tags = new MP3File(f).getID3v1Tag();
            this.artist=tags.getLeadArtist();
            this.title=tags.getTitle();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TagException e) {
            e.printStackTrace();
        }

        if(this.title.length()==0 || this.getArtist().length()==0){
            String s=f.getName().replace(".mp3","").replace(".ogg","").replace(".flac","").toLowerCase();
            this.artist=s.split("-")[0].trim();
            if(s.indexOf("-")>-1)this.title=s.split("-")[1].trim();
        }

        this.text= Base64.encode(f.getName().getBytes());
        this.path=f.getAbsolutePath();

        for(int i=0;i<10;i++)this.path=this.path.replace("/","_").replace("\\","_");
        this.computer=computer;
        this.user=user;
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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
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
