package woowacourse.shopping.data.server

enum class Server(private val url: String) {
    JERRY("https://woowa.store"),
    GITJJANG("https://gitchan.shop"),
    HOI("http://hoyzzang.shop");

    companion object {
        fun getUrl(server: String): String {
            return values().find { it.name == server }?.url ?: ""
        }
    }
}
