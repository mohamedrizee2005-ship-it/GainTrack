package com.gaintrack.app.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.StandardCharsets
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * App-private Gemini settings. The API key is encrypted with a non-exportable
 * Android Keystore key. A fresh key is required after uninstall/reinstall.
 *
 * Never send keys to telemetry, logs, analytics, or a repository.
 */
object SecureGeminiSettings {
    private const val ALIAS = "gaintrack_gemini_local_aes_v1"
    private const val FILE = "gaintrack_gemini_secret"
    private const val VALUE = "encrypted_key"
    private const val MODEL = "gemini_model"
    const val FREE_MODEL = "gemini-3.5-flash-lite"
    const val QUALITY_MODEL = "gemini-3.8-flash"

    private fun prefs(context: Context) =
        context.getSharedPreferences(FILE, Context.MODE_PRIVATE)

    private fun secretKey(): SecretKey {
        val store = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (store.getKey(ALIAS, null) as? SecretKey)?.let { return it }
        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }

    fun saveKey(context: Context, key: String) {
        if (key.isBlank()) {
            clearKey(context)
            return
        }
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey())
        val encrypted = cipher.doFinal(key.trim().toByteArray(StandardCharsets.UTF_8))
        val combined = cipher.iv + encrypted
        prefs(context).edit()
            .putString(VALUE, Base64.encodeToString(combined, Base64.NO_WRAP))
            .apply()
    }

    fun loadKey(context: Context): String {
        val encoded = prefs(context).getString(VALUE, null) ?: return ""
        return try {
            val bytes = Base64.decode(encoded, Base64.DEFAULT)
            require(bytes.size > 28) { "Invalid encrypted key data" }
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(
                Cipher.DECRYPT_MODE,
                secretKey(),
                GCMParameterSpec(128, bytes.copyOfRange(0, 12))
            )
            String(cipher.doFinal(bytes.copyOfRange(12, bytes.size)), StandardCharsets.UTF_8)
        } catch (_: Exception) {
            // Keystore keys do not survive uninstall or all device migrations.
            clearKey(context)
            ""
        }
    }

    fun clearKey(context: Context) {
        prefs(context).edit().remove(VALUE).apply()
    }

    fun model(context: Context): String {
        val saved = prefs(context).getString(MODEL, FREE_MODEL)
        return if (saved == QUALITY_MODEL) QUALITY_MODEL else FREE_MODEL
    }

    fun saveModel(context: Context, model: String) {
        prefs(context).edit().putString(
            MODEL,
            if (model == QUALITY_MODEL) QUALITY_MODEL else FREE_MODEL
        ).apply()
    }
}
