package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.cart.Receipt
import java.time.LocalDate
import java.time.LocalDateTime

class BoGoCoupon(
    override val couponId: Long,
    override val expirationDate: LocalDate,
    private val buyQuantity: Int,
    private val getQuantity: Int,
) : Coupon {

    override fun isAvailable(receipt: Receipt, current: LocalDateTime): Boolean =
        current.toLocalDate() <= expirationDate && receipt.hasEnoughItems(buyQuantity + getQuantity)

    override fun discountPrice(receipt: Receipt): Int {
        return receipt.findMostExpensiveItemPrice(buyQuantity + getQuantity) * getQuantity
    }
}

