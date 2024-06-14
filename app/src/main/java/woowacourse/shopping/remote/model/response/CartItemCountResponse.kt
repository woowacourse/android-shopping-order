package woowacourse.shopping.remote.model.response

import com.squareup.moshi.Json

data class CartItemCountResponse(
    @Json(name = "quantity") val quantity: Int,
)
