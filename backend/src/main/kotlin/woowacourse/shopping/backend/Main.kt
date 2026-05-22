package woowacourse.shopping.backend

fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 8080
    val server = ShoppingBackendServer(port = port)
    server.start()
    println("Shopping backend started at ${server.baseUrl}")
}
