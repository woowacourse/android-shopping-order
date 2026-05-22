package woowacourse.shopping.backend

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.InetSocketAddress
import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

class ShoppingBackendServer(
    port: Int = 8080,
) {
    private val json =
        Json {
            encodeDefaults = true
            explicitNulls = false
        }

    private val store = InMemoryShoppingStore()
    private val server: HttpServer = HttpServer.create(InetSocketAddress(port), 0)

    val baseUrl: String
        get() = "http://localhost:${server.address.port}/"

    init {
        server.createContext("/") { exchange ->
            runCatching {
                handle(exchange)
            }.getOrElse { throwable ->
                if (throwable is IllegalArgumentException) {
                    exchange.respondJson(400, ErrorResponse(throwable.message ?: "잘못된 요청입니다."))
                    return@createContext
                }

                exchange.respondJson(500, ErrorResponse("서버 내부 오류가 발생했습니다."))
            }
        }
        server.executor = Executors.newCachedThreadPool()
    }

    fun start() {
        server.start()
    }

    fun stop(delaySeconds: Int = 0) {
        server.stop(delaySeconds)
    }

    private fun handle(exchange: HttpExchange) {
        when {
            exchange.requestMethod == "GET" && exchange.requestPath == "/" -> handleRoot(exchange)
            exchange.requestMethod == "GET" && exchange.requestPath == "/health" -> handleHealth(exchange)
            exchange.requestMethod == "GET" && exchange.requestPath == "/products" -> handleGetProducts(exchange)
            exchange.requestMethod == "GET" && exchange.requestPath.startsWith("/products/") -> handleGetProduct(exchange)
            exchange.requestMethod == "GET" && exchange.requestPath == "/cart-items" -> handleGetCartItems(exchange)
            exchange.requestMethod == "POST" && exchange.requestPath == "/cart-items" -> handleAddCartItem(exchange)
            exchange.requestMethod == "PATCH" && exchange.requestPath.startsWith("/cart-items/") -> handleUpdateCartItem(exchange)
            exchange.requestMethod == "DELETE" && exchange.requestPath.startsWith("/cart-items/") -> handleDeleteCartItem(exchange)
            exchange.requestMethod == "GET" && exchange.requestPath == "/cart-items/counts" -> handleGetCartItemCount(exchange)
            exchange.requestMethod == "POST" && exchange.requestPath == "/orders" -> handleCreateOrder(exchange)
            else -> exchange.respondJson(404, ErrorResponse("요청한 경로를 찾을 수 없습니다."))
        }
    }

    private fun handleRoot(exchange: HttpExchange) {
        exchange.respondJson(
            200,
            RootResponse(
                message = "Shopping backend is running.",
                endpoints =
                    listOf(
                        "GET /health",
                        "GET /products?page={page}&size={size}&category={category?}",
                        "GET /products/{id}",
                        "GET /cart-items?page={page}&size={size}",
                        "POST /cart-items",
                        "PATCH /cart-items/{id}",
                        "DELETE /cart-items/{id}",
                        "GET /cart-items/counts",
                        "POST /orders",
                    ),
            ),
        )
    }

    private fun handleHealth(exchange: HttpExchange) {
        exchange.respondJson(200, HealthResponse(status = "ok"))
    }

    private fun handleGetProducts(exchange: HttpExchange) {
        val page = exchange.queryParameter("page")?.toIntOrNull() ?: 0
        val size = exchange.queryParameter("size")?.toIntOrNull() ?: 20
        val category = exchange.queryParameter("category")
        val response = store.getProducts(page = page, size = size, category = category)
        exchange.respondJson(200, response)
    }

    private fun handleGetProduct(exchange: HttpExchange) {
        val productId = exchange.requestPath.substringAfterLast("/").toLongOrNull()
            ?: throw IllegalArgumentException("유효한 상품 ID가 필요합니다.")
        val product = store.getProduct(productId)
            ?: return exchange.respondJson(404, ErrorResponse("상품을 찾을 수 없습니다."))
        exchange.respondJson(200, product)
    }

    private fun handleGetCartItems(exchange: HttpExchange) {
        val page = exchange.queryParameter("page")?.toIntOrNull() ?: 0
        val size = exchange.queryParameter("size")?.toIntOrNull() ?: 20
        exchange.respondJson(200, store.getCartItems(page = page, size = size))
    }

    private fun handleAddCartItem(exchange: HttpExchange) {
        val request = exchange.readJson<CartItemRequest>()
        store.addCartItem(productId = request.productId, quantity = request.quantity)
        exchange.respondStatus(201)
    }

    private fun handleUpdateCartItem(exchange: HttpExchange) {
        val cartItemId = exchange.requestPath.substringAfterLast("/").toLongOrNull()
            ?: throw IllegalArgumentException("유효한 장바구니 항목 ID가 필요합니다.")
        val request = exchange.readJson<CartItemQuantityUpdateRequest>()
        val updated = store.updateCartItem(cartItemId = cartItemId, quantity = request.quantity)
        if (!updated) {
            exchange.respondJson(404, ErrorResponse("장바구니 항목을 찾을 수 없습니다."))
            return
        }
        exchange.respondStatus(204)
    }

    private fun handleDeleteCartItem(exchange: HttpExchange) {
        val cartItemId = exchange.requestPath.substringAfterLast("/").toLongOrNull()
            ?: throw IllegalArgumentException("유효한 장바구니 항목 ID가 필요합니다.")
        store.deleteCartItem(cartItemId)
        exchange.respondStatus(204)
    }

    private fun handleGetCartItemCount(exchange: HttpExchange) {
        exchange.respondJson(200, CartItemCountResponse(quantity = store.getCartItemQuantity()))
    }

    private fun handleCreateOrder(exchange: HttpExchange) {
        val request = exchange.readJson<OrderRequest>()
        store.createOrder(request.cartItemIds)
        exchange.respondStatus(204)
    }

    private inline fun <reified T> HttpExchange.readJson(): T {
        val rawBody = requestBody.readAllBytes().toString(StandardCharsets.UTF_8)
        if (rawBody.isBlank()) {
            throw IllegalArgumentException("요청 본문이 비어 있습니다.")
        }

        return try {
            json.decodeFromString(rawBody)
        } catch (exception: SerializationException) {
            throw IllegalArgumentException("요청 본문 형식이 올바르지 않습니다.")
        }
    }

    private fun HttpExchange.respondStatus(statusCode: Int) {
        sendResponseHeaders(statusCode, -1)
        close()
    }

    private fun HttpExchange.respondJson(
        statusCode: Int,
        body: Any,
    ) {
        val encodedBody =
            when (body) {
                is ProductResponse -> json.encodeToString(body)
                is ProductPageResponse -> json.encodeToString(body)
                is CartItemResponse -> json.encodeToString(body)
                is CartPageResponse -> json.encodeToString(body)
                is CartItemCountResponse -> json.encodeToString(body)
                is RootResponse -> json.encodeToString(body)
                is HealthResponse -> json.encodeToString(body)
                is ErrorResponse -> json.encodeToString(body)
                else -> throw IllegalArgumentException("지원하지 않는 응답 형식입니다.")
            }
        val payload = encodedBody.toByteArray(StandardCharsets.UTF_8)

        responseHeaders.add("Content-Type", "application/json; charset=utf-8")
        sendResponseHeaders(statusCode, payload.size.toLong())
        responseBody.use { it.write(payload) }
    }

    private val HttpExchange.requestPath: String
        get() = requestURI.path.removeSuffix("/").ifBlank { "/" }

    private fun HttpExchange.queryParameter(name: String): String? =
        requestURI.rawQuery
            ?.split("&")
            ?.mapNotNull { token ->
                val parts = token.split("=", limit = 2)
                val key = parts.firstOrNull() ?: return@mapNotNull null
                val value = parts.getOrNull(1).orEmpty()
                URLDecoder.decode(key, StandardCharsets.UTF_8) to URLDecoder.decode(value, StandardCharsets.UTF_8)
            }?.firstOrNull { it.first == name }
            ?.second
}

