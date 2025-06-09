package woowacourse.shopping.domain.usecase

import android.util.Log
import woowacourse.shopping.domain.model.CouponDetailInfo
import woowacourse.shopping.domain.model.CouponDetailInfo.Companion.isAvailable
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
    ): Int? {
        val orderAmount: Int =
            products.getSelectedCartProductsPrice() + products.getSelectedCartRecommendProductsPrice()
        val totalPaymentAmount = orderAmount + DEFAULT_SHIPPING_FEE
        val selectedCoupon = couponRepository.fetchCoupon(couponId) ?: return null
        if (!selectedCoupon.isAvailable(products, now)) return null

        return when (selectedCoupon) {
            is CouponDetailInfo.FixedDiscount -> {
                totalPaymentAmount - selectedCoupon.discount
            }

            is CouponDetailInfo.BuyXGetYFree -> {
                val maximumPrice = products.products.maxOf { it.productDetail.price }
                Log.d("TAG", "invoke: ${products.products}")
                totalPaymentAmount - (maximumPrice * selectedCoupon.getQuantity)
            }

            is CouponDetailInfo.FreeShippingOver -> {
                totalPaymentAmount - DEFAULT_SHIPPING_FEE
            }

            is CouponDetailInfo.PercentDiscount -> {
                val discount = orderAmount * selectedCoupon.discount / 100
                totalPaymentAmount - discount
            }
        }
    }

    companion object {
        const val DEFAULT_SHIPPING_FEE: Int = 3000
    }
}
