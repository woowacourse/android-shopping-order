package woowacourse.shopping.data.model

sealed class Server {
    enum class Url(val value: String) {
        BASE_URL_TORI("http://13.209.68.194:8080"),
        BASE_URL_JENNA("http://3.34.126.146:8080"),
        BASE_URL_POI("http://3.39.194.150:8080"),
    }
}
