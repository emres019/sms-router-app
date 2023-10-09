package com.github.emresarincioglu.smsrouter.core.utility

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object CryptoManager {

    private const val ANDROID_KEY_STORE = "AndroidKeyStore"
    private const val ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
    private const val BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
    private const val PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
    private const val TRANSFORMATION = "$ALGORITHM/$BLOCK_MODE/$PADDING"

    private val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply {
        load(null)
    }

    /**
     * @return First element is the initialization vector, second element is the encrypted and
     * encoded data.
     */
    fun encrypt(keyAlias: String, data: String): Pair<ByteArray, String> {

        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.ENCRYPT_MODE, getSecretKey(keyAlias))
        }

        val encryptedData = cipher.doFinal(data.encodeToByteArray())
        return cipher.iv to Base64.encodeToString(encryptedData, Base64.DEFAULT)
    }

    fun decrypt(keyAlias: String, iv: ByteArray, data: String): String {

        val cipher = Cipher.getInstance(TRANSFORMATION).apply {
            init(Cipher.DECRYPT_MODE, getSecretKey(keyAlias), GCMParameterSpec(128, iv))
        }

        val decodedData = Base64.decode(data, Base64.DEFAULT)
        val decryptedData = cipher.doFinal(decodedData)
        return String(decryptedData, Charsets.UTF_8)
    }

    fun deleteKey(keyAlias: String) = keyStore.deleteEntry(keyAlias)

    /**
     * Gets the secret (private) key from android key store for encryption/decryption.
     *
     * If the key does not exist, launches [createSecretKey] to create and store a new key.
     */
    private fun getSecretKey(keyAlias: String): SecretKey {

        val keyEntry = keyStore.getEntry(keyAlias, null) as? KeyStore.SecretKeyEntry
        return keyEntry?.secretKey ?: createSecretKey(keyAlias)
    }

    /**
     * Creates and stores a secret (private) key in android key store for encryption/decryption.
     */
    private fun createSecretKey(keyAlias: String): SecretKey {

        val keyGenSpec = KeyGenParameterSpec.Builder(
            keyAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ).setBlockModes(BLOCK_MODE)
            .setEncryptionPaddings(PADDING)
            .setKeySize(256)
            .setUserAuthenticationRequired(false)
            .setRandomizedEncryptionRequired(true)
            .build()

        return KeyGenerator.getInstance(ALGORITHM, ANDROID_KEY_STORE).apply {
            init(keyGenSpec)
        }.generateKey()
    }
}