package woowacourse.shopping.fixture

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts

val PRODUCT1 =
    Product(
        id = 1,
        name = "에어포스1",
        price = 100_000,
        imageUrl = "https://i.namu.wiki/i/ExNTyOB5363wFnhGLSfRPOSj9G5VwSQiISkjICuIVI-8S8djFN8cJLB44Mb7jzqQMu-8OJxtuPTmE3FLkq4ebg.webp",
        category = "패션잡화",
    )

val PRODUCT2 =
    Product(
        id = 2,
        name = "에어포스2",
        price = 100_000,
        imageUrl = "https://i.namu.wiki/i/ExNTyOB5363wFnhGLSfRPOSj9G5VwSQiISkjICuIVI-8S8djFN8cJLB44Mb7jzqQMu-8OJxtuPTmE3FLkq4ebg.webp",
        category = "패션일까",
    )

val SHOPPING_CART_QUANTITY = 1

val SHOPPING_CART_PRODUCT1 =
    ShoppingCartProduct(
        id = 1,
        product = PRODUCT1,
        quantity = 1,
    )

val SHOPPING_CARTS =
    ShoppingCarts(
        last = true,
        listOf(
            SHOPPING_CART_PRODUCT1,
        ),
    )

val RECENT_PRODUCTS =
    listOf(
        PRODUCT1,
        PRODUCT2,
    )
