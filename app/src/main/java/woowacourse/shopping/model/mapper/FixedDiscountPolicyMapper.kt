package woowacourse.shopping.model.mapper

import com.example.domain.FixedDiscountPolicy
import woowacourse.shopping.model.order.FixedDiscountPolicyState

fun FixedDiscountPolicy.toUi(): FixedDiscountPolicyState {
    return FixedDiscountPolicyState(
        minimumPrice = minimumPrice,
        discountPrice = discountPrice
    )
}

fun FixedDiscountPolicyState.toDomain(): FixedDiscountPolicy {
    return FixedDiscountPolicy(
        minimumPrice = minimumPrice,
        discountPrice = discountPrice
    )
}
