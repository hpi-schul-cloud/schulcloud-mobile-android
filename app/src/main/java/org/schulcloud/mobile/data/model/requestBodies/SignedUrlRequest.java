package org.schulcloud.mobile.data.model.requestBodies;


public class SignedUrlRequest {
    public String action;
    public String path;
    public String fileType;

    public SignedUrlRequest(String action, String path, String fileType) {
        this.action = action;
        this.path = path;
        this.fileType = fileType;
    }
}
