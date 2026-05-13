package woowacourse.shopping.repository

import woowacourse.shopping.model.Money
import woowacourse.shopping.model.Product
import woowacourse.shopping.model.ProductId

object CartRepositoryFixture {
    val shrimpCracker =
        Product(
            id = ProductId(1L),
            name = "새우깡",
            price = Money(3_100),
            imageUrl = "",
        )

    val sourCandy =
        Product(
            id = ProductId(2L),
            name = "아이셔",
            price = Money(1_300),
            imageUrl = "",
        )

    val products = listOf(shrimpCracker, sourCandy)
}
