package woowacourse.shopping.remote.model.response

import com.squareup.moshi.Json

data class CartResponse(
    @Json(name = "id") val id: Int,
    @Json(name = "quantity") val quantity: Int,
    @Json(name = "product") val productResponse: ProductResponse,
)
