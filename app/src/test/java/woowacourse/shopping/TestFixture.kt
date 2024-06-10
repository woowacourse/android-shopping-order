package woowacourse.shopping

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.CartItemCounter
import woowacourse.shopping.domain.model.Product
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
                    price = 10_000,
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
}
