package woowacourse.shopping.data.order.model

import com.example.domain.FixedDiscountPolicy
import com.example.domain.FixedDiscountPolicyUnit
import com.example.domain.order.OrderSummary
import woowacourse.shopping.data.order.model.dto.response.FixedDiscountPolicyResponse
import woowacourse.shopping.data.order.model.dto.response.FixedDiscountPolicyUnitResponse
import woowacourse.shopping.data.order.model.dto.response.OrderSummaryResponse

fun FixedDiscountPolicyUnitResponse.toDomain(): FixedDiscountPolicyUnit {
    return FixedDiscountPolicyUnit(
        minimumPrice = minimumPrice,
        discountPrice = discountPrice
    )
}

fun FixedDiscountPolicyResponse.toDomain(): FixedDiscountPolicy {
    return FixedDiscountPolicy(
        fixedDiscountPolicyUnits = fixedDiscountPolicy.map { it.toDomain() }
    )
}

fun OrderSummaryResponse.toDomain(): OrderSummary {
    return OrderSummary(
        id = id,
        finalPrice = finalPrice,
        products = products,
        orderDate = orderDate
    )
}
