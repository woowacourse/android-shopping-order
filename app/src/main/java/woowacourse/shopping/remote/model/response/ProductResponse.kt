package woowacourse.shopping.remote.model.response

import com.squareup.moshi.Json

data class ProductResponse(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "price") val price: Int,
    @Json(name = "category") val category: String,
    @Json(name = "imageUrl") val imageUrl: String,
)
