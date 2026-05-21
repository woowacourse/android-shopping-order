package woowacourse.shopping.mock

import android.util.Log
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import woowacourse.shopping.data.remote.dto.CartItemRequest
import woowacourse.shopping.data.remote.dto.CartItemResponse
import woowacourse.shopping.data.remote.dto.CartResponse
import woowacourse.shopping.data.remote.dto.OrderRequest
import woowacourse.shopping.data.remote.dto.PageableResponse
import woowacourse.shopping.data.remote.dto.ProductResponse
import woowacourse.shopping.data.remote.dto.ProductsResponse
import woowacourse.shopping.data.remote.dto.Quantity
import woowacourse.shopping.data.remote.dto.SortResponse
import java.net.HttpURLConnection
import java.net.InetAddress

object ShoppingMockServer {
    private const val TAG = "ShoppingMockServer"
    private const val PORT = 8080

    private val server =
        MockWebServer().apply {
            dispatcher = ShoppingMockDispatcher()
        }

    @Volatile
    private var serverStarted = false

    fun start() {
        if (serverStarted) return
        server.start(loopbackAddress(), PORT)
        serverStarted = true
        Log.d(TAG, "Mock server started at http://127.0.0.1:$PORT/")
    }

    private fun loopbackAddress(): InetAddress = InetAddress.getByAddress(byteArrayOf(127, 0, 0, 1))
}

private val json = Json { ignoreUnknownKeys = true }

private class ShoppingMockDispatcher : Dispatcher() {
    private val repository = MockShoppingRepository()

    override fun dispatch(request: RecordedRequest): MockResponse {
        Log.d("ShoppingMockServer", "Received ${request.method} ${request.path}")
        val url = request.requestUrl ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        return when {
            request.method == "GET" && url.encodedPath == "/products" -> products(url)
            request.method == "GET" && url.pathSegments.size == 2 && url.pathSegments[0] == "products" -> productDetail(url)
            request.method == "GET" && url.encodedPath == "/cart-items" -> cartItems(url)
            request.method == "GET" && url.encodedPath == "/cart-items/counts" -> count()
            request.method == "POST" && url.encodedPath == "/cart-items" -> addCartItem(request)
            request.method == "PATCH" && url.pathSegments.size == 2 && url.pathSegments[0] == "cart-items" -> updateCartItem(url, request)
            request.method == "DELETE" && url.pathSegments.size == 2 && url.pathSegments[0] == "cart-items" -> deleteCartItem(url)
            request.method == "POST" && url.encodedPath == "/orders" -> order(request)
            else -> errorResponse(HttpURLConnection.HTTP_NOT_FOUND)
        }
    }

    private fun products(url: HttpUrl): MockResponse {
        val page = url.queryParameter("page")?.toIntOrNull() ?: 0
        val size = url.queryParameter("size")?.toIntOrNull() ?: 20
        return jsonResponse(repository.getProducts(page, size))
    }

    private fun productDetail(url: HttpUrl): MockResponse {
        val id = url.pathSegments[1].toLongOrNull() ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        val product = repository.getProduct(id) ?: return errorResponse(HttpURLConnection.HTTP_NOT_FOUND)
        return jsonResponse(product)
    }

    private fun cartItems(url: HttpUrl): MockResponse {
        val page = url.queryParameter("page")?.toIntOrNull() ?: 0
        val size = url.queryParameter("size")?.toIntOrNull() ?: Int.MAX_VALUE
        return jsonResponse(repository.getCartItems(page, size))
    }

    private fun count(): MockResponse = jsonResponse(repository.getCartItemsCount())

    private fun addCartItem(request: RecordedRequest): MockResponse {
        val payload = json.decodeFromString<CartItemRequest>(request.body.readUtf8())
        repository.addCartItem(payload.productId, payload.quantity)
        return emptyResponse(HttpURLConnection.HTTP_OK)
    }

