package org.schulcloud.mobile.network

class FeathersResponse<T> {
    var total: Int? = null
    var limit: Int? = null
    var skip: Int? = null
    var data: T? = null
}