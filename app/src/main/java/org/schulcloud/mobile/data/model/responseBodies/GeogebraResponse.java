package org.schulcloud.mobile.data.model.responseBodies;

/**
 * Date: 2/18/2018
 */
public class GeogebraResponse {
    public Responses responses;

    public static class Responses {
        public Response response;

        public static class Response {
            public Item item;

            public static class Item {
                public String previewUrl;
            }
        }
    }
}
