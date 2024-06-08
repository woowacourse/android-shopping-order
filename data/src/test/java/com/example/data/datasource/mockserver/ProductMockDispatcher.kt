package com.example.data.datasource.mockserver

import com.example.data.datasource.mockserver.MockWebServerPath.CONTENT_KEY
import com.example.data.datasource.mockserver.MockWebServerPath.CONTENT_TYPE
import com.example.data.datasource.mockserver.MockWebServerPath.PRODUCTS
import com.example.data.datasource.remote.retrofit.model.response.Sort
import com.example.data.datasource.remote.retrofit.model.response.product.Pageable
import com.example.data.datasource.remote.retrofit.model.response.product.ProductResponse
import com.example.data.datasource.remote.retrofit.model.response.product.toProductContent
import com.example.data.datasource.remote.retrofit.model.response.product.toProductContents
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest
import woowacourse.shopping.data.dummy.dummyProductList

class ProductMockDispatcher() : Dispatcher() {
    override fun dispatch(request: RecordedRequest): MockResponse {
        val url = request.requestUrl ?: return makeErrorResponse()
        val segments = url.pathSegments
        when (segments[0]) {
            PRODUCTS -> {
                if (segments.size == 1) {
                    val page = url.queryParameter("page")?.toInt() ?: 0
                    val size = url.queryParameter("size")?.toInt() ?: 0
                    return getProductsResponse(page, size)
                }
                println(segments[1])
                return getProductResponse(segments[1].toInt())
            }
        }
        return makeErrorResponse()
    }

    private fun getProductResponse(productId: Int): MockResponse {
        val product =
            dummyProductList.find { it.id == productId }?.toProductContent()
                ?: return makeErrorResponse()
        return makeSuccessResponse(product)
    }

    private fun getProductsResponse(
        page: Int,
        size: Int,
    ): MockResponse {
        val list = dummyProductList.subList(page, size).toList().toProductContents()
        val first = page == 0
        val last = page * size + size > dummyProductList.size
        val sort = Sort(false, false, false)
        val pageable = Pageable(page, size, size, true, sort, false)
        val productResponse =
            ProductResponse(
                list,
                false,
                first,
                last,
                size,
                size,
                pageable,
                size,
                sort,
                dummyProductList.size,
                dummyProductList.size % size + 1,
            )
        return makeSuccessResponse(productResponse)
    }

    private inline fun <reified T> makeSuccessResponse(body: T): MockResponse {
        return MockResponse()
            .setHeader(CONTENT_TYPE, CONTENT_KEY)
            .setResponseCode(200)
            .setBody(Json.encodeToString(body))
    }

    private fun makeErrorResponse(): MockResponse {
        return MockResponse().setResponseCode(404)
    }
}
