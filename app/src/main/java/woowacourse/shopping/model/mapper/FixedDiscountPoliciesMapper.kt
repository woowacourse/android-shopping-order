package woowacourse.shopping.model.mapper

import com.example.domain.FixedDiscountPolicy
import woowacourse.shopping.model.order.FixedDiscountPolicyState

fun FixedDiscountPolicy.toUi(): FixedDiscountPolicyState {
    return FixedDiscountPolicyState(
        fixedDiscountPolicy = fixedDiscountPolicyUnits.map { it.toUi() }
    )
}

fun FixedDiscountPolicyState.toDomain(): FixedDiscountPolicy {
    return FixedDiscountPolicy(
        fixedDiscountPolicyUnits = fixedDiscountPolicy.map { it.toDomain() }
    )
}
