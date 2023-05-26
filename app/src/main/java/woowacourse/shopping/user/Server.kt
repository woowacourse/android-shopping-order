package woowacourse.shopping.user

enum class Server(val url: String, val token: String) {
    Deetoo(
        "http://ec2-15-164-212-191.ap-northeast-2.compute.amazonaws.com:8080/",
        "YUBhLmNvbToxMjM0",
    ),
    Roise("http://ec2-13-209-67-35.ap-northeast-2.compute.amazonaws.com:8080/", "YUBhLmNvbToxMjM0"),
    Emil("http://ec2-3-35-209-252.ap-northeast-2.compute.amazonaws.com:8080/", "YUBhLmNvbToxMjM0"),
}
