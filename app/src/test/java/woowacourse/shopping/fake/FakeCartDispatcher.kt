package woowacourse.shopping.fake

import kotlinx.serialization.json.Json
import mockwebserver3.Dispatcher
import mockwebserver3.MockResponse
import mockwebserver3.RecordedRequest
import woowacourse.shopping.data.source.remote.api.AddItemRequestBody
import woowacourse.shopping.data.source.remote.api.QuantityRequestBody
import woowacourse.shopping.data.source.remote.dto.Pageable
import woowacourse.shopping.data.source.remote.dto.Sort
import woowacourse.shopping.data.source.remote.dto.cart.CartContent
import woowacourse.shopping.data.source.remote.dto.cart.CartResponse
import woowacourse.shopping.data.source.remote.dto.cart.Product
import kotlin.math.min

class FakeCartDispatcher(
    private val fixedProductContent: Product,
) : Dispatcher() {
    private val json =
        Json {
            ignoreUnknownKeys = true
        }
    private val allCartItems = mutableListOf<CartContent>()

    override fun dispatch(request: RecordedRequest): MockResponse {
        return try {
            if (request.headers["Authorization"] != "Basic $authToken") return unAuthResponse
            if (request.url.encodedPath.startsWith("/cart-items")) {
                val method = request.method
                val pathSegments = request.url.encodedPathSegments
                if (pathSegments.size <= 1) {
                    if (method == "GET") {
                        val page =
                            request.url.queryParameter("page")?.toInt()
                                ?: return clientErrorResponse
                        val size =
                            request.url.queryParameter("size")?.toInt()
                                ?: return clientErrorResponse
                        return responseGetCartItems(page, size)
                    }

                    if (method == "POST") {
                        val bodyContent = request.body?.utf8() ?: return clientErrorResponse
                        val addItemRequestBody =
                            json.decodeFromString<AddItemRequestBody>(bodyContent)
                        return responseAddItem(addItemRequestBody.quantity)
                    }
                } else if (method == "DELETE") {
                    val id = pathSegments[1].toLongOrNull() ?: return clientErrorResponse
                    return responseDeleteItem(id.toInt())
                } else if (method == "PATCH") {
                    val id = pathSegments[1].toLongOrNull() ?: return clientErrorResponse
                    val bodyContent = request.body?.utf8() ?: return clientErrorResponse
                    val changeQuantityRequestBody =
                        json.decodeFromString<QuantityRequestBody>(bodyContent)
                    return changeQuantity(id.toInt(), changeQuantityRequestBody.quantity)
                }
                clientErrorResponse
            } else {
                MockResponse.Builder().code(404).build()
            }
        } catch (_: Exception) {
            serverErrorResponse
        }
    }

    private val serverErrorResponse = MockResponse.Builder().code(500).build()
    private val clientErrorResponse = MockResponse.Builder().code(404).build()
    private val unAuthResponse = MockResponse.Builder().code(401).build()

    private fun responseGetCartItems(
        page: Int,
        size: Int,
    ): MockResponse {
        val cartItems = allCartItems.subList(page, min(page + size, allCartItems.size))
        return MockResponse
            .Builder()
            .code(200)
            .body(
                json.encodeToString(
                    CartResponse(
                        cartContent = cartItems,
                        empty = cartItems.isEmpty(),
                        first = page == 0,
                        last = page + size >= allCartItems.size,
                        number = 0,
                        numberOfElements = 0,
                        pageable =
                            Pageable(
                                offset = page,
                                pageNumber = page / size,
                                pageSize = size,
                                paged = true,
                                sort = fixedSort,
                                unpaged = false,
                            ),
                        size = cartItems.size,
                        sort = fixedSort,
                        totalElements = 0,
                        totalPages = 0,
                    ),
                ),
            ).build()
    }

    private fun responseDeleteItem(index: Int): MockResponse {
        allCartItems.removeAt(index)
        return MockResponse.Builder().code(200).build()
    }

    private fun responseAddItem(quantity: Int): MockResponse {
        allCartItems.add(
            CartContent(
                id = allCartItems.size.toLong(),
                product = fixedProductContent,
                quantity = quantity,
            ),
        )
        return MockResponse.Builder().code(200).build()
    }

    private fun changeQuantity(
        index: Int,
        quantity: Int,
    ): MockResponse {
        allCartItems[index] = allCartItems[index].copy(quantity = quantity)
        return MockResponse.Builder().code(200).build()
    }

    private val fixedSort =
        Sort(
            empty = true,
            sorted = true,
            unsorted = true,
        )

    companion object {
        val authToken = "hoyWithSmile"
    }
}
