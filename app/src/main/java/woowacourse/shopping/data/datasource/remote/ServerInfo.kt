package woowacourse.shopping.data.datasource.remote

import woowacourse.shopping.data.datasource.local.product.ProductCacheImpl

object ServerInfo {

    private lateinit var _currentBaseUrl: String
    val currentBaseUrl get() = _currentBaseUrl

    private lateinit var _serverName: String
    val serverName get() = _serverName

    private val baseUrlMap: Map<String, String> = mapOf(
        "modi" to "http://54.180.88.191:8080",
        "onekong" to "http://3.39.177.53:8080",
        "jamie" to "http://52.79.242.229:8080"
    )

    fun setBaseUrl(serverName: String) {
        _currentBaseUrl = baseUrlMap[serverName] ?: ""
        _serverName = serverName
        ProductCacheImpl.clear()
    }
}
