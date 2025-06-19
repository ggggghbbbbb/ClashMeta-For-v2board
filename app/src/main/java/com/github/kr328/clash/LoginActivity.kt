// 增加获取订阅链接和用户信息的接口
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
    } catch (e: Exception) {
        null
    }
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
    } catch (e: Exception) {
        null
    }
}
