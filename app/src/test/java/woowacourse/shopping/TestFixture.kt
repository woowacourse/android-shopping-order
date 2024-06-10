package woowacourse.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.Coupon
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.view.cart.model.ShoppingCart
import java.time.LocalDate
import java.time.LocalTime
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

object TestFixture {
    private const val TIME_OUT_MESSAGE = "LiveData value was never set."
    private const val UNCHECKED_CAST = "UNCHECKED_CAST"

    fun <T> LiveData<T>.getOrAwaitValue(
        time: Long = 2,
        timeUnit: TimeUnit = TimeUnit.SECONDS,
    ): T {
        var data: T? = null
        val latch = CountDownLatch(1)
        val observer =
            object : Observer<T> {
                override fun onChanged(value: T) {
                    data = value
                    latch.countDown()
                    this@getOrAwaitValue.removeObserver(this)
                }
            }

        this.observeForever(observer)

        if (!latch.await(time, timeUnit)) {
            throw TimeoutException(TIME_OUT_MESSAGE)
        }

        @Suppress(UNCHECKED_CAST)
        return data as T
    }

    val cartItem0 =
        CartItem(
            id = 0L,
            product =
                Product(
                    id = 0L,
                    imageUrl =
                        "https://images.emarteveryday.co.kr/images/product/8801392067167/vSYMPCA3qqbLJjhv.png",
                    price = 30_000,
                    name = "PET보틀-단지(400ml) 레몬청",
                    category = "",
                    cartItemCounter = CartItemCounter(3),
                ),
        )

    val cartItem1 =
        CartItem(
            id = 1L,
            product =
                Product(
                    id = 1L,
                    imageUrl =
                        "https://images.emarteveryday.co.kr/images/product/8801392067167/vSYMPCA3qqbLJjhv.png",
                    price = 12_000,
                    name = "PET보틀-납작(2000ml) 밀크티",
                    category = "",
                    cartItemCounter = CartItemCounter(2),
                ),
        )

    val cartItem2 =
        CartItem(
            id = 2L,
            product =
                Product(
                    id = 2L,
                    imageUrl =
                        "https://images.emarteveryday.co.kr/images/product/8801392067167/vSYMPCA3qqbLJjhv.png",
                    price = 12_000,
                    name = "PET보틀-밀크티(600ml)",
                    category = "",
                    cartItemCounter = CartItemCounter(1),
                ),
        )

    val newProduct =
        CartItem(
            id = 3L,
            product =
                Product(
                    id = 3L,
                    imageUrl =
                        "https://images.emarteveryday.co.kr/images/product/8801392067167/vSYMPCA3qqbLJjhv.png",
                    price = 12_000,
                    name = "PET보틀-밀크티(600ml)",
                    category = "",
                    cartItemCounter = CartItemCounter(1),
                ),
        )

    val shoppingCartItemsTotal126000 =
        ShoppingCart()
            .apply {
                addProduct(cartItem0)
                addProduct(cartItem1)
                addProduct(cartItem2)
            }

    val shoppingCartItemsTotal90000 =
        ShoppingCart()
            .apply {
                addProduct(cartItem0)
            }

    val shoppingCartItemsTotal24000 =
        ShoppingCart()
            .apply {
                addProduct(cartItem1)
            }

    val shoppingCartItemsTripleCartItem0 =
        ShoppingCart()
            .apply {
                addProduct(cartItem0)
            }

    val shoppingCartItemsNotBogo =
        ShoppingCart()
            .apply {
                addProduct(cartItem1)
            }

    val fixed5000DiscountCoupon =
        Coupon.FixedDiscountCoupon(
            id = 0L,
            code = "FIXED5000",
            description = "5000원 할인 쿠폰",
            expirationDate = LocalDate.now().plusDays(7),
            discountType = "FIXED",
            discount = 5000,
            minimumAmount = 100000,
        )

    val bogoCoupon =
        Coupon.BogoCoupon(
            id = 1L,
            code = "BOGO",
            description = "2+1 쿠폰",
            expirationDate = LocalDate.now().plusDays(7),
            discountType = "BOGO",
            buyQuantity = 2,
            getQuantity = 1,
        )

    val freeShippingCoupon =
        Coupon.FreeShippingCoupon(
            id = 2L,
            code = "FREESHIPPING",
            description = "무료배송 쿠폰",
            expirationDate = LocalDate.now().plusDays(7),
            discountType = "FREESHIPPING",
            minimumAmount = 50000,
        )

    val notNowTimeBasedDiscountCoupon =
        Coupon.TimeBasedDiscountCoupon(
            id = 3L,
            code = "TIMEBASED",
            description = "시간대별 할인 쿠폰",
            expirationDate = LocalDate.now().plusDays(7),
            discountType = "TIMEBASED",
            discount = 30,
            availableTimeStart = LocalTime.MIN,
            availableTimeEnd = LocalTime.now().minusHours(3),
        )

    val nowTimeBasedDiscountCoupon =
        Coupon.TimeBasedDiscountCoupon(
            id = 3L,
            code = "TIMEBASED",
            description = "시간대별 할인 쿠폰",
            expirationDate = LocalDate.now().plusDays(7),
            discountType = "TIMEBASED",
            discount = 30,
            availableTimeStart = LocalTime.MIN,
            availableTimeEnd = LocalTime.now().plusHours(3),
        )
}
