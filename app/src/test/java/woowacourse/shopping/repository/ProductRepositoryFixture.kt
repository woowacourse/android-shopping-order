package woowacourse.shopping.repository

import woowacourse.shopping.domain.model.common.Money
import woowacourse.shopping.domain.model.product.Product

object ProductRepositoryFixture {
    val products: List<Product> =
        List(24) { index ->
            val category =
                when (index % 3) {
                    0 -> "dessert"
                    1 -> "fruit"
                    else -> "snack"
                }
            Product(
                id = ((index + 1).toLong()),
                name = "상품${index + 1}",
                price = Money(10_000 + index * 1_000),
                imageUrl = "https://example.com/product-${index + 1}.png",
                category = category,
            )
        }
}
