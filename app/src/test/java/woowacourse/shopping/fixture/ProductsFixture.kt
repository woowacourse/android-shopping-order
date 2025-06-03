package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

object ProductsFixture {
    val dummyProduct =
        Product(
            0,
            "맥심 모카골드 마일드",
            Price(12000),
            "https://sitem.ssgcdn.com/64/93/82/item/0000006829364_i1_464.jpg",
            "커피",
        )

    val dummyProducts: List<Product> =
        List(20) { index ->
            Product(
                index.toLong(),
                "맥심 모카골드 마일드",
                Price(12000),
                "https://sitem.ssgcdn.com/64/93/82/item/0000006829364_i1_464.jpg",
                "커피",
            )
        }

    val dummyCartItem =
        CartItem(
            product =
                Product(
                    productId = 0,
                    imageUrl = "",
                    name = "Product 0",
                    _price = Price(1000),
                    category = "패션",
                ),
            quantity = 1,
        )

    val dummyCartItems =
        List(10) {
            CartItem(
                product =
                    Product(
                        productId = it.toLong(),
                        imageUrl = "",
                        name = "Product $it",
                        _price = Price(1000),
                        category = "패션",
                    ),
                quantity = 1,
            )
        }
}
