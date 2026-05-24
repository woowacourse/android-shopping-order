package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.order.Order
import java.time.LocalDate

data class NplusMFreeCoupon(
    override val code: String,
    override val name: String,
    override val expirationDate: LocalDate,
    val purchaseQuantity: Int,
    val freeQuantity: Int,
) : Coupon(
    code = code,
    name = name,
    expirationDate = expirationDate,
) {
    init {
        require(purchaseQuantity > 0) { "구매 수량은 1개 이상이어야 합니다." }
        require(freeQuantity > 0) { "무료 제공 수량은 1개 이상이어야 합니다." }
    }

    override fun discountAmount(
        order: Order,
        context: CouponContext,
    ): Int =
        order.purchaseProducts.purchaseProducts
            .maxOfOrNull { purchaseProduct ->
                val freeCount = (purchaseProduct.count / (purchaseQuantity + freeQuantity)) * freeQuantity
                purchaseProduct.price * freeCount
            } ?: 0
}
