package woowacourse.shopping.fixture

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.ShoppingCart

val shoppingCartFixture1 =
    ShoppingCart(
        id = 1L,
        product = productFixture3,
        quantity = Quantity(2),
    )

val shoppingCartFixture2 =
    ShoppingCart(
        id = 2L,
        product = productFixture4,
        quantity = Quantity(3),
    )

val shoppingCartFixtures = listOf(shoppingCartFixture1, shoppingCartFixture2)
