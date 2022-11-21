package com.example.spygame.auth

import org.json.JSONObject

import org.json.JSONTokener
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

// Pull from Java server side code and convert to Kotlin, remove references to player id
class PlayerEncryptionKey {

    private object CipherConstants {
        const val CIPHER_TYPE = "AES";
    }

    private var secretKey: SecretKey? = null
    private var cipher: Cipher? = null

    fun initialize(premasterSecret: ByteArray) {
        secretKey = SecretKeySpec(premasterSecret, CipherConstants.CIPHER_TYPE)
        var cipher: Cipher? = null
        try {
            cipher = Cipher.getInstance(CipherConstants.CIPHER_TYPE)
        } catch (ex: NoSuchPaddingException) {
            ex.printStackTrace()
        } catch (ex: NoSuchAlgorithmException) {
            ex.printStackTrace()
        }
        this.cipher = cipher
    }

    fun isInitialized(): Boolean {
        return secretKey != null
    }

    fun encryptJSONObject(jsonObject: JSONObject): String? {
        check(isInitialized()) { "Cannot encrypt without an initialized encryption key." }
        try {
            cipher?.init(Cipher.ENCRYPT_MODE, secretKey)
            val objectBytesToEncrypt = jsonObject.toString().toByteArray()
            val encryptedBytes: ByteArray? = cipher?.doFinal(objectBytesToEncrypt)
            return String(encryptedBytes!!)
        } catch (ex: IllegalBlockSizeException) {
            ex.printStackTrace()
        } catch (ex: BadPaddingException) {
            ex.printStackTrace()
        } catch (ex: InvalidKeyException) {
            ex.printStackTrace()
        }
        return null
    }

    fun decryptJSONObject(encryptedJSONObject: String): JSONObject? {
        check(isInitialized()) { "Cannot decrypt without an initialized encryption key." }
        try {
            cipher?.init(Cipher.DECRYPT_MODE, secretKey)
            val encryptedBytes = encryptedJSONObject.toByteArray()
            val decryptedObjectBytes: ByteArray? = cipher?.doFinal(encryptedBytes)
            val stringJSONObject = String(decryptedObjectBytes!!)
            val jsonTokener = JSONTokener(stringJSONObject)
            return JSONObject(jsonTokener)
        } catch (ex: IllegalBlockSizeException) {
            ex.printStackTrace()
        } catch (ex: BadPaddingException) {
            ex.printStackTrace()
        } catch (ex: InvalidKeyException) {
            ex.printStackTrace()
        }
        return null
    }

}