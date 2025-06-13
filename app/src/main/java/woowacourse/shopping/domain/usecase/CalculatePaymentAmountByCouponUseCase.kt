package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CouponRepository
import java.time.LocalTime

class CalculatePaymentAmountByCouponUseCase(
    private val couponRepository: CouponRepository,
) {
    operator fun invoke(
        couponId: Long,
        products: Products,
        now: LocalTime = LocalTime.now(),
    ): Int {
        val orderAmount: Int =
            products.getSelectedCartRecommendProductsPrice()
        val totalPaymentAmount = orderAmount + DEFAULT_SHIPPING_FEE
        val selectedCoupon = couponRepository.fetchCoupon(couponId) ?: return totalPaymentAmount
        if (!selectedCoupon.isAvailable(products, now)) return totalPaymentAmount

        return when (selectedCoupon) {
            is Coupon.FixedDiscount -> {
                totalPaymentAmount - selectedCoupon.discount
            }

            is Coupon.BuyXGetYFree -> {
                val maximumPrice = products.products.maxOf { it.productDetail.price }
                totalPaymentAmount - (maximumPrice * selectedCoupon.getQuantity)
            }

            is Coupon.FreeShippingOver -> {
                totalPaymentAmount - DEFAULT_SHIPPING_FEE
            }

            is Coupon.PercentDiscount -> {
                val discount = orderAmount * selectedCoupon.discount / ONE_HUNDRED_PERCENT
                totalPaymentAmount - discount
            }
        }
    }

    companion object {
        const val DEFAULT_SHIPPING_FEE: Int = 3000
        const val ONE_HUNDRED_PERCENT: Int = 100
    }
}
