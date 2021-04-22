package com.unfuwa.ngservice.extendedmodel;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.loader.content.CursorLoader;

import java.io.File;

public class Photo {

    private Context context;

    private Uri uri;
    private File file;
    private String name;
    private long size;

    public Photo(Context context, Uri uri) {
        this.context = context;
        this.uri = uri;
        this.file = new File(uri.getPath());
        this.name = file.getAbsoluteFile().getName();
        this.size = file.length() / 1024 / 1024;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}
