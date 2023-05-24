package woowacourse.shopping.data.service

object ServerInfo {

    private lateinit var _currentBaseUrl: String
    val currentBaseUrl get() = _currentBaseUrl

    private val baseUrlMap: Map<String, String> = mapOf(
        "모디" to "http://54.180.88.191:8080"
    )

    fun setBaseUrl(serverName: String) {
        _currentBaseUrl = baseUrlMap[serverName] ?: ""
    }
}
