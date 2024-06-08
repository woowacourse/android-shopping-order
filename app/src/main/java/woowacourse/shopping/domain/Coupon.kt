package woowacourse.shopping.domain

import woowacourse.shopping.data.dto.response.AvailableTime
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime

sealed class Coupon(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discountType: DiscountType,
) {
    protected fun isValidPeriod(nowDate: LocalDate): Boolean {
        val duration = Duration.between(expirationDate.atStartOfDay(), nowDate.atStartOfDay())
        return (duration.isNegative)
    }

    abstract fun calculateDiscount(products: List<ProductListItem.ShoppingProductItem>): Long
}

class FixedCoupon(
    id: Int,
    code: String,
    description: String,
    expirationDate: LocalDate,
    discountType: DiscountType,
    val discount: Int,
    val minimumAmount: Int,
) : Coupon(id, code, description, expirationDate, discountType) {
    private fun isValid(totalPrice: Long) = (minimumAmount <= totalPrice) && isValidPeriod(LocalDate.now())

    override fun calculateDiscount(products: List<ProductListItem.ShoppingProductItem>): Long {
        val totalPrice = products.sumOf { it.price * it.quantity }
        return if (isValid(totalPrice)) {
            discount.toLong()
        } else {
            0
        }
    }
}

class BuyXGetYCoupon(
    id: Int,
    code: String,
    description: String,
    expirationDate: LocalDate,
    discountType: DiscountType,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon(id, code, description, expirationDate, discountType) {
    private fun isValid(products: List<ProductListItem.ShoppingProductItem>): Boolean =
        (products.any { it.quantity == buyQuantity }) && isValidPeriod(LocalDate.now())

    override fun calculateDiscount(products: List<ProductListItem.ShoppingProductItem>): Long {
        return if (isValid(products)) {
            val discountableProducts = products.filter { it.quantity == buyQuantity }
            val highestPriceProduct = discountableProducts.maxBy { it.price }
            highestPriceProduct.price * getQuantity
        } else {
            0
        }
    }
}

class FreeShippingCoupon(
    id: Int,
    code: String,
    description: String,
    expirationDate: LocalDate,
    discountType: DiscountType,
) : Coupon(id, code, description, expirationDate, discountType) {
    private fun isValid() = isValidPeriod(LocalDate.now())

    override fun calculateDiscount(products: List<ProductListItem.ShoppingProductItem>): Long {
        return if (isValid()) {
            3_000
        } else {
            0
        }
    }
}

class PercentageCoupon(
    id: Int,
    code: String,
    description: String,
    expirationDate: LocalDate,
    discountType: DiscountType,
    val discount: Int,
    val availableTime: AvailableTime,
) : Coupon(id, code, description, expirationDate, discountType) {
    private fun isValid(nowTime: LocalTime): Boolean =
        nowTime.isAfter(availableTime.start) && nowTime.isBefore(availableTime.end) &&
            isValidPeriod(
                LocalDate.now(),
            )

    override fun calculateDiscount(products: List<ProductListItem.ShoppingProductItem>): Long {
        val totalPrice = products.sumOf { it.price }
        return if (isValid(LocalTime.now())) {
            (totalPrice * discount) / 100
        } else {
            0
        }
    }
}
