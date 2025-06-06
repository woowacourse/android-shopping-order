package woowacourse.shopping.domain.model

import java.time.LocalDate

@JvmInline
value class Coupons(
    val value: List<Coupon>,
) {
    fun selectCoupon(couponId: Int): Coupons {
        val selectedCoupon = value.find { it.detail.id == couponId } ?: return this

        return Coupons(
            value.map {
                when {
                    it.detail.id == couponId -> it.copy(isSelected = !selectedCoupon.isSelected)
                    else -> it.copy(isSelected = false)
                }
            },
        )
    }

    fun applyCoupon(products: Products): Price =
        value.find { it.isSelected }?.apply(products)
            ?: Price(products.selectedProductsPrice)

    fun filterAvailableCoupons(
        products: Products,
        nowDate: LocalDate,
    ): Coupons =
        Coupons(
            value.filter { coupon ->
                coupon.getIsAvailable(products, nowDate)
            },
        )

    companion object {
        val EMPTY_COUPONS: Coupons = Coupons(emptyList())
    }
}
