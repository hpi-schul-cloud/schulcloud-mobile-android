package org.schulcloud.mobile.data.model.responseBodies;


import com.google.gson.annotations.SerializedName;

public class SignedUrlResponse {
    public String url;
    public SignedUrlResponseHeader header;


    private class SignedUrlResponseHeader {
        @SerializedName("Content-Type")
        public String contentType;

        @SerializedName("x-amz-meta-path")
        public String metaPath;

        @SerializedName("x-amz-meta-name")
        public String metaName;

        @SerializedName("x-amz-meta-thumbnail")
        public String metaThumbnail;

    }
}
