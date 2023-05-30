package woowacourse.shopping.data.entity

import com.squareup.moshi.JsonClass
import woowacourse.shopping.domain.order.DiscountPolicy

@JsonClass(generateAdapter = true)
data class DiscountEntity(
    val policyName: String,
    val discountRate: Double,
    val discountPrice: Int
) {
    companion object {
        fun DiscountPolicy.toEntity() = DiscountEntity(
            name, discountRate, discountPrice
        )

        fun DiscountEntity.toDomain() = DiscountPolicy(
            policyName, discountRate, discountPrice
        )
    }
}
