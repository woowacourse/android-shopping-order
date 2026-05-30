package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.PaymentItems
import woowacourse.shopping.domain.model.PriceModifiers

class CalculateOrderPricingUseCase {
    operator fun invoke(
        coupon: Coupon?,
        items: PaymentItems,
    ): PriceModifiers {
        val orderAmount = items.totalPrice.amount

        return when (coupon) {
            null ->
                PriceModifiers(
                    orderAmount = orderAmount,
                    discountAmount = 0L,
                    deliveryFee = DEFAULT_DELIVERY_FEE,
                )

            is Coupon.FreeShipping ->
                PriceModifiers(
                    orderAmount = orderAmount,
                    discountAmount = 0L,
                    deliveryFee = 0,
                )

            else ->
                PriceModifiers(
                    orderAmount = items.totalPrice.amount,
                    discountAmount = coupon.discountAmount(items).amount,
                    deliveryFee = DEFAULT_DELIVERY_FEE,
                )
        }
    }

    companion object {
        const val DEFAULT_DELIVERY_FEE = 3000
    }
}
