package woowacourse.shopping.server

enum class Server(private val url: String) {
    JERRY("https://woowa.store"),
    GITJJANG("https://gitchan.shop"),
    HOI("");

    companion object {
        fun getUrl(server: String): String {
            return values().find { it.name == server }?.url ?: ""
        }
    }
}
