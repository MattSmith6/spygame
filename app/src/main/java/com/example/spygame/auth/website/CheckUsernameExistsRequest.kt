package com.example.spygame.auth.website

import androidx.core.util.Consumer
import com.example.spygame.util.ThreadCreator
import org.apache.hc.core5.http.HttpEntity
import org.json.JSONObject

class CheckUsernameExistsRequest(private val username: String) : SpyGameHttpRequest {

    private object Constants {
        const val PATH: String = "username/check/%s";
    }

    override fun createHttpRequest(objectConsumer: Consumer<JSONObject?>) {
        var jsonObject: JSONObject? = null;
        ThreadCreator.createThreadWithCallback({
            jsonObject = makeRequest(String.format(Constants.PATH, username), null, this::makeGetRequest)
        }, {
            objectConsumer.accept(jsonObject)
        })
    }

}