package woowacourse.shopping.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class QuantityEntity(
    val quantity: Int
)
