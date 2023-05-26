package woowacourse.shopping.data.model

enum class Server(val url: String) {
    BASE_URL_TORI("http://13.209.68.194:8080"),
    BASE_URL_JENNA("http://3.34.126.146:8080"),
    BASE_URL_POI("http://3.39.194.150:8080"),
    ;

    companion object {
        const val TOKEN_KRRONG = "a2FuZ3NqOTY2NUBnbWFpbC5jb206MTIzNA=="
        const val TOKEN_SUNNY = "eWlzMDkyNTIxQGdtYWlsLmNvbToxMjM0"
    }
}
