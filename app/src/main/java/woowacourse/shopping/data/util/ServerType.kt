package woowacourse.shopping.data.util

enum class ServerType(val url: String) {
    SUBAN_MOCK_SERVER("http://localhost"),
    MINT_SERVER("http://43.201.65.203"),
    JOY_SERVER("http://54.180.88.58");

    companion object {
        const val INTENT_KEY = "server_key"
    }
}
