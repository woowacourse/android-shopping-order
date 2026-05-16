package woowacourse.shopping.fixture

import woowacourse.shopping.domain.Money
import woowacourse.shopping.domain.Product

object TestProductFixture {

    const val DEFAULT_CATEGORY = "fashion"
    const val ANOTHER_CATEGORY = "food"

    fun product(
        id: String = "1",
        name: String = "품목 $id",
        price: Int = 1_000,
        imageUrl: String = "https://image/$id.png",
        category: String = DEFAULT_CATEGORY,
    ): Product = Product(
        name = name,
        price = Money(price),
        imageUrl = imageUrl,
        id = id,
        category = category,
    )

    fun products(
        count: Int,
        category: String = DEFAULT_CATEGORY,
    ): List<Product> = (1..count).map { i ->
        product(
            id = i.toString(),
            name = "품목$i",
            price = i * 1_000,
            category = category,
        )
    }
}
