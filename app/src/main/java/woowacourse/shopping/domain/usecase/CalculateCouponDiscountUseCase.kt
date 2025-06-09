package woowacourse.shopping.domain.usecase

import woowacourse.shopping.domain.model.CouponDetailInfo
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.CouponRepository
import woowacourse.shopping.domain.usecase.CalculatePaymentAmountByCouponUseCase.Companion.DEFAULT_SHIPPING_FEE

class CalculateCouponDiscountUseCase(
    private val couponRepository: CouponRepository,
) {
    operator fun invoke(
        couponId: Long,
        products: Products,
    ): Int {
        val orderAmount: Int =
            products.getSelectedCartProductsPrice()
        val selectedCoupon = couponRepository.fetchCoupon(couponId) ?: return 0
        return when (selectedCoupon) {
            is CouponDetailInfo.FixedDiscount -> {
                -selectedCoupon.discount
            }

            is CouponDetailInfo.BuyXGetYFree -> {
                val maximumPrice = products.products.maxOf { it.productDetail.price }
                -(maximumPrice * selectedCoupon.getQuantity)
            }

            is CouponDetailInfo.FreeShippingOver -> {
                -DEFAULT_SHIPPING_FEE
            }

            is CouponDetailInfo.PercentDiscount -> {
                val discount = orderAmount * selectedCoupon.discount / 100
                -discount
            }
        }
    }
}
