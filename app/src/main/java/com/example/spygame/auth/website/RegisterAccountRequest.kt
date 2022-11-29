package com.example.spygame.auth.website

import androidx.core.util.Consumer
import com.example.spygame.util.ThreadCreator
import org.apache.hc.core5.http.HttpEntity
import org.json.JSONObject

class RegisterAccountRequest(private val email: String, private val username: String,
                             private val password: String) : SpyGameHttpRequest {

    private object Constants {
        const val PATH = "register"
    }

    override fun createHttpRequest(objectConsumer: Consumer<JSONObject?>) {
        var jsonObject: JSONObject? = null;
        ThreadCreator.createThreadWithCallback({
            jsonObject = makeRequest(Constants.PATH, createParameters(), this::makePostRequest)
        }, {
            objectConsumer.accept(jsonObject)
        })
    }

    private fun createParameters(): Map<String, String> {
        val parameterMap = HashMap<String, String>();

        parameterMap["email"] = email;
        parameterMap["username"] = username;
        parameterMap["password"] = password;

        return parameterMap;
    }

}