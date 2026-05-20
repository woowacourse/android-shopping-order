package woowacourse.shopping.data.remote

import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import woowacourse.shopping.data.remote.dto.CartItemRequest
import woowacourse.shopping.data.remote.dto.CartItemResponse
import woowacourse.shopping.data.remote.dto.CartResponse
import woowacourse.shopping.data.remote.dto.PageableResponse
import woowacourse.shopping.data.remote.dto.ProductResponse
import woowacourse.shopping.data.remote.dto.ProductsResponse
import woowacourse.shopping.data.remote.dto.Quantity
import woowacourse.shopping.data.remote.dto.SortResponse
import kotlin.math.ceil

class MockInterceptor : Interceptor {
    private val json = Json { ignoreUnknownKeys = true }

    private val products = (1..30).map { i ->
        val mod = i % 5
        ProductResponse(
            id = i.toLong(),
            name = when (mod) {
                0 -> "맛있는 치킨 $i"
                1 -> "고소한 피자 $i"
                2 -> "든든한 햄버거 $i"
                3 -> "시원한 콜라 $i"
                else -> "톡쏘는 맥주 $i"
            },
            price = (5000 + (i * 1000)).toLong(),
            imageUrl = "https://picsum.photos/seed/${i + 100}/800/800",
            category = if (mod in 0..2) "FOOD" else "DRINK"
        )
    }

    private val cartItems = mutableListOf<CartItemResponse>()
    private var cartIdCounter = 1L

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
        val path = url.encodedPath
        val method = request.method

        return when {
            path.endsWith("/products") && method == "GET" -> {
                val page = url.queryParameter("page")?.toIntOrNull() ?: 0
                val size = url.queryParameter("size")?.toIntOrNull() ?: 20

                val startIndex = page * size
                val endIndex = minOf(startIndex + size, products.size)

                val content = if (startIndex < products.size) {
                    products.subList(startIndex, endIndex)
                } else {
                    emptyList()
                }

                val totalPages =
                    if (products.isEmpty()) 0 else ceil(products.size.toDouble() / size).toInt()
                val isLast = if (products.isEmpty()) true else page >= totalPages - 1

                val response = ProductsResponse(
                    content = content,
                    pageable = createMockPageable(page, size),
                    last = isLast,
                    totalPages = totalPages,
                    totalElements = products.size,
                    sort = createMockSort(),
                    first = page == 0,
                    number = page,
                    numberOfElements = content.size,
                    size = size,
                    empty = content.isEmpty()
                )
                makeJsonResponse(chain, 200, json.encodeToString(response))
            }

            path.contains("/products/") && method == "GET" -> {
                val id = path.substringAfterLast("/").toLongOrNull()
                val product = products.find { it.id == id }
                if (product != null) {
                    makeJsonResponse(chain, 200, json.encodeToString(product))
                } else {
                    makeJsonResponse(chain, 404, "Product not found")
                }
            }

            path.endsWith("/cart-items") && method == "GET" -> {
                val page = url.queryParameter("page")?.toIntOrNull() ?: 0
                val size = url.queryParameter("size")?.toIntOrNull() ?: 50

                val startIndex = page * size
                val endIndex = minOf(startIndex + size, cartItems.size)

                val content = if (startIndex < cartItems.size) {
                    cartItems.subList(startIndex, endIndex)
                } else {
                    emptyList()
                }

                val totalPages =
                    if (cartItems.isEmpty()) 0 else ceil(cartItems.size.toDouble() / size).toInt()
                val isLast = if (cartItems.isEmpty()) true else page >= totalPages - 1

                val response = CartResponse(
                    content = content,
                    pageable = createMockPageable(page, size),
                    totalElements = cartItems.size.toLong(),
                    totalPages = totalPages,
                    last = isLast,
                    size = size,
                    number = page,
                    sort = createMockSort(),
                    numberOfElements = content.size,
                    first = page == 0,
                    empty = cartItems.isEmpty()
                )
                makeJsonResponse(chain, 200, json.encodeToString(response))
            }

            path.endsWith("/cart-items") && method == "POST" -> {
                val bodyString = request.body?.let {
                    val buffer = Buffer()
                    it.writeTo(buffer)
                    buffer.readUtf8()
                } ?: ""
                val cartRequest = json.decodeFromString<CartItemRequest>(bodyString)
                val product = products.find { it.id == cartRequest.productId }
                if (product != null) {
                    val existing = cartItems.find { it.product.id == product.id }
                    val id = if (existing != null) {
                        val index = cartItems.indexOf(existing)
                        cartItems[index] =
                            existing.copy(quantity = existing.quantity + cartRequest.quantity)
                        existing.id
                    } else {
                        val newId = cartIdCounter++
                        cartItems.add(CartItemResponse(newId, product, cartRequest.quantity))
                        newId
                    }
                    Response.Builder()
                        .request(chain.request())
                        .protocol(Protocol.HTTP_1_1)
                        .code(201)
                        .message("Created")
                        .header("Location", "/cart-items/$id")
                        .body("".toResponseBody("application/json".toMediaType()))
                        .build()
                } else {
                    makeJsonResponse(chain, 404, "Product not found")
                }
            }

            path.contains("/cart-items/") && method == "DELETE" -> {
                val id = path.substringAfterLast("/").toLongOrNull()
                cartItems.removeIf { it.id == id }
                makeJsonResponse(chain, 204, "")
            }

            path.contains("/cart-items/") && method == "PATCH" -> {
                val id = path.substringAfterLast("/").toLongOrNull()
                val bodyString = request.body?.let {
                    val buffer = Buffer()
                    it.writeTo(buffer)
                    buffer.readUtf8()
                } ?: ""
                val quantityReq = json.decodeFromString<Quantity>(bodyString)
                val index = cartItems.indexOfFirst { it.id == id }
                if (index != -1) {
                    cartItems[index] = cartItems[index].copy(quantity = quantityReq.quantity)
                    makeJsonResponse(chain, 200, "")
                } else {
                    makeJsonResponse(chain, 404, "Cart item not found")
                }
            }

            path.endsWith("/cart-items/counts") && method == "GET" -> {
                val count = Quantity(cartItems.sumOf { it.quantity })
                makeJsonResponse(chain, 200, json.encodeToString(count))
            }

            path.endsWith("/orders") && method == "POST" -> {
                cartItems.clear()
                makeJsonResponse(chain, 200, "")
            }

            else -> chain.proceed(request)
        }
    }

    private fun makeJsonResponse(chain: Interceptor.Chain, code: Int, content: String): Response {
        return Response.Builder()
            .request(chain.request())
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code < 300) "OK" else "Error")
            .body(content.toResponseBody("application/json".toMediaType()))
            .addHeader("content-type", "application/json")
            .build()
    }

    private fun createMockPageable(page: Int, size: Int) = PageableResponse(
        pageNumber = page,
        pageSize = size,
        sort = createMockSort(),
        offset = page * size,
        paged = true,
        unpaged = false
    )

    private fun createMockSort() = SortResponse(
        sorted = false,
        unsorted = true,
        empty = true
    )
}
