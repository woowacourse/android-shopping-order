package woowacourse.shopping.data.network.mock

import java.net.InetAddress
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import woowacourse.shopping.data.network.cart.dto.CartItemAddRequest
import woowacourse.shopping.data.network.cart.dto.CartItemDto
import woowacourse.shopping.data.network.cart.dto.QuantityDto
import woowacourse.shopping.data.network.order.dto.OrderRequest
import woowacourse.shopping.data.network.product.dto.ProductDto
import woowacourse.shopping.data.network.product.dto.ProductItemDto

class ShoppingMockServer(
    private val json: Json,
) {
    private val server = MockWebServer()
    private val products = defaultProducts().toMutableList()
    private val cartItems = mutableListOf<CartItemDto>()
    private var nextCartItemId = 1

    fun start(): HttpUrl {
        server.dispatcher = ShoppingDispatcher()
        server.start(LOCALHOST, 0)
        return HttpUrl.Builder()
            .scheme("http")
            .host("127.0.0.1")
            .port(server.port)
            .addPathSegment("")
            .build()
    }

    fun shutdown() {
        server.shutdown()
    }

    private inner class ShoppingDispatcher : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse {
            val path = request.requestUrl?.encodedPath.orEmpty()
            return when {
                request.method == "GET" && path == "/products" -> handleProducts(request)
                request.method == "POST" && path == "/products" -> handleProductCreate(request)
                request.method == "GET" && path.startsWith("/products/") -> handleProductDetail(path)
                request.method == "DELETE" && path.startsWith("/products/") -> handleProductDelete(path)
                request.method == "GET" && path == "/cart-items" -> handleCartItems(request)
                request.method == "POST" && path == "/cart-items" -> handleCartItemCreate(request)
                request.method == "GET" && path == "/health" -> textResponse("OK")
                request.method == "GET" && path == "/coupons" -> jsonResponse(defaultCoupons())
                request.method == "GET" && path == "/cart-items/counts" -> {
                    jsonResponse(QuantityDto(cartItems.sumOf { it.quantity }))
                }
                request.method == "PATCH" && path.startsWith("/cart-items/") -> handleCartItemUpdate(path, request)
                request.method == "DELETE" && path.startsWith("/cart-items/") -> handleCartItemDelete(path)
                request.method == "POST" && path == "/orders" -> handleOrder(request)
                else -> emptyResponse(404)
            }
        }
    }

    private fun handleProducts(request: RecordedRequest): MockResponse {
        val url = request.requestUrl ?: return emptyResponse(400)
        val page = url.queryParameter("page")?.toIntOrNull() ?: 0
        val size = url.queryParameter("size")?.toIntOrNull() ?: 20
        val category = url.queryParameter("category")
        val filtered = products.filter { category.isNullOrBlank() || it.category == category }
        val start = (page * size).coerceAtMost(filtered.size)
        val end = (start + size).coerceAtMost(filtered.size)
        val content = filtered.subList(start, end).map {
            ProductItemDto(
                category = it.category,
                id = it.id.toLong(),
                imageUrl = it.imageUrl,
                name = it.name,
                price = it.price,
            )
        }
        return jsonResponse(pageResponse(content = content, page = page, size = size, totalElements = filtered.size))
    }

    private fun handleProductCreate(request: RecordedRequest): MockResponse {
        val incoming = json.decodeFromString<ProductRequest>(request.body.readUtf8())
        products += ProductDto(
            id = (products.maxOfOrNull { it.id } ?: 0) + 1,
            name = incoming.name.orEmpty(),
            price = incoming.price ?: 0,
            imageUrl = incoming.imageUrl.orEmpty(),
            category = incoming.category.orEmpty(),
        )
        return emptyResponse(201)
    }

    private fun handleProductDetail(path: String): MockResponse {
        val id = path.substringAfterLast("/").toIntOrNull() ?: return emptyResponse(400)
        val product = products.firstOrNull { it.id == id } ?: return emptyResponse(404)
        return jsonResponse(product)
    }

    private fun handleProductDelete(path: String): MockResponse {
        val id = path.substringAfterLast("/").toIntOrNull() ?: return emptyResponse(400)
        products.removeAll { it.id == id }
        cartItems.removeAll { it.product.id == id }
        return emptyResponse(204)
    }

    private fun handleCartItems(request: RecordedRequest): MockResponse {
        val url = request.requestUrl ?: return emptyResponse(400)
        val page = url.queryParameter("page")?.toIntOrNull() ?: 0
        val size = url.queryParameter("size")?.toIntOrNull() ?: 20
        val start = (page * size).coerceAtMost(cartItems.size)
        val end = (start + size).coerceAtMost(cartItems.size)
        return jsonResponse(pageResponse(content = cartItems.subList(start, end), page = page, size = size, totalElements = cartItems.size))
    }

    private fun handleCartItemCreate(request: RecordedRequest): MockResponse {
        val body = json.decodeFromString<CartItemAddRequest>(request.body.readUtf8())
        val product = products.firstOrNull { it.id == body.productId.toInt() } ?: return emptyResponse(404)
        val existingIndex = cartItems.indexOfFirst { it.product.id == product.id }
        if (existingIndex >= 0) {
            val existing = cartItems[existingIndex]
            cartItems[existingIndex] = existing.copy(quantity = existing.quantity + body.quantity)
        } else {
            cartItems += CartItemDto(
                id = nextCartItemId++,
                product = product,
                quantity = body.quantity,
            )
        }
        return emptyResponse(201)
    }

    private fun handleCartItemUpdate(
        path: String,
        request: RecordedRequest,
    ): MockResponse {
        val id = path.substringAfterLast("/").toIntOrNull() ?: return emptyResponse(400)
        val body = json.decodeFromString<QuantityDto>(request.body.readUtf8())
        val index = cartItems.indexOfFirst { it.id == id }
        if (index < 0) return emptyResponse(404)
        cartItems[index] = cartItems[index].copy(quantity = body.quantity)
        return emptyResponse(200)
    }

    private fun handleCartItemDelete(path: String): MockResponse {
        val id = path.substringAfterLast("/").toIntOrNull() ?: return emptyResponse(400)
        cartItems.removeAll { it.id == id }
        return emptyResponse(204)
    }

    private fun handleOrder(request: RecordedRequest): MockResponse {
        val ids = json.decodeFromString<OrderRequest>(request.body.readUtf8()).cartItemIds.toSet()
        cartItems.removeAll { it.id.toLong() in ids }
        return emptyResponse(201)
    }

    private fun emptyResponse(code: Int): MockResponse = MockResponse().setResponseCode(code)

    private fun textResponse(
        body: String,
        code: Int = 200,
    ): MockResponse = MockResponse()
        .setResponseCode(code)
        .setHeader("Content-Type", "text/plain")
        .setBody(body)

    private inline fun <reified T> jsonResponse(
        body: T,
        code: Int = 200,
    ): MockResponse = MockResponse()
        .setResponseCode(code)
        .setHeader("Content-Type", "application/json")
        .setBody(json.encodeToString(body))

    private fun <T> pageResponse(
        content: List<T>,
        page: Int,
        size: Int,
        totalElements: Int,
    ): PageResponse<T> {
        val safeSize = size.coerceAtLeast(1)
        val totalPages = if (totalElements == 0) 0 else (totalElements + safeSize - 1) / safeSize
        return PageResponse(
            content = content,
            totalElements = totalElements,
            totalPages = totalPages,
            first = page == 0,
            last = page >= (totalPages - 1).coerceAtLeast(0),
            numberOfElements = content.size,
            sort = SortObject(sorted = false, unsorted = true, empty = true),
            pageable = PageableObject(
                sort = SortObject(sorted = false, unsorted = true, empty = true),
                paged = true,
                pageNumber = page,
                pageSize = safeSize,
                unpaged = false,
                offset = page.toLong() * safeSize,
            ),
            size = safeSize,
            number = page,
            empty = content.isEmpty(),
        )
    }

    @Serializable
    private data class ProductRequest(
        @SerialName("name")
        val name: String? = null,
        @SerialName("price")
        val price: Int? = null,
        @SerialName("imageUrl")
        val imageUrl: String? = null,
        @SerialName("category")
        val category: String? = null,
    )

    @Serializable
    private data class PageResponse<T>(
        @SerialName("totalElements")
        val totalElements: Int,
        @SerialName("totalPages")
        val totalPages: Int,
        @SerialName("first")
        val first: Boolean,
        @SerialName("last")
        val last: Boolean,
        @SerialName("numberOfElements")
        val numberOfElements: Int,
        @SerialName("sort")
        val sort: SortObject,
        @SerialName("pageable")
        val pageable: PageableObject,
        @SerialName("size")
        val size: Int,
        @SerialName("content")
        val content: List<T>,
        @SerialName("number")
        val number: Int,
        @SerialName("empty")
        val empty: Boolean,
    )

    @Serializable
    private data class PageableObject(
        @SerialName("sort")
        val sort: SortObject,
        @SerialName("paged")
        val paged: Boolean,
        @SerialName("pageNumber")
        val pageNumber: Int,
        @SerialName("pageSize")
        val pageSize: Int,
        @SerialName("unpaged")
        val unpaged: Boolean,
        @SerialName("offset")
        val offset: Long,
    )

    @Serializable
    private data class SortObject(
        @SerialName("sorted")
        val sorted: Boolean,
        @SerialName("unsorted")
        val unsorted: Boolean,
        @SerialName("empty")
        val empty: Boolean,
    )

    @Serializable
    private data class Coupon(
        @SerialName("id")
        val id: Long,
        @SerialName("code")
        val code: String,
        @SerialName("description")
        val description: String,
        @SerialName("expirationDate")
        val expirationDate: String,
        @SerialName("discountType")
        val discountType: String,
        @SerialName("discount")
        val discount: Int? = null,
        @SerialName("minimumAmount")
        val minimumAmount: Int? = null,
    )

    private companion object {
        private val LOCALHOST: InetAddress = InetAddress.getByAddress(byteArrayOf(127, 0, 0, 1))
        private val categories = listOf("음료", "의류", "식품", "생활", "디지털")
        private const val IMAGE_BASE_URL =
            "https://github.com/CommitTheKermit/android-shopping-cart/blob/step1/images/product_image"
        private const val IMAGE_URL_SUFFIX = ".png?raw=true"

        private fun defaultProducts(): List<ProductDto> = (1..35).map { index ->
            ProductDto(
                id = index,
                name = "품목$index",
                price = index * 1_000,
                imageUrl = "$IMAGE_BASE_URL${(index - 1) % 5}$IMAGE_URL_SUFFIX",
                category = categories[(index - 1) % categories.size],
            )
        }

        private fun defaultCoupons(): List<Coupon> = listOf(
            Coupon(
                id = 1,
                code = "WELCOME3000",
                description = "3,000원 할인",
                expirationDate = "2026-12-31",
                discountType = "FIXED_DISCOUNT",
                discount = 3_000,
                minimumAmount = 10_000,
            ),
            Coupon(
                id = 2,
                code = "FREESHIP",
                description = "무료 배송",
                expirationDate = "2026-12-31",
                discountType = "FREE_SHIPPING",
                minimumAmount = 20_000,
            ),
        )
    }
}
