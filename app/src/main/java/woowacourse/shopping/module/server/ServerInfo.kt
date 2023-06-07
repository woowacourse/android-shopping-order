package woowacourse.shopping.module.server

object ServerInfo {
    private lateinit var server: Server

    val serverName: String
        get() = server.name

    val url: String
        get() = server.url

    fun changeServer(server: Server) {
        ServerInfo.server = server
    }
}
