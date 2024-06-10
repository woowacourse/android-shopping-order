package woowacourse.shopping.domain

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime

sealed class Coupon(
    val id: Int,
    val code: String,
    val description: String,
    val expirationDate: LocalDate,
    val discountType: DiscountType,
    val isChecked: Boolean = false,
) {
    fun isValidPeriod(nowDate: LocalDate): Boolean {
        val duration = Duration.between(expirationDate.atStartOfDay(), nowDate.atStartOfDay())
        return (duration.isNegative)
    }

    abstract fun calculateDiscount(
        products: List<ProductListItem.ShoppingProductItem>,
        now: LocalDateTime,
    ): Long

    abstract fun updateCheck(isChecked: Boolean): Coupon
}

class FixedCoupon(
    id: Int,
    code: String,
    description: String,
    expirationDate: LocalDate,
    discountType: DiscountType,
    isChecked: Boolean,
    val discount: Int,
    val minimumAmount: Int,
) : Coupon(id, code, description, expirationDate, discountType, isChecked) {
    private fun isValid(
        totalPrice: Long,
        now: LocalDateTime,
    ) = (minimumAmount <= totalPrice) && isValidPeriod(now.toLocalDate())

    override fun calculateDiscount(
        products: List<ProductListItem.ShoppingProductItem>,
        now: LocalDateTime,
    ): Long {
        val totalPrice = products.sumOf { it.price * it.quantity }
        return if (isValid(totalPrice, now)) {
            discount.toLong()
        } else {
            0
        }
    }

    override fun updateCheck(isChecked: Boolean): Coupon {
        return FixedCoupon(
            id = this.id,
            code = this.code,
            description = this.description,
            expirationDate = this.expirationDate,
            discountType = this.discountType,
            isChecked = isChecked,
            discount = this.discount,
            minimumAmount = this.minimumAmount,
        )
    }
}

class BuyXGetYCoupon(
    id: Int,
    code: String,
    description: String,
    expirationDate: LocalDate,
    discountType: DiscountType,
    isChecked: Boolean,
    val buyQuantity: Int,
    val getQuantity: Int,
) : Coupon(id, code, description, expirationDate, discountType, isChecked) {
    private fun isValid(
        products: List<ProductListItem.ShoppingProductItem>,
        now: LocalDateTime,
    ): Boolean = (products.any { isDiscountableQuantity(it) }) && isValidPeriod(now.toLocalDate())

    override fun calculateDiscount(
        products: List<ProductListItem.ShoppingProductItem>,
        now: LocalDateTime,
    ): Long {
        return if (isValid(products, now)) {
            val discountableProducts = products.filter { isDiscountableQuantity(it) }
            val highestPriceProduct = discountableProducts.maxBy { it.price }
            highestPriceProduct.price * getQuantity
        } else {
            0
        }
    }

    private fun isDiscountableQuantity(it: ProductListItem.ShoppingProductItem) = it.quantity == (buyQuantity + getQuantity)

    override fun updateCheck(isChecked: Boolean): Coupon {
        return BuyXGetYCoupon(
            id = this.id,
            code = this.code,
            description = this.description,
            expirationDate = this.expirationDate,
            discountType = this.discountType,
            isChecked = isChecked,
            buyQuantity = this.buyQuantity,
            getQuantity = this.getQuantity,
        )
    }
}

class FreeShippingCoupon(
    id: Int,
    code: String,
    description: String,
    expirationDate: LocalDate,
    discountType: DiscountType,
    isChecked: Boolean,
) : Coupon(id, code, description, expirationDate, discountType, isChecked) {
    private fun isValid(now: LocalDateTime) = isValidPeriod(now.toLocalDate())

    override fun calculateDiscount(
        products: List<ProductListItem.ShoppingProductItem>,
        now: LocalDateTime,
    ): Long {
        return if (isValid(now)) {
            3_000
        } else {
            0
        }
    }

    override fun updateCheck(isChecked: Boolean): Coupon =
        FreeShippingCoupon(
            id = this.id,
            code = this.code,
            description = this.description,
            expirationDate = this.expirationDate,
            discountType = this.discountType,
            isChecked = isChecked,
        )
}

class PercentageCoupon(
    id: Int,
    code: String,
    description: String,
    expirationDate: LocalDate,
    discountType: DiscountType,
    isChecked: Boolean,
    val discount: Int,
    val availableTime: AvailableTime,
) : Coupon(id, code, description, expirationDate, discountType, isChecked) {
    private fun isValid(now: LocalDateTime): Boolean {
        val time = now.toLocalTime()
        val date = now.toLocalDate()
        return time.isAfter(availableTime.start) &&
            time.isBefore(availableTime.end) &&
            isValidPeriod(date)
    }

    override fun calculateDiscount(
        products: List<ProductListItem.ShoppingProductItem>,
        now: LocalDateTime,
    ): Long {
        val totalPrice = products.sumOf { it.price * it.quantity }
        return if (isValid(now)) {
            (totalPrice * discount) / 100
        } else {
            0
        }
    }

    override fun updateCheck(isChecked: Boolean): Coupon =
        PercentageCoupon(
            id = this.id,
            code = this.code,
            description = this.description,
            expirationDate = this.expirationDate,
            discountType = this.discountType,
            isChecked = isChecked,
            discount = this.discount,
            availableTime = this.availableTime,
        )
}
