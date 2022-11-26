package com.example.spygame.auth

import com.example.spygame.auth.AuthenticationConstants.BYTE_ORDER
import com.example.spygame.auth.AuthenticationConstants.IMD
import com.example.spygame.auth.AuthenticationConstants.RNG
import com.github.glusk.caesar.Bytes
import com.github.glusk.caesar.PlainText
import com.github.glusk.srp6_variables.*
import org.json.JSONObject
import java.nio.ByteOrder


class PlayerAuthenticationHandshake(username: String, password: String) {

    private val I: Bytes = PlainText(username)
    private val P: Bytes = PlainText(password)

    private lateinit var N: SRP6IntegerVariable
    private lateinit var g: SRP6IntegerVariable
    private lateinit var s: Bytes
    private lateinit var B: SRP6IntegerVariable

    private lateinit var A: SRP6IntegerVariable
    private lateinit var S: SRP6IntegerVariable
    private lateinit var K: Bytes
    private lateinit var M1: Bytes

    fun getPlayerHelloObject(): JSONObject {
        val helloPacket: JSONObject = JSONObject()
        helloPacket.put("I", I)

        return helloPacket
    }

    fun getKeyExchangeObject(helloObject: JSONObject): JSONObject {
        val responseObject = JSONObject()

        N = getIntegerVariableFromJSON(helloObject, "N")
        g = getIntegerVariableFromJSON(helloObject, "g")
        s = getBytesFromJSON(helloObject, "s")
        B = getIntegerVariableFromJSON(helloObject, "B")

        try {
            val x = SRP6PrivateKey(IMD, s, I, P, BYTE_ORDER)
            val a = SRP6RandomEphemeral(RNG, -1, N)
            A = SRP6ClientPublicKey(N, g, a)
            val u = SRP6ScramblingParameter(IMD, A, B, N, BYTE_ORDER)
            val k = SRP6Multiplier(IMD, N, g, BYTE_ORDER);
            S = SRP6ClientSharedSecret(N, g, k, B, x, u, a)
            K = SRP6SessionKey(IMD, S, BYTE_ORDER)
            M1 = SRP6ClientSessionProof(IMD, N, g, I, s, A, B, K, BYTE_ORDER)

            responseObject.put("A", encodeToBase64(A))
            responseObject.put("M1", encodeToBase64(M1))
        } catch (e: SRP6Exception) {
            responseObject.put("error", "Bad handshake")
        } catch (e: IllegalStateException) {
            responseObject.put("error", e.message)
        }

        return responseObject
    }

    fun isServerProofValid(proofObject: JSONObject): Boolean {
        val M2 = getBytesFromJSON(proofObject, "M2")
        val cM2 = SRP6ServerSessionProof(IMD, N, A, M1, K, BYTE_ORDER)

        return M2.equals(cM2)
    }

    fun getSharedSecret(): ByteArray {
        return S.bytes(BYTE_ORDER).asArray()
    }

    private fun encodeToBase64(integerVariable: SRP6IntegerVariable): String {
        return encodeToBase64(integerVariable.bytes(BYTE_ORDER))
    }

    private fun encodeToBase64(bytes: Bytes): String {
        return android.util.Base64.encodeToString(bytes.asArray(), getBase64Flag())
    }

    private fun getBytesFromJSON(jsonObject: JSONObject, path: String): Bytes {
        if (!jsonObject.has(path)) {
            throw IllegalStateException("Bad handshake, $path is null")
        }

        val encodedString = jsonObject.getString(path)
        val byteArray = android.util.Base64.decode(encodedString, getBase64Flag())
        return Bytes.wrapped(*byteArray)
    }

    private fun getBase64Flag(): Int {
        return android.util.Base64.DEFAULT
    }

    private fun getIntegerVariableFromJSON(
        jsonObject: JSONObject,
        path: String
    ): SRP6IntegerVariable {
        return SRP6CustomIntegerVariable(getBytesFromJSON(jsonObject, path), ByteOrder.BIG_ENDIAN)
    }

}