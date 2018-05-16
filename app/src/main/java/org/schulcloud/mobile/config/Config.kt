package org.schulcloud.mobile.config

class Config {

    companion object {

        val HOST: String? = "Schul-Cloud"
        val API_URL: String = "https://schul-cloud.org:8080"

        val HEADER_AUTH = "Authorization"
        val HEADER_AUTH_VALUE_PREFIX = "Bearer "

        val REALM_SCHEMA_VERSION = 1L
    }
}
