package woowacourse.shopping.domain.model

import woowacourse.shopping.domain.model.Price.Companion.MINIMUM_PRICE
import woowacourse.shopping.domain.model.Product.Companion.MINIMUM_QUANTITY

class QuantityBonusCoupon(
    override val detail: CouponDetail,
    override val isSelected: Boolean = false,
) : Coupon {
    override fun apply(products: Products): Price {
        val original = products.selectedProductsPrice
        val isAvailable = getIsAvailable(products)

        val discount =
            if (isAvailable) {
                products.products
                    .filter { it.isSelected && it.quantity >= getRequiredTotalQuantity() }
                    .maxByOrNull { it.productDetail.price }
                    ?.productDetail
                    ?.price ?: MINIMUM_PRICE
            } else {
                MINIMUM_PRICE
            }

        val shipping = Price.EMPTY_PRICE.shipping

        return Price(
            original = original,
            discount = discount,
            shipping = shipping,
        )
    }

    override fun getIsAvailable(products: Products): Boolean =
        products.products.any {
            it.isSelected && it.quantity >= getRequiredTotalQuantity()
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
