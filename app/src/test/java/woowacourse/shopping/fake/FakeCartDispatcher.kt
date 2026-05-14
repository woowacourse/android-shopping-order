package woowacourse.shopping.fake

import kotlinx.serialization.json.Json
import mockwebserver3.Dispatcher
import mockwebserver3.MockResponse
import mockwebserver3.RecordedRequest
import woowacourse.shopping.data.source.remote.api.AddItemRequestBody
import woowacourse.shopping.data.source.remote.dto.Pageable
import woowacourse.shopping.data.source.remote.dto.Sort
import woowacourse.shopping.data.source.remote.dto.cart.CartContent
import woowacourse.shopping.data.source.remote.dto.cart.CartResponse
import woowacourse.shopping.data.source.remote.dto.cart.Product

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
//            if (request.headers["Authorization"] != authToken) return unAuthResponse
            when (request.url.encodedPath) {
                "/cart-items" -> {
                    val pathSegments = request.url.encodedPathSegments
                    if (pathSegments.size <= 1) {
                        val method = request.method
                        if (method == "GET") {
                            val page = request.url.queryParameter("page")?.toInt() ?: return clientErrorResponse
                            val size = request.url.queryParameter("size")?.toInt() ?: return clientErrorResponse
                            return responseGetCartItems(page, size)
                        }

                        if (method == "POST") {
                            val bodyContent = request.body?.utf8() ?: return clientErrorResponse
                            val addItemRequestBody = json.decodeFromString<AddItemRequestBody>(bodyContent)
                            allCartItems.add(
                                CartContent(
                                    id = allCartItems.size.toLong(),
                                    product = fixedProductContent,
                                    quantity = addItemRequestBody.quantity,
                                ),
                            )
                        }
                    }
                    clientErrorResponse
                }
                else -> MockResponse.Builder().code(404).build()
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
    ): MockResponse =
        MockResponse
            .Builder()
            .code(200)
            .body(
                json.encodeToString(
                    CartResponse(
                        cartContent = allCartItems,
                        empty = true,
                        first = true,
                        last = true,
                        number = 0,
                        numberOfElements = 0,
                        pageable =
                            Pageable(
                                offset = 0,
                                pageNumber = 0,
                                pageSize = 0,
                                paged = false,
                                sort = fixedSort,
                                unpaged = false,
                            ),
                        size = 0,
                        sort = fixedSort,
                        totalElements = 0,
                        totalPages = 0,
                    ),
                ),
            ).build()

    private val fixedSort =
        Sort(
            empty = true,
            sorted = true,
            unsorted = true,
        )

    companion object {
        val authToken =
            java.util.Base64
                .getEncoder()
                .encodeToString("joon0447:password".toByteArray())
    }
}
