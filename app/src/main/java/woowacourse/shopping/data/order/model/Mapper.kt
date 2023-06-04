package woowacourse.shopping.data.order.model

import com.example.domain.FixedDiscountPolicies
import com.example.domain.FixedDiscountPolicy
import com.example.domain.order.OrderSummary

fun FixedDiscountPolicyResponse.toDomain(): FixedDiscountPolicy {
    return FixedDiscountPolicy(
        minimumPrice = minimumPrice,
        discountPrice = discountPrice
    )
}

fun FixedDiscountPoliciesResponse.toDomain(): FixedDiscountPolicies {
    return FixedDiscountPolicies(
        fixedDiscountPolicies = fixedDiscountPolicies.map { it.toDomain() }
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
