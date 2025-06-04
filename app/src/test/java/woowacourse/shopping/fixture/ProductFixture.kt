package woowacourse.shopping.fixture

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.product.Price
import woowacourse.shopping.domain.product.Product

val productFixture1 =
    Product(
        id = 1L,
        name = "Product1",
        category = "",
        imgUrl = "",
        price = Price(0),
        quantity = Quantity(1),
    )

val productFixture2 =
    Product(
        id = 2L,
        name = "Product2",
        category = "",
        imgUrl = "",
        price = Price(0),
        quantity = Quantity(1),
    )
