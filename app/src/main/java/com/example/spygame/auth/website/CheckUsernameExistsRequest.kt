package com.example.spygame.auth.website

import androidx.core.util.Consumer
import org.apache.hc.core5.http.HttpEntity

class CheckUsernameExistsRequest(private val username: String) : SpyGameHttpRequest {

    private object Constants {
        const val PATH: String = "username/check/%s";
    }

    override fun createHttpRequest(entityConsumer: Consumer<HttpEntity>) {
        makeRequest(String.format(Constants.PATH, username), null, this::makeGetRequest,
        entityConsumer);
    }

}