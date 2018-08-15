package org.schulcloud.mobile.models.content

class GeogebraResponse {
    var responses: Responses? = null

    class Responses {
        var response: Response? = null

        class Response {
            var item: Item? = null

            class Item {
                var previewUrl: String? = null
            }
        }
    }
}
