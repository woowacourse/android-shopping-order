package woowacourse.shopping.data.discount

import woowacourse.shopping.domain.DiscountPolicy
import woowacourse.shopping.domain.DiscountResult

data class DiscountResultDto(
    val policyName: String,
    val discountRate: Double,
    val discountPrice: Int
) {
    fun toDomain(): DiscountResult {
        return DiscountResult(
            mapDiscountPolicy(policyName),
            discountRate,
            discountPrice
        )
    }

    private fun mapDiscountPolicy(policyName: String): DiscountPolicy {
        return when (policyName) {
            "memberGradeDiscount" -> DiscountPolicy.MEMBER_GRADE_DISCOUNT
            "priceDiscount" -> DiscountPolicy.PRICE_DISCOUNT
            else -> throw IllegalArgumentException("${policyName}은 코드에 반영하지 않은 할인 정책입니다.")
        }
    }
}
