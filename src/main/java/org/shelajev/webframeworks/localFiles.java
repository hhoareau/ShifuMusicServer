package org.shelajev.webframeworks;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by u016272 on 02/07/2016.
 */
public class localFiles implements Serializable {

    private List<LocalFile> files=new ArrayList<LocalFile>();

    public localFiles() {
    }

    public localFiles(List<LocalFile> lf) {
        this.files=lf;
    }

    public List<LocalFile> getFiles() {
        return files;
    }

    public void setFiles(List<LocalFile> files) {
        this.files = files;
    }
}
