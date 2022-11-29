package com.example.spygame.auth.website

import androidx.core.util.Consumer
import org.apache.hc.core5.http.HttpEntity

class RegisterAccountRequest(private val email: String, private val username: String,
                             private val password: String) : SpyGameHttpRequest {

    private object Constants {
        const val PATH = "register"
    }

    override fun createHttpRequest(entityConsumer: Consumer<HttpEntity>) {
        makeRequest(Constants.PATH, createParameters(), this::makePostRequest, entityConsumer)
    }

    private fun createParameters(): Map<String, String> {
        val parameterMap = HashMap<String, String>();

        parameterMap["email"] = email;
        parameterMap["username"] = username;
        parameterMap["password"] = password;

        return parameterMap;
    }

}