    private fun updateCartItem(
        url: HttpUrl,
        request: RecordedRequest,
    ): MockResponse {
        val id = url.pathSegments[1].toLongOrNull() ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        val payload = json.decodeFromString<Quantity>(request.body.readUtf8())
        repository.updateCartItem(id, payload.quantity)
        return emptyResponse(HttpURLConnection.HTTP_OK)
    }

    private fun deleteCartItem(url: HttpUrl): MockResponse {
        val id = url.pathSegments[1].toLongOrNull() ?: return errorResponse(HttpURLConnection.HTTP_BAD_REQUEST)
        repository.deleteCartItem(id)
        return emptyResponse(HttpURLConnection.HTTP_NO_CONTENT)
    }

    private fun order(request: RecordedRequest): MockResponse {
        val payload = json.decodeFromString<OrderRequest>(request.body.readUtf8())
        repository.order(payload.cartItemIds)
        return emptyResponse(HttpURLConnection.HTTP_OK)
    }

    private fun jsonResponse(body: Any): MockResponse =
        MockResponse()
            .setResponseCode(HttpURLConnection.HTTP_OK)
            .setHeader("Content-Type", "application/json")
            .setBody(
                when (body) {
                    is ProductsResponse -> json.encodeToString(body)
                    is ProductResponse -> json.encodeToString(body)
                    is CartResponse -> json.encodeToString(body)
                    is Quantity -> json.encodeToString(body)
                    else -> error("Unsupported response type: ${body::class.java.simpleName}")
                },
            )

    private fun emptyResponse(code: Int): MockResponse = MockResponse().setResponseCode(code)

    private fun errorResponse(code: Int): MockResponse =
        MockResponse()
            .setResponseCode(code)
            .setHeader("Content-Type", "application/json")
            .setBody("""{"code":$code}""")
}

private class MockShoppingRepository {
    private val products =
        listOf(
            product(1, "Americano", 2500, "COFFEE"),
            product(2, "Cafe Latte", 3500, "LATTE"),
            product(3, "Cappuccino", 3500, "COFFEE"),
            product(4, "Vanilla Latte", 4200, "LATTE"),
            product(5, "Cold Brew", 4000, "COFFEE"),
            product(6, "Hazelnut Latte", 4300, "LATTE"),
            product(7, "Green Tea", 3200, "TEA"),
            product(8, "Earl Grey", 3200, "TEA"),
            product(9, "Grapefruit Ade", 4500, "BEVERAGE"),
            product(10, "Lemon Ade", 4500, "BEVERAGE"),
            product(11, "Strawberry Smoothie", 5300, "SMOOTHIE"),
            product(12, "Mango Smoothie", 5300, "SMOOTHIE"),
            product(13, "Blueberry Yogurt Smoothie", 5500, "SMOOTHIE"),
            product(14, "Chocolate Frappe", 5500, "FRAPPE"),
            product(15, "Mocha Frappe", 5500, "FRAPPE"),
            product(16, "Cheese Cake", 5500, "DESSERT"),
            product(17, "Chocolate Cookie", 2500, "DESSERT"),
            product(18, "Bagel", 2800, "BAKERY"),
            product(19, "Croissant", 3200, "BAKERY"),
            product(20, "Tumbler", 12000, "MD"),
            product(21, "Mug Cup", 9000, "MD"),
            product(22, "Orange Juice", 4800, "JUICE"),
            product(23, "Apple Juice", 4800, "JUICE"),
            product(24, "Decaf Americano", 2800, "DECAFFEINE"),
        )

    private val cartItems =
        linkedMapOf(
            1L to CartEntry(id = 1L, productId = 2L, quantity = 1),
            2L to CartEntry(id = 2L, productId = 9L, quantity = 2),
        )
    private var nextCartItemId = 3L

    @Synchronized
    fun getProducts(
        page: Int,
        size: Int,
    ): ProductsResponse = products.toProductsResponse(page, size)

