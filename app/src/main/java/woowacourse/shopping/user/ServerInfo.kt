package woowacourse.shopping.user

object ServerInfo {
    private lateinit var server: Server

    val url: String
        get() = server.url

    val token: String
        get() = server.token

    fun changeServer(server: Server) {
        this.server = server
    }
}
