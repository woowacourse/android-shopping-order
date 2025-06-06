package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Price.Companion.DEFAULT_SHIPPING_PRICE
import woowacourse.shopping.domain.model.Product.Companion.MINIMUM_QUANTITY
import java.time.LocalDateTime

class QuantityBonusCoupon(
    override val detail: CouponDetail,
    override val isSelected: Boolean = false,
) : Coupon {
    override fun apply(products: Products): Price {
        val original = products.selectedProductsPrice
        if (!getIsAvailable(products)) return Price(original = original)

        val targetProduct =
            products.products
                .filter { it.isSelected && it.quantity >= getRequiredTotalQuantity() }
                .maxByOrNull { it.productDetail.price }
                ?: return Price(original = original)

        val productPrice = targetProduct.productDetail.price

        val discount =
            with(detail) {
                if (buyQuantity == null || getQuantity == null) return Price(original = original)
                val quantityForDiscount = targetProduct.quantity / (buyQuantity + getQuantity) * getQuantity
                productPrice * quantityForDiscount
            }

        return Price(
            original = original,
            discount = discount,
            shipping = DEFAULT_SHIPPING_PRICE,
        )
    }

    override fun getIsAvailable(
        products: Products,
        nowDateTime: LocalDateTime,
    ): Boolean {
        val isValidQuantity =
            products.products.any {
                it.isSelected && it.quantity >= getRequiredTotalQuantity()
            }
        val isDateOkay = detail.expirationDate >= nowDateTime.toLocalDate()

        return isValidQuantity && isDateOkay
    }

    private fun getRequiredTotalQuantity(): Int {
        val buy = detail.buyQuantity ?: MINIMUM_QUANTITY
        val get = detail.getQuantity ?: MINIMUM_QUANTITY
        return buy + get
    }

    override fun copy(
        detail: CouponDetail,
        isSelected: Boolean,
    ): Coupon = QuantityBonusCoupon(detail, isSelected)
}