private class InMemoryShoppingStore {
    private val lock = Any()
    private val productsById = ProductSeedData.products.associateBy { it.id }
    private val cartItems = linkedMapOf<Long, MutableCartItem>()
    private var nextCartItemId = 1L

    fun getProducts(
        page: Int,
        size: Int,
        category: String?,
    ): ProductPageResponse =
        synchronized(lock) {
            val filtered =
                productsById.values.filter { product ->
                    category.isNullOrBlank() || product.category == category
                }

            val totalElements = filtered.size.toLong()
            val totalPages = filtered.totalPages(size)
            val currentPage = page.coerceAtLeast(0)
            val content = filtered.pageSlice(page = currentPage, size = size)

            ProductPageResponse(
                content = content,
                totalElements = totalElements,
                last = totalPages == 0 || currentPage >= totalPages - 1,
            )
        }

    fun getProduct(productId: Long): ProductResponse? =
        synchronized(lock) {
            productsById[productId]
        }

    fun getCartItems(
        page: Int,
        size: Int,
    ): CartPageResponse =
        synchronized(lock) {
            val currentPage = page.coerceAtLeast(0)
            val snapshot =
                cartItems.values.mapNotNull { cartItem ->
                    val product = productsById[cartItem.productId] ?: return@mapNotNull null
                    CartItemResponse(
                        id = cartItem.id,
                        quantity = cartItem.quantity,
                        product = product,
                    )
                }
            val totalElements = snapshot.size.toLong()

            CartPageResponse(
                content = snapshot.pageSlice(page = currentPage, size = size),
                totalElements = totalElements,
                totalPages = snapshot.totalPages(size),
                number = currentPage,
                size = size.coerceAtLeast(0),
            )
        }

