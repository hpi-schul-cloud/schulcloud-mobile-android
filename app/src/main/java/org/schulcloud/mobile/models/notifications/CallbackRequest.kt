package org.schulcloud.mobile.models.notifications

class CallbackRequest(public var notificationId: String, public var type: String){
    companion object {
        val TYPE_RECIEVED = "recieved"
    }
}