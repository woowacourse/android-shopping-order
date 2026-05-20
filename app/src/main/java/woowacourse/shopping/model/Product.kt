package woowacourse.shopping.model

import java.util.concurrent.atomic.AtomicLong

data class Product(
    val id: Long = counter.getAndIncrement(),
    val name: String,
    val price: Money,
    val imageUrl: String,
    val category: String = "",
) {
    companion object {
        val counter = AtomicLong(0)
    }
}
