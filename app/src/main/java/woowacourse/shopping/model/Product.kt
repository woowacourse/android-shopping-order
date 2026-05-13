package woowacourse.shopping.model

import kotlin.random.Random

data class Product(
    val id: Long,
    val name: String,
    val price: Money,
    val imageUrl: String,
    val category: String = "",
)
