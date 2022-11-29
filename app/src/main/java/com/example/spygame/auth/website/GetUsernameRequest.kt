package com.example.spygame.auth.website

import androidx.core.util.Consumer
import org.apache.hc.core5.http.HttpEntity

class GetUsernameRequest(private val email: String) : SpyGameHttpRequest {

    private object Constants {
        const val PATH: String = "username/request"
    }

    override fun createHttpRequest(entityConsumer: Consumer<HttpEntity>) {
        makeRequest(Constants.PATH, getParameters(), this::makePostRequest, entityConsumer)
    }

    private fun getParameters(): Map<String, String> {
        val parameters = HashMap<String, String>()

        parameters["email"] = email

        return parameters
    }

}