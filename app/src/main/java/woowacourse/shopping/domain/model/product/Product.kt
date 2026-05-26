package woowacourse.shopping.domain.model.product

import woowacourse.shopping.domain.model.common.Money

data class Product(
    val id: Long,
    val name: String,
    val price: Money,
    val imageUrl: String,
    val category: String = "",
)
