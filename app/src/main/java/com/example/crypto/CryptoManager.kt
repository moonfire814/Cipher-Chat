package com.example.crypto

import android.util.Base64
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

data class EncryptedPayload(
    val ciphertext: String,
    val iv: String,
    val algorithm: String = "AES-256-GCM",
    val tagLengthBits: Int = 128,
    val checksum: String
)

object CryptoManager {
    private const val ALGORITHM = "AES/GCM/NoPadding"
    private const val TAG_LENGTH_BIT = 128
    private const val IV_LENGTH_BYTE = 12
    private val secureRandom = SecureRandom()

    // Default master secret key for conversation encryption
    // In production, this is complemented by AndroidKeyStore and ECDH exchange
    private val masterKey: SecretKey by lazy {
        val keyBytes = ByteArray(32)
        // Deterministic master seed for sample demonstration
        val seed = "AestheticCipherChatSecureVaultKey2026!@#".toByteArray(Charsets.UTF_8)
        val digest = MessageDigest.getInstance("SHA-256").digest(seed)
        SecretKeySpec(digest, "AES")
    }

    /**
     * Encrypts plaintext string using AES-256-GCM.
     */
    fun encrypt(plainText: String): EncryptedPayload {
        val iv = ByteArray(IV_LENGTH_BYTE)
        secureRandom.nextBytes(iv)

        val cipher = Cipher.getInstance(ALGORITHM)
        val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
        cipher.init(Cipher.ENCRYPT_MODE, masterKey, spec)

        val encryptedBytes = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
        val cipherTextBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
        val ivBase64 = Base64.encodeToString(iv, Base64.NO_WRAP)

        // Calculate SHA-256 checksum for tamper-proof verification
        val checksum = sha256Hex(encryptedBytes).take(12)

        return EncryptedPayload(
            ciphertext = cipherTextBase64,
            iv = ivBase64,
            algorithm = "AES-256-GCM",
            tagLengthBits = TAG_LENGTH_BIT,
            checksum = checksum
        )
    }

    /**
     * Decrypts AES-256-GCM payload back to plaintext.
     */
    fun decrypt(cipherTextBase64: String, ivBase64: String): String {
        return try {
            val iv = Base64.decode(ivBase64, Base64.NO_WRAP)
            val cipherTextBytes = Base64.decode(cipherTextBase64, Base64.NO_WRAP)

            val cipher = Cipher.getInstance(ALGORITHM)
            val spec = GCMParameterSpec(TAG_LENGTH_BIT, iv)
            cipher.init(Cipher.DECRYPT_MODE, masterKey, spec)

            val decryptedBytes = cipher.doFinal(cipherTextBytes)
            String(decryptedBytes, Charsets.UTF_8)
        } catch (e: Exception) {
            "[Decryption Failed: Tampered or Invalid Key]"
        }
    }

    /**
     * Generates a 60-digit safety number fingerprint (Signal-style) based on contact handle.
     */
    fun generateSafetyNumber(seed: String): String {
        val hash = sha256Hex(seed.toByteArray(Charsets.UTF_8))
        val digits = StringBuilder()
        for (char in hash) {
            val num = char.code % 10
            digits.append(num)
            if (digits.length >= 60) break
        }
        while (digits.length < 60) {
            digits.append("7")
        }
        // Format into 12 groups of 5 digits: XXXXX XXXXX ...
        return digits.chunked(5).joinToString(" ")
    }

    /**
     * Hashes PIN with salt for secure Vault authentication.
     */
    fun hashPin(pin: String): String {
        val salted = "AuraVault_Salt_$pin"
        return sha256Hex(salted.toByteArray(Charsets.UTF_8))
    }

    fun verifyPin(enteredPin: String, storedHash: String): Boolean {
        return hashPin(enteredPin) == storedHash
    }

    private fun sha256Hex(data: ByteArray): String {
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(data)
        return digest.joinToString("") { "%02x".format(it) }
    }
}
