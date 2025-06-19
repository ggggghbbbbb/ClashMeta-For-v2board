package com.github.kr328.clash.service

import android.content.Context
import android.content.SharedPreferences
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject

class V2boardService(private val context: Context) {
    private val client = OkHttpClient()
    private val sp: SharedPreferences = context.getSharedPreferences("v2board", Context.MODE_PRIVATE)
    private var currentDomain: String? = null

    fun getSavedToken(): String? = sp.getString("token", null)
    fun saveToken(token: String) = sp.edit().putString("token", token).apply()
    fun clearToken() = sp.edit().remove("token").apply()

    suspend fun getAvailableDomain(): String? {
        val request = Request.Builder()
            .url("https://url.o808o.com/config.json")
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val json = JSONArray(response.body?.string() ?: "")
                    for (i in 0 until json.length()) {
                        val domain = json.getJSONObject(i).getString("url")
                        if (isAvailable(domain)) {
                            currentDomain = domain
                            return domain
                        }
                    }
                }
                null
            }
        } catch (_: Exception) { null }
    }
    private fun isAvailable(domain: String): Boolean {
        return try {
            val request = Request.Builder()
                .url("$domain/api/v1/guest/status")
                .build()
            client.newCall(request).execute().use { response ->
                response.isSuccessful
            }
        } catch (_: Exception) { false }
    }
    suspend fun login(email: String, password: String): String? {
        val domain = currentDomain ?: getAvailableDomain() ?: return null
        val body = JSONObject().apply {
            put("email", email)
            put("password", password)
        }
        val request = Request.Builder()
            .url("$domain/api/v1/passport/auth/login")
            .post(RequestBody.create(MediaType.parse("application/json"), body.toString()))
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val obj = JSONObject(response.body?.string() ?: "")
                    val token = obj.optJSONObject("data")?.optString("token")
                    if (!token.isNullOrEmpty()) {
                        saveToken(token)
                        token
                    } else null
                } else null
            }
        } catch (_: Exception) { null }
    }
    suspend fun getUserInfo(token: String): JSONObject? {
        val domain = currentDomain ?: getAvailableDomain() ?: return null
        val request = Request.Builder()
            .url("$domain/api/v1/user/info")
            .header("Authorization", "Bearer $token")
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    JSONObject(response.body?.string() ?: "")
                } else null
            }
        } catch (_: Exception) { null }
    }
    suspend fun getSubscribeUrl(token: String): String? {
        val domain = currentDomain ?: getAvailableDomain() ?: return null
        val request = Request.Builder()
            .url("$domain/api/v1/user/getSubscribe")
            .header("Authorization", "Bearer $token")
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    JSONObject(response.body?.string() ?: "").optString("data", null)
                } else null
            }
        } catch (_: Exception) { null }
    }
    suspend fun getAnnouncement(): String? {
        val domain = currentDomain ?: getAvailableDomain() ?: return null
        val request = Request.Builder()
            .url("$domain/api/v1/announcement?per_page=1")
            .build()
        return try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    val arr = JSONObject(response.body?.string() ?: "").optJSONArray("data")
                    if (arr != null && arr.length() > 0) {
                        arr.getJSONObject(0).optString("content")
                    } else null
                } else null
            }
        } catch (_: Exception) { null }
    }
}
