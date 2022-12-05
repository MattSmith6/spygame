package com.example.spygame.auth

import com.github.glusk.caesar.hashing.ImmutableMessageDigest
import java.nio.ByteOrder
import java.security.MessageDigest
import java.security.SecureRandom

object AuthenticationConstants {

    val IMD: ImmutableMessageDigest = ImmutableMessageDigest(
        MessageDigest.getInstance("SHA-256"))

    val RNG: SecureRandom = SecureRandom()

    val BYTE_ORDER: ByteOrder = ByteOrder.BIG_ENDIAN

}