    @Synchronized
    fun getProduct(id: Long): ProductResponse? = products.find { it.id == id }

    @Synchronized
    fun getCartItems(
        page: Int,
        size: Int,
    ): CartResponse {
        val allItems =
            cartItems.values.mapNotNull { entry ->
                products.find { it.id == entry.productId }?.let { product ->
                    CartItemResponse(
                        id = entry.id,
                        product = product,
                        quantity = entry.quantity,
                    )
                }
            }
        return allItems.toCartResponse(page, size)
    }

    @Synchronized
    fun getCartItemsCount(): Quantity = Quantity(cartItems.size)

    @Synchronized
    fun addCartItem(
        productId: Long,
        quantity: Int,
    ) {
        val existing = cartItems.values.find { it.productId == productId }
        if (existing != null) {
            existing.quantity += quantity
            return
        }
        cartItems[nextCartItemId] = CartEntry(nextCartItemId, productId, quantity)
        nextCartItemId += 1
    }

    @Synchronized
    fun updateCartItem(
        id: Long,
        quantity: Int,
    ) {
        val target = cartItems[id] ?: return
        if (quantity <= 0) {
            cartItems.remove(id)
            return
        }
        target.quantity = quantity
    }

    @Synchronized
    fun deleteCartItem(id: Long) {
        cartItems.remove(id)
    }

    @Synchronized
    fun order(cartItemIds: List<Long>) {
        cartItemIds.forEach(cartItems::remove)
    }

    private fun product(
        id: Long,
        name: String,
        price: Long,
        category: String,
    ): ProductResponse =
        ProductResponse(
            id = id,
            name = name,
            price = price,
            imageUrl = "https://picsum.photos/seed/$id/640/640",
            category = category,
        )
}

private fun pageable(
    page: Int,
    size: Int,
    offset: Int,
): PageableResponse =
    PageableResponse(
        pageNumber = page,
        pageSize = size,
        sort = defaultSort(),
        offset = offset,
        paged = true,
        unpaged = false,
    )

private fun defaultSort(): SortResponse =
    SortResponse(
        sorted = false,
        unsorted = true,
        empty = true,
    )

private fun List<ProductResponse>.toProductsResponse(
    page: Int,
    size: Int,
): ProductsResponse {
    val safePage = page.coerceAtLeast(0)
    val safeSize = size.coerceAtLeast(1)
    val totalElements = this.size
    val totalPages = if (totalElements == 0) 1 else ((totalElements - 1) / safeSize) + 1
    val offset = safePage * safeSize
    val pageItems = drop(offset).take(safeSize)
    return ProductsResponse(
        content = pageItems,
        pageable = pageable(safePage, safeSize, offset),
        last = safePage >= totalPages - 1,
        totalPages = totalPages,
        totalElements = totalElements,
        sort = defaultSort(),
        first = safePage == 0,
        number = safePage,
        numberOfElements = pageItems.size,
        size = safeSize,
        empty = pageItems.isEmpty(),
    )
}

private fun List<CartItemResponse>.toCartResponse(
    page: Int,
    size: Int,
): CartResponse {
    val safePage = page.coerceAtLeast(0)
    val safeSize = size.coerceAtLeast(1)
    val totalElements = this.size
    val totalPages = if (totalElements == 0) 1 else ((totalElements - 1) / safeSize) + 1
    val offset = safePage * safeSize
    val pageItems = drop(offset).take(safeSize)
    return CartResponse(
        content = pageItems,
        pageable = pageable(safePage, safeSize, offset),
        totalElements = totalElements.toLong(),
        totalPages = totalPages,
        last = safePage >= totalPages - 1,
        size = safeSize,
        number = safePage,
        sort = defaultSort(),
        numberOfElements = pageItems.size,
        first = safePage == 0,
        empty = pageItems.isEmpty(),
    )
}

private data class CartEntry(
    val id: Long,
    val productId: Long,
    var quantity: Int,
)
