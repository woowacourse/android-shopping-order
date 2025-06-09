package woowacourse.shopping.domain.model.coupon

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.PaymentSummary
import java.time.LocalDate
import java.time.LocalDateTime

data class BuyXGetYCoupon(
    override val id: Long,
    override val code: String,
    override val description: String,
    override val expirationDate: LocalDate,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon() {
    override fun calculateDiscountAmount(
        paymentSummary: PaymentSummary,
        dateTime: LocalDateTime,
    ): PaymentSummary {
        if (!isAvailable(dateTime, paymentSummary)) return paymentSummary

        val filteredProducts = paymentSummary.products.filterApplicableProducts()
        val mostExpensiveProduct = filteredProducts.maxBy { it.product.price.value }
        val discountAmount = mostExpensiveProduct.product.price.value
        return paymentSummary.copy(discountPrice = discountAmount)
    }

    override fun isAvailable(
        dateTime: LocalDateTime,
        paymentSummary: PaymentSummary,
    ): Boolean {
        val filteredProducts = paymentSummary.products.filterApplicableProducts()
        val isApplicable = filteredProducts.isNotEmpty()

        return super.isAvailable(dateTime, paymentSummary) && isApplicable
    }

    private fun List<CartProduct>.filterApplicableProducts(): List<CartProduct> = this.filter { it.quantity >= (buyQuantity + getQuantity) }
}
