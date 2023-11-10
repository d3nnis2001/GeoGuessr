package com.gse23.dschielke;

public class ImageInfo {
    private final String fileName;
    private String width;
    private String length;
    private String desc;

    public ImageInfo(String fileName, String width, String length, String desc) {
        this.fileName = fileName;
        this.width = width;
        this.length = length;
        this.desc = desc;
    }

    public String getFileName() {
        return fileName;
    }

    public String getWidth() {
        return width;
    }

    public String getLength() {
        return length;
    }

    public String getDesc() {
        return desc;
    }
}
