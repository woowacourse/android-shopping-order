package woowacourse.shopping.model.mapper

import com.example.domain.FixedDiscountPolicyUnit
import woowacourse.shopping.model.order.FixedDiscountPolicyUnitState

fun FixedDiscountPolicyUnit.toUi(): FixedDiscountPolicyUnitState {
    return FixedDiscountPolicyUnitState(
        minimumPrice = minimumPrice,
        discountPrice = discountPrice
    )
}

fun FixedDiscountPolicyUnitState.toDomain(): FixedDiscountPolicyUnit {
    return FixedDiscountPolicyUnit(
        minimumPrice = minimumPrice,
        discountPrice = discountPrice
    )
}
