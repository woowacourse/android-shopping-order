package woowacourse.shopping.model

import kotlin.random.Random

data class Product(
    val id: ProductId = ProductId(Random.nextLong(1L, Long.MAX_VALUE)),
    val name: String,
    val price: Money,
    val imageUrl: String,
)
