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
            exchange.requestMethod == "GET" && exchange.requestPath == "/coupons" -> handleGetCoupons(exchange)
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
                        "GET /coupons",
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
        val productId =
            exchange.requestPath.substringAfterLast("/").toLongOrNull()
                ?: throw IllegalArgumentException("유효한 상품 ID가 필요합니다.")
        val product =
            store.getProduct(productId)
                ?: return exchange.respondJson(404, ErrorResponse("상품을 찾을 수 없습니다."))
        exchange.respondJson(200, product)
    }

    private fun handleGetCoupons(exchange: HttpExchange) {
        exchange.respondJson(200, CouponListResponse(store.getCoupons()))
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
        val cartItemId =
            exchange.requestPath.substringAfterLast("/").toLongOrNull()
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
        val cartItemId =
            exchange.requestPath.substringAfterLast("/").toLongOrNull()
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
                is CouponListResponse -> json.encodeToString(body)
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

    fun getCoupons(): List<CouponResponse> = CouponSeedData.coupons

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
    private const val PRODUCT_IMAGE_URL = "https://cdn.frame-less.co.kr/news/photo/202510/1344_3681_750.jpg"

    val products: List<ProductResponse> =
        listOf(
            product(id = 1L, name = "다람", price = 10_000, category = "dessert"),
            product(id = 2L, name = "뽀야미", price = 11_000, category = "dessert"),
            product(id = 3L, name = "바닐라", price = 12_000, category = "dessert"),
            product(id = 4L, name = "애플", price = 13_000, category = "fruit"),
            product(id = 6L, name = "글루민", price = 15_000, category = "snack"),
            product(id = 5L, name = "샤니", price = 14_000, category = "snack"),
            product(id = 7L, name = "다람2", price = 10_000, category = "dessert"),
            product(id = 8L, name = "뽀야미2", price = 11_000, category = "dessert"),
            product(id = 9L, name = "바닐라2", price = 12_000, category = "dessert"),
            product(id = 10L, name = "애플2", price = 13_000, category = "fruit"),
            product(id = 12L, name = "글루민2", price = 15_000, category = "snack"),
            product(id = 11L, name = "샤니2", price = 14_000, category = "snack"),
            product(id = 13L, name = "다람3", price = 10_000, category = "dessert"),
            product(id = 14L, name = "뽀야미3", price = 11_000, category = "dessert"),
            product(id = 15L, name = "바닐라3", price = 12_000, category = "dessert"),
            product(id = 16L, name = "애플3", price = 13_000, category = "fruit"),
            product(id = 18L, name = "글루민3", price = 15_000, category = "snack"),
            product(id = 17L, name = "샤니3", price = 14_000, category = "snack"),
            product(id = 19L, name = "다람4", price = 10_000, category = "dessert"),
            product(id = 20L, name = "뽀야미4", price = 11_000, category = "dessert"),
            product(id = 21L, name = "바닐라4", price = 12_000, category = "dessert"),
            product(id = 22L, name = "애플4", price = 13_000, category = "fruit"),
            product(id = 24L, name = "글루민4", price = 15_000, category = "snack"),
            product(id = 23L, name = "샤니4", price = 14_000, category = "snack"),
        )

    private fun product(
        id: Long,
        name: String,
        price: Int,
        category: String,
    ): ProductResponse =
        ProductResponse(
            id = id,
            name = name,
            price = price,
            imageUrl = PRODUCT_IMAGE_URL,
            category = category,
        )
}

private object CouponSeedData {
    val coupons: List<CouponResponse> =
        listOf(
            CouponResponse(
                id = 1L,
                code = "FIXED5000",
                title = "5,000원 할인 쿠폰",
                description = "100,000원 이상 주문 시 5,000원을 할인합니다.",
                expirationDate = "2026-12-31",
                minimumOrderAmount = 100_000,
                fixedDiscountAmount = 5_000,
            ),
            CouponResponse(
                id = 2L,
                code = "BOGO",
                title = "3개 구매 1개 가격 할인 쿠폰",
                description = "동일한 상품을 3개 담으면 가장 비싼 상품 1개 가격만큼 할인합니다.",
                expirationDate = "2026-11-30",
                requiredSameProductQuantity = 3,
                bogoEligible = true,
            ),
            CouponResponse(
                id = 3L,
                code = "FREESHIPPING",
                title = "무료 배송 쿠폰",
                description = "50,000원 이상 주문 시 배송비를 무료로 처리합니다.",
                expirationDate = "2026-10-31",
                minimumOrderAmount = 50_000,
                freeShipping = true,
            ),
            CouponResponse(
                id = 4L,
                code = "MIRACLESALE",
                title = "미라클 세일 30% 할인 쿠폰",
                description = "오전 4시부터 7시 사이 주문 금액의 30%를 할인합니다.",
                expirationDate = "2026-09-30",
                percentageDiscountRate = 30,
                availableFromHour = 4,
                availableToHourExclusive = 7,
            ),
        )
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
private data class CouponListResponse(
    val coupons: List<CouponResponse>,
)

@Serializable
private data class CouponResponse(
    val id: Long,
    val code: String,
    val title: String,
    val description: String,
    val expirationDate: String,
    val minimumOrderAmount: Int? = null,
    val fixedDiscountAmount: Int? = null,
    val percentageDiscountRate: Int? = null,
    val requiredSameProductQuantity: Int? = null,
    val freeShipping: Boolean = false,
    val bogoEligible: Boolean = false,
    val availableFromHour: Int? = null,
    val availableToHourExclusive: Int? = null,
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
