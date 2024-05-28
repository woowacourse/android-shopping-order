package woowacourse.shopping.data.product.server

import com.google.gson.Gson
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import woowacourse.shopping.data.product.server.ProductServerApiPath2.CONTENT_KEY
import woowacourse.shopping.data.product.server.ProductServerApiPath2.CONTENT_TYPE
import woowacourse.shopping.data.product.server.ProductServerApiPath2.FIND_PRODUCT_PAGE_PATH
import woowacourse.shopping.data.retrofit.ProductResponse
import kotlin.math.min

class MockWebProductServerDispatcher2(
    private val productResponses: List<ProductResponse>,
) : Dispatcher() {
    private val gson = Gson()

    override fun dispatch(request: RecordedRequest): MockResponse {
        val path = request.path ?: return makeErrorResponse()

        return when {
            path.startsWith(FIND_PRODUCT_PAGE_PATH) -> {
                val (page, pageSize) =
                    path.removePrefix(FIND_PRODUCT_PAGE_PATH)
                        .split("/")
                        .map { it.toInt() }

                val fromIndex = page * pageSize
                val toIndex = min(fromIndex + pageSize, productResponses.size)
                val pageProducts = productResponses.subList(fromIndex, toIndex)
                makeSuccessResponse(pageProducts)
            }
            else -> makeErrorResponse()
        }
    }

    private fun <T> makeSuccessResponse(body: T): MockResponse {
        return MockResponse()
            .setHeader(CONTENT_TYPE, CONTENT_KEY)
            .setResponseCode(200)
            .setBody(gson.toJson(body))
    }

    private fun makeErrorResponse(): MockResponse {
        return MockResponse().setResponseCode(404)
    }
}
