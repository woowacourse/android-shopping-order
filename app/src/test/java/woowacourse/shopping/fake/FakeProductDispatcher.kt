package woowacourse.shopping.fake

import kotlinx.serialization.json.Json
import mockwebserver3.Dispatcher
import mockwebserver3.MockResponse
import mockwebserver3.RecordedRequest
import woowacourse.shopping.data.source.remote.dto.Pageable
import woowacourse.shopping.data.source.remote.dto.Sort
import woowacourse.shopping.data.source.remote.dto.product.ProductContent
import woowacourse.shopping.data.source.remote.dto.product.ProductResponse
import woowacourse.shopping.data.source.remote.dto.product.ProductsResponse
import woowacourse.shopping.domain.model.Product

class FakeProductDispatcher(
    size: Int,
    product: Product,
) : Dispatcher() {
    private val json =
        Json {
            ignoreUnknownKeys = true
        }
    private val allProductContents =
        List(size) { index ->
            ProductContent(
                category = "비밀",
                id = (index + 1).toLong(),
                imageUrl = product.imageUrl,
                name = product.name.name,
                price = product.price.amount.toInt(),
            )
        }

    override fun dispatch(request: RecordedRequest): MockResponse {
        return try {
            val requestUrl = request.url.encodedPath
            when {
                requestUrl.contains("/products") -> {
                    val pathSegments = request.url.encodedPathSegments
                    if (pathSegments.size <= 1) {
                        val page = request.url.queryParameter("page")?.toInt() ?: return response400
                        val size = request.url.queryParameter("size")?.toInt() ?: return response400
                        return requestProducts(page, size)
                    }
                    val productId = pathSegments[1].toLongOrNull() ?: return response400
                    requestProduct(productId)
                }
                else -> MockResponse.Builder().code(404).build()
            }
        } catch (_: Exception) {
            response500
        }
    }

    private fun requestProducts(
        page: Int,
        size: Int,
    ): MockResponse {
        val productContents = allProductContents.subList(page, page + size)
        return MockResponse
            .Builder()
            .code(200)
            .body(
                json.encodeToString(
                    ProductsResponse(
                        content = productContents,
                        empty = productContents.isEmpty(),
                        first = page == 0,
                        last = allProductContents.size <= page + size,
                        number = 0,
                        numberOfElements = productContents.size,
                        pageable =
                            Pageable(
                                offset = page * size,
                                pageNumber = allProductContents.size / size,
                                pageSize = size,
                                paged = true,
                                sort = fixedSort,
                                unpaged = false,
                            ),
                        size = productContents.size,
                        sort = fixedSort,
                        totalElements = 0,
                        totalPages = 0,
                    ),
                ),
            ).build()
    }

    private fun requestProduct(productId: Long): MockResponse {
        val productContent = allProductContents.find { it.id == productId }
        if (productContent == null) return response400
        return MockResponse
            .Builder()
            .code(200)
            .body(
                json.encodeToString(
                    ProductResponse(
                        category = productContent.category,
                        id = productContent.id,
                        imageUrl = productContent.imageUrl,
                        name = productContent.name,
                        price = productContent.price,
                    ),
                ),
            ).build()
    }

    private val response500 = MockResponse.Builder().code(500).build()
    private val response400 = MockResponse.Builder().code(404).build()

    private val fixedSort =
        Sort(
            empty = true,
            sorted = true,
            unsorted = true,
        )
}
