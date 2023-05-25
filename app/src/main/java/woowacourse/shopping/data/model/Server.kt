package woowacourse.shopping.data.model

enum class Server(val url: String) {
    BASE_URL_TORI("http://13.209.68.194:8080"),
    BASE_URL_JENNA("http://3.34.178.40:8080"),
    BASE_URL_POI("http://3.39.194.150:8080"),
    ;

    companion object {
        const val TOKEN = "a2FuZ3NqOTY2NUBnbWFpbC5jb206MTIzNA=="
    }
}
