package woowacourse.shopping.fixture

import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts

val SHOPPING_CART_PRODUCT1 =
    ShoppingCartProduct(
        id = 1,
        product = PRODUCT1,
        quantity = 2,
    )

val SHOPPING_CART_PRODUCT2 =
    ShoppingCartProduct(
        id = 2,
        product = PRODUCT1,
        quantity = 1,
    )

val SHOPPING_CART_PRODUCT3 =
    ShoppingCartProduct(
        id = 3,
        product = PRODUCT1,
        quantity = 1,
    )

val SHOPPING_CART_PRODUCT4 =
    ShoppingCartProduct(
        id = 4,
        product = PRODUCT1,
        quantity = 1,
    )

val SHOPPING_CART_PRODUCT5 =
    ShoppingCartProduct(
        id = 5,
        product = PRODUCT1,
        quantity = 1,
    )

val SHOPPING_CARTS1 =
    ShoppingCarts(
        last = false,
        listOf(
            SHOPPING_CART_PRODUCT1,
            SHOPPING_CART_PRODUCT2,
            SHOPPING_CART_PRODUCT3,
            SHOPPING_CART_PRODUCT4,
            SHOPPING_CART_PRODUCT5,
        ),
    )

val SHOPPING_CART_INCREASED =
    ShoppingCarts(
        last = false,
        listOf(
            SHOPPING_CART_PRODUCT1.copy(quantity = 3),
            SHOPPING_CART_PRODUCT2,
            SHOPPING_CART_PRODUCT3,
            SHOPPING_CART_PRODUCT4,
            SHOPPING_CART_PRODUCT5,
        ),
    )

val SHOPPING_CART_DECREASED =
    ShoppingCarts(
        last = false,
        listOf(
            SHOPPING_CART_PRODUCT1.copy(quantity = 1),
            SHOPPING_CART_PRODUCT2,
            SHOPPING_CART_PRODUCT3,
            SHOPPING_CART_PRODUCT4,
            SHOPPING_CART_PRODUCT5,
        ),
    )

val SHOPPING_CART_REMOVED =
    ShoppingCarts(
        last = false,
        listOf(
            SHOPPING_CART_PRODUCT1,
            SHOPPING_CART_PRODUCT3,
            SHOPPING_CART_PRODUCT4,
            SHOPPING_CART_PRODUCT5,
        ),
    )
