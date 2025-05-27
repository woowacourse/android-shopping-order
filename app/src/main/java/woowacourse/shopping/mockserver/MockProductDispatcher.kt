package woowacourse.shopping.mockserver

import com.google.gson.Gson
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import woowacourse.shopping.data.model.PageableResponse
import woowacourse.shopping.domain.model.Product

class MockProductDispatcher(
    private val products: List<Product>,
) : Dispatcher() {
    private val gson = Gson()

    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path ?: return MockResponse().setResponseCode(404)

        return when {
            path.startsWith("/product/") -> {
                val id = path.removePrefix("/product/").toLongOrNull()
                val product = products.find { it.id == id }

                if (product != null) {
                    MockResponse()
                        .setResponseCode(200)
                        .setBody(gson.toJson(product))
                } else {
                    MockResponse()
                        .setResponseCode(404)
                        .setBody("{\"error\": \"Product not found\"}")
                }
            }

            path.startsWith("/product?ids=") -> {
                val idListString = path.substringAfter("?ids=")
                val ids = gson.fromJson(idListString, Array<Long>::class.java).toList()
                val foundProducts = products.filter { it.id in ids }
                MockResponse().setResponseCode(200).setBody(gson.toJson(foundProducts))
            }

            path.startsWith("/products/") -> {
                val offset = path.substringAfter("offset=").substringBefore("&").toIntOrNull() ?: 0
                val limit = path.substringAfter("limit=").toIntOrNull() ?: 10
                val paged = products.drop(offset).take(limit)
                val hasMore = offset + limit < products.size
                val pagedWithHasMore = PageableResponse(paged, hasMore)
                MockResponse().setResponseCode(200).setBody(gson.toJson(pagedWithHasMore))
            }

            else -> MockResponse().setResponseCode(404).setBody("Not found")
        }
    }
}
