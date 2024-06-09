package woowacourse.shopping.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Parcelize
sealed class Coupon(
    open val id: Int,
    open val code: String,
    open val description: String,
    open val discountType: String,
    open val expirationDate: String,
    open val discount: Int? = null,
    open val minimumAmount: Int? = null,
    open val buyQuantity: Int? = null,
    open val getQuantity: Int? = null,
    open val availableTimeStart: String? = null,
    open val availableTimeEnd: String? = null,
) : Parcelable {
    abstract fun isValid(cartProducts: List<CartProduct>): Boolean

    abstract fun getPriceDiscount(cartProducts: List<CartProduct>): Int

    abstract fun getDeliveryDiscount(cartProducts: List<CartProduct>): Int

    class Fixed5000(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val discountType: String,
        override val expirationDate: String,
        override val discount: Int,
        override val minimumAmount: Int,
    ) : Coupon(
            id,
            code,
            description,
            discountType,
            expirationDate,
        ) {
        override fun isValid(cartProducts: List<CartProduct>): Boolean {
            val currentDate = LocalDate.now()
            val expirationDate = LocalDate.parse(expirationDate, DateTimeFormatter.ISO_DATE)

            return currentDate <= expirationDate && cartProducts.sumOf { it.quantity * it.price } >= minimumAmount
        }

        override fun getPriceDiscount(cartProducts: List<CartProduct>): Int {
            return 5_000
        }

        override fun getDeliveryDiscount(cartProducts: List<CartProduct>): Int {
            return 0
        }
    }

    class Bogo(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        override val buyQuantity: Int,
        override val getQuantity: Int,
        override val discountType: String,
    ) : Coupon(
            id,
            code,
            description,
            discountType,
            expirationDate,
        ) {
        override fun isValid(cartProducts: List<CartProduct>): Boolean {
            val currentDate = LocalDate.now()
            val expirationDate = LocalDate.parse(expirationDate, DateTimeFormatter.ISO_DATE)

            return currentDate <= expirationDate && cartProducts.any { it.quantity >= buyQuantity }
        }

        override fun getPriceDiscount(cartProducts: List<CartProduct>): Int {
            return cartProducts.filter { it.quantity >= buyQuantity }.maxOf { it.price }.toInt() * getQuantity
        }

        override fun getDeliveryDiscount(cartProducts: List<CartProduct>): Int {
            return 0
        }
    }

    class FreeShipping(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        override val minimumAmount: Int,
        override val discountType: String,
    ) : Coupon(
            id,
            code,
            description,
            discountType,
            expirationDate,
        ) {
        override fun isValid(cartProducts: List<CartProduct>): Boolean {
            val currentDate = LocalDate.now()
            val expirationDate = LocalDate.parse(expirationDate, DateTimeFormatter.ISO_DATE)

            return currentDate <= expirationDate && cartProducts.sumOf { it.quantity * it.price } >= minimumAmount
        }

        override fun getPriceDiscount(cartProducts: List<CartProduct>): Int {
            return 0
        }

        override fun getDeliveryDiscount(cartProducts: List<CartProduct>): Int {
            return 3_000
        }
    }

    class MiracleSale(
        override val id: Int,
        override val code: String,
        override val description: String,
        override val expirationDate: String,
        override val discount: Int,
        override val availableTimeStart: String,
        override val availableTimeEnd: String,
        override val discountType: String,
    ) : Coupon(id, code, description, discountType, expirationDate) {
        override fun isValid(cartProducts: List<CartProduct>): Boolean {
            val currentTime = LocalTime.now()
            val startTime = LocalTime.parse(availableTimeStart, DateTimeFormatter.ISO_TIME)
            val endTime = LocalTime.parse(availableTimeEnd, DateTimeFormatter.ISO_TIME)
            return currentTime.isAfter(startTime) && currentTime.isBefore(endTime)
        }

        override fun getPriceDiscount(cartProducts: List<CartProduct>): Int {
            return (cartProducts.sumOf { it.price * it.quantity } * 0.3).toInt()
        }

        override fun getDeliveryDiscount(cartProducts: List<CartProduct>): Int {
            return 0
        }
    }

    class Unknown(override val id: Int, override val code: String, override val description: String, override val expirationDate: String, override val discountType: String) : Coupon(
        id,
        code,
        description,
        discountType,
        expirationDate,
    ) {
        override fun isValid(cartProducts: List<CartProduct>): Boolean {
            return false
        }

        override fun getPriceDiscount(cartProducts: List<CartProduct>): Int {
            return 0
        }

        override fun getDeliveryDiscount(cartProducts: List<CartProduct>): Int {
            return 0
        }
    }
}
