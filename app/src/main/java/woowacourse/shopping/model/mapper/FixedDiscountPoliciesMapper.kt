package woowacourse.shopping.model.mapper

import com.example.domain.FixedDiscountPolicies
import woowacourse.shopping.model.order.FixedDiscountPoliciesState

fun FixedDiscountPolicies.toUi(): FixedDiscountPoliciesState {
    return FixedDiscountPoliciesState(
        fixedDiscountPolicies = fixedDiscountPolicies.map { it.toUi() }
    )
}

fun FixedDiscountPoliciesState.toDomain(): FixedDiscountPolicies {
    return FixedDiscountPolicies(
        fixedDiscountPolicies = fixedDiscountPolicies.map { it.toDomain() }
    )
}
