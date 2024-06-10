package com.example.data.datasource.mockserver

import com.example.data.datasource.mockserver.MockWebServerPath.CART_ITEMS
import com.example.data.datasource.remote.model.response.Pageable
import com.example.data.datasource.remote.model.response.Sort
import com.example.data.datasource.remote.model.response.cart.CartResponse
import com.example.data.datasource.remote.model.response.cart.toCartContent
import com.example.data.datasource.remote.model.response.cart.toCartContents
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import woowacourse.shopping.data.dummy.dummyProductList

class CartMockDispatcher : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        val url = request.requestUrl ?: return makeErrorResponse()
        val segments = url.pathSegments
        when (segments[0]) {
            CART_ITEMS -> {
                if (segments.size == 1) {
                    val page = url.queryParameter("page")?.toInt() ?: 0
                    val size = url.queryParameter("size")?.toInt() ?: 0
                    return getCartItemsResponse(page, size)
                }
                return getProductResponse(segments[1].toInt())
            }
        }
        return makeErrorResponse()
    }

    private fun getProductResponse(productId: Int): MockResponse {
        val cartContent =
            dummyCartItems.find { it.id == productId }?.toCartContent()
                ?: return makeErrorResponse()
        return makeSuccessResponse(cartContent)
    }

    private fun getCartItemsResponse(
        page: Int,
        size: Int,
    ): MockResponse {
        val list = dummyCartItems.subList(page, size).toList().toCartContents()
        val first = page == 0
        val last = page * size + size > dummyProductList.size
        val sort = Sort(false, false, false)
        val pageable = Pageable(page, size, size, true, sort, false)
        val cartResponse =
            CartResponse(
                list,
                false,
                first,
                last,
                size,
                size,
                pageable,
                size,
                sort,
                dummyCartItems.size,
                dummyCartItems.size % size + 1,
            )
        return makeSuccessResponse(cartResponse)
    }

    private inline fun <reified T> makeSuccessResponse(body: T): MockResponse {
        return MockResponse()
            .setHeader(MockWebServerPath.CONTENT_TYPE, MockWebServerPath.CONTENT_KEY)
            .setResponseCode(200)
            .setBody(Json.encodeToString(body))
    }

    private fun makeErrorResponse(): MockResponse {
        return MockResponse().setResponseCode(404)
    }
}
