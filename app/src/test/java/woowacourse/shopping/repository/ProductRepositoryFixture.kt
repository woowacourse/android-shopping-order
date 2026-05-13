package woowacourse.shopping.repository

import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductId

object ProductRepositoryFixture {
    val products: List<Product> =
        List(24) { index ->
            Product(
                id = ProductId((index + 1).toLong()),
                name = "상품${index + 1}",
                price = Money(10_000 + index * 1_000),
                imageUrl = "https://example.com/product-${index + 1}.png",
            )
        }
}
