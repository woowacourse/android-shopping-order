package woowacourse.shopping.ui

import woowacourse.shopping.domain.Basket
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Count
import woowacourse.shopping.domain.Price
import woowacourse.shopping.domain.Product

object BasketFixture {

    fun createBasket() = Basket(
        products = listOf(
            BasketProduct(
                id = 0,
                count = Count(10),
                product = Product(
                    id = 0,
                    name = "",
                    price = Price(0),
                    imageUrl = ""
                ),
                checked = true
            ),
            BasketProduct(
                id = 1,
                count = Count(10),
                product = Product(
                    id = 1,
                    name = "",
                    price = Price(0),
                    imageUrl = ""
                )
            ),
            BasketProduct(
                id = 2,
                count = Count(10),
                product = Product(
                    id = 2,
                    name = "",
                    price = Price(0),
                    imageUrl = ""
                ),
                checked = true
            )
        )
    )
}
