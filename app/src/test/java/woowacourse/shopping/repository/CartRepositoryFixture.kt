package woowacourse.shopping.repository

import woowacourse.shopping.model.product.Money
import woowacourse.shopping.model.product.Product

object CartRepositoryFixture {
    val shrimpCracker =
        Product(
            id = (1L),
            name = "새우깡",
            price = Money(3_100),
            imageUrl = "",
        )

    val sourCandy =
        Product(
            id = (2L),
            name = "아이셔",
            price = Money(1_300),
            imageUrl = "",
        )

    val products = listOf(shrimpCracker, sourCandy)
}
