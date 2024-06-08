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
    val isChecked: Boolean = false,
) {
    protected fun isValidPeriod(nowDate: LocalDate): Boolean {
        val duration = Duration.between(expirationDate.atStartOfDay(), nowDate.atStartOfDay())
        return (duration.isNegative)
    }

    abstract fun calculateDiscount(products: List<ProductListItem.ShoppingProductItem>): Long

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
    private fun isValid(totalPrice: Long) = (minimumAmount <= totalPrice) && isValidPeriod(LocalDate.now())

    override fun calculateDiscount(products: List<ProductListItem.ShoppingProductItem>): Long {
        val totalPrice = products.sumOf { it.price * it.quantity }
        return if (isValid(totalPrice)) {
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
    private fun isValid() = isValidPeriod(LocalDate.now())

    override fun calculateDiscount(products: List<ProductListItem.ShoppingProductItem>): Long {
        return if (isValid()) {
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
