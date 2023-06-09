package woowacourse.shopping.data.entity

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class DiscountsEntity(
    val discountInformation: List<DiscountEntity>
)
