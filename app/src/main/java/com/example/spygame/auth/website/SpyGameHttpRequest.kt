package com.example.spygame.auth.website

import androidx.core.util.Consumer
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.classic.methods.HttpPost
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.core5.http.ClassicHttpRequest
import org.apache.hc.core5.http.HttpEntity
import org.apache.hc.core5.http.NameValuePair
import org.apache.hc.core5.http.io.entity.EntityUtils
import org.apache.hc.core5.http.message.BasicNameValuePair
import org.apache.hc.core5.net.URIBuilder
import org.json.JSONObject
import org.json.JSONTokener
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URI
import kotlin.reflect.KFunction2

interface SpyGameHttpRequest {

    object Constants {
        const val HOST_NAME: String = "http://137.184.180.66:80/account/";
    }

    fun createHttpRequest(entityConsumer: Consumer<HttpEntity>)

    fun makeRequest(
        path: String, params: Map<String, String>?,
        requestFunction: KFunction2<String, Map<String, String>?, ClassicHttpRequest>,
        entityConsumer: Consumer<HttpEntity>
    ) {
        try {
            HttpClients.createDefault().use { httpClient ->
                val httpRequest = requestFunction.call(path, params)
                httpClient.execute(httpRequest).use { response ->
                    val httpEntity = response.entity
                    entityConsumer.accept(httpEntity)
                    EntityUtils.consume(httpEntity)
                }
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
        }
    }

    fun getJSONObjectConsumer(jsonConsumer: Consumer<JSONObject?>): Consumer<HttpEntity> {
        return Consumer {
            httpEntity ->
            var jsonObject: JSONObject? = null
            try {
                BufferedReader(InputStreamReader(httpEntity.getContent())).use { bufferedReader ->
                    val jsonStringBuilder = StringBuilder()

                    var currentLine: String? = bufferedReader.readLine()
                    while (currentLine != null) {
                        jsonStringBuilder.append(currentLine)
                        currentLine = bufferedReader.readLine()
                    }

                    val stringifiedJSON = jsonStringBuilder.toString()
                    val jsonTokener = JSONTokener(stringifiedJSON)
                    jsonObject = JSONObject(jsonTokener)
                }
            } catch (ex: IOException) {
                ex.printStackTrace()
            }

            jsonConsumer.accept(jsonObject)
        }
    }

    fun makeGetRequest(path: String, keyValuePairs: Map<String, String>?): HttpGet {
        val httpGet = HttpGet(getUrlFromPath(path))

        if (keyValuePairs == null) {
            return httpGet
        }

        val uri: URI = URIBuilder(httpGet.uri).addParameters(getNameValuePairList(keyValuePairs)).build()
        httpGet.uri = uri
        return httpGet
    }

    fun makePostRequest(path: String, keyValuePairs: Map<String, String>?): HttpPost {
        val postRequest = HttpPost(getUrlFromPath(path))

        if (keyValuePairs == null) {
            return postRequest
        }

        postRequest.entity = getEntity(keyValuePairs)
        return postRequest
    }

    fun getUrlFromPath(path: String): String? {
        return Constants.HOST_NAME + path
    }

    fun getEntity(keyValuePairs: Map<String, String>): HttpEntity {
        return UrlEncodedFormEntity(getNameValuePairList(keyValuePairs))
    }

    fun getNameValuePairList(keyValuePairs: Map<String, String>): List<NameValuePair> {
        val nameValuePairs: MutableList<NameValuePair> = ArrayList(keyValuePairs.size)
        for ((key, value) in keyValuePairs.entries) {
            nameValuePairs.add(BasicNameValuePair(key, value))
        }
        return nameValuePairs
    }

}