    fun addCartItem(
        productId: Long,
        quantity: Int,
    ) {
        require(quantity > 0) { "수량은 1 이상이어야 합니다." }

        synchronized(lock) {
            require(productsById.containsKey(productId)) { "존재하지 않는 상품입니다." }

            val existing = cartItems.values.firstOrNull { it.productId == productId }
            if (existing != null) {
                existing.quantity += quantity
                return
            }

            val nextId = nextCartItemId++
            cartItems[nextId] = MutableCartItem(id = nextId, productId = productId, quantity = quantity)
        }
    }

    fun updateCartItem(
        cartItemId: Long,
        quantity: Int,
    ): Boolean {
        require(quantity >= 0) { "수량은 0 이상이어야 합니다." }

        synchronized(lock) {
            val target = cartItems[cartItemId] ?: return false
            if (quantity == 0) {
                cartItems.remove(cartItemId)
                return true
            }

            target.quantity = quantity
            return true
        }
    }

    fun deleteCartItem(cartItemId: Long) {
        synchronized(lock) {
            cartItems.remove(cartItemId)
        }
    }

    fun getCartItemQuantity(): Int =
        synchronized(lock) {
            cartItems.values.sumOf { it.quantity }
        }

    fun createOrder(cartItemIds: List<Long>) {
        synchronized(lock) {
            cartItemIds.toSet().forEach { cartItemId ->
                cartItems.remove(cartItemId)
            }
        }
    }

    private fun <T> List<T>.pageSlice(
        page: Int,
        size: Int,
    ): List<T> {
        if (size <= 0) return emptyList()

        val fromIndex = page * size
        if (fromIndex >= this.size) return emptyList()

        val toIndex = (fromIndex + size).coerceAtMost(this.size)
        return subList(fromIndex, toIndex)
    }

    private fun List<*>.totalPages(size: Int): Int {
        if (isEmpty() || size <= 0) return 0
        return (this.size + size - 1) / size
    }
}

private data class MutableCartItem(
    val id: Long,
    val productId: Long,
    var quantity: Int,
)

private object ProductSeedData {
    val products: List<ProductResponse> =
        List(24) { index ->
            val category =
                when (index % 3) {
                    0 -> "dessert"
                    1 -> "fruit"
                    else -> "snack"
                }
            ProductResponse(
                id = (index + 1).toLong(),
                name = "상품${index + 1}",
                price = 10_000 + index * 1_000,
                imageUrl = "https://example.com/product-${index + 1}.png",
                category = category,
            )
        }
}

@Serializable
private data class ProductResponse(
    val id: Long,
    val name: String,
    val price: Int,
    val imageUrl: String,
    val category: String,
)

@Serializable
private data class ProductPageResponse(
    val content: List<ProductResponse>,
    val totalElements: Long,
    val last: Boolean,
)

@Serializable
private data class CartItemResponse(
    val id: Long,
    val quantity: Int,
    val product: ProductResponse,
)

@Serializable
private data class CartPageResponse(
    val content: List<CartItemResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val number: Int,
    val size: Int,
)

@Serializable
private data class CartItemRequest(
    val productId: Long,
    val quantity: Int = 1,
)

@Serializable
private data class CartItemQuantityUpdateRequest(
    val quantity: Int,
)

@Serializable
private data class CartItemCountResponse(
    val quantity: Int,
)

@Serializable
private data class RootResponse(
    val message: String,
    val endpoints: List<String>,
)

@Serializable
private data class HealthResponse(
    val status: String,
)

@Serializable
private data class OrderRequest(
    val cartItemIds: List<Long>,
)

@Serializable
private data class ErrorResponse(
    val message: String,
)
