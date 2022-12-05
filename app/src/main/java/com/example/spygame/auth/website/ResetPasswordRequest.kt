package com.example.spygame.auth.website

import androidx.core.util.Consumer
import com.example.spygame.util.ThreadCreator
import org.json.JSONObject

class ResetPasswordRequest(private val email: String) : SpyGameHttpRequest {

    private object Constants {
        const val PATH: String = "reset/request"
    }

    override fun createHttpRequest(objectConsumer: Consumer<JSONObject?>) {
        var jsonObject: JSONObject? = null;
        ThreadCreator.createThreadWithCallback({
            jsonObject = makeRequest(Constants.PATH, getParameters(), this::makePostRequest)
        }, {
            objectConsumer.accept(jsonObject)
        })
    }

    private fun getParameters(): Map<String, String> {
        val parameters = HashMap<String, String>()

        parameters["email"] = email

        return parameters
    }

}