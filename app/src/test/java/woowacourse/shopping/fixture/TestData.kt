package woowacourse.shopping.fixture

import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import woowacourse.shopping.domain.shoppingCart.ShoppingCarts
import woowacourse.shopping.view.product.ProductsItem
import woowacourse.shopping.view.shoppingCart.ShoppingCartItem

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
        category = "패션잡화",
    )

val PRODUCT3 =
    Product(
        id = 3,
        name = "에어포스2",
        price = 100_000,
        imageUrl = "https://i.namu.wiki/i/ExNTyOB5363wFnhGLSfRPOSj9G5VwSQiISkjICuIVI-8S8djFN8cJLB44Mb7jzqQMu-8OJxtuPTmE3FLkq4ebg.webp",
        category = "패션잡화",
    )

val PRODUCT4 =
    Product(
        id = 4,
        name = "에어포스2",
        price = 100_000,
        imageUrl = "https://i.namu.wiki/i/ExNTyOB5363wFnhGLSfRPOSj9G5VwSQiISkjICuIVI-8S8djFN8cJLB44Mb7jzqQMu-8OJxtuPTmE3FLkq4ebg.webp",
        category = "패션잡화",
    )

val PRODUCT5 =
    Product(
        id = 5,
        name = "에어포스2",
        price = 100_000,
        imageUrl = "https://i.namu.wiki/i/ExNTyOB5363wFnhGLSfRPOSj9G5VwSQiISkjICuIVI-8S8djFN8cJLB44Mb7jzqQMu-8OJxtuPTmE3FLkq4ebg.webp",
        category = "패션잡화",
    )

val PRODUCT6 =
    Product(
        id = 6,
        name = "에어포스2",
        price = 100_000,
        imageUrl = "https://i.namu.wiki/i/ExNTyOB5363wFnhGLSfRPOSj9G5VwSQiISkjICuIVI-8S8djFN8cJLB44Mb7jzqQMu-8OJxtuPTmE3FLkq4ebg.webp",
        category = "패션잡화",
    )

val PRODUCT7 =
    Product(
        id = 7,
        name = "에어포스2",
        price = 100_000,
        imageUrl = "https://i.namu.wiki/i/ExNTyOB5363wFnhGLSfRPOSj9G5VwSQiISkjICuIVI-8S8djFN8cJLB44Mb7jzqQMu-8OJxtuPTmE3FLkq4ebg.webp",
        category = "패션잡화",
    )

val PRODUCT8 =
    Product(
        id = 8,
        name = "에어포스2",
        price = 100_000,
        imageUrl = "https://i.namu.wiki/i/ExNTyOB5363wFnhGLSfRPOSj9G5VwSQiISkjICuIVI-8S8djFN8cJLB44Mb7jzqQMu-8OJxtuPTmE3FLkq4ebg.webp",
        category = "패션잡화",
    )

val PRODUCT9 =
    Product(
        id = 9,
        name = "에어포스2",
        price = 100_000,
        imageUrl = "https://i.namu.wiki/i/ExNTyOB5363wFnhGLSfRPOSj9G5VwSQiISkjICuIVI-8S8djFN8cJLB44Mb7jzqQMu-8OJxtuPTmE3FLkq4ebg.webp",
        category = "패션잡화",
    )

val PRODUCT10 =
    Product(
        id = 10,
        name = "에어포스2",
        price = 100_000,
        imageUrl = "https://i.namu.wiki/i/ExNTyOB5363wFnhGLSfRPOSj9G5VwSQiISkjICuIVI-8S8djFN8cJLB44Mb7jzqQMu-8OJxtuPTmE3FLkq4ebg.webp",
        category = "패션잡화",
    )

val PRODUCT11 =
    Product(
        id = 11,
        name = "에어포스2",
        price = 100_000,
        imageUrl = "https://i.namu.wiki/i/ExNTyOB5363wFnhGLSfRPOSj9G5VwSQiISkjICuIVI-8S8djFN8cJLB44Mb7jzqQMu-8OJxtuPTmE3FLkq4ebg.webp",
        category = "패션잡화",
    )

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

val ALL_TOGGLED =
    listOf(
        ShoppingCartItem.ShoppingCartProductItem(
            shoppingCartProduct = SHOPPING_CART_PRODUCT1,
            isChecked = true,
        ),
        ShoppingCartItem.ShoppingCartProductItem(
            shoppingCartProduct = SHOPPING_CART_PRODUCT2,
            isChecked = true,
        ),
        ShoppingCartItem.ShoppingCartProductItem(
            shoppingCartProduct = SHOPPING_CART_PRODUCT3,
            isChecked = true,
        ),
        ShoppingCartItem.ShoppingCartProductItem(
            shoppingCartProduct = SHOPPING_CART_PRODUCT4,
            isChecked = true,
        ),
        ShoppingCartItem.ShoppingCartProductItem(
            shoppingCartProduct = SHOPPING_CART_PRODUCT5,
            isChecked = true,
        ),
    )

val RECENT_PRODUCTS =
    listOf(
        PRODUCT1,
        PRODUCT2,
    )

val RECENT_PRODUCTS_FULL =
    listOf(
        PRODUCT1,
        PRODUCT2,
        PRODUCT3,
        PRODUCT4,
        PRODUCT5,
        PRODUCT6,
        PRODUCT7,
        PRODUCT8,
        PRODUCT9,
        PRODUCT10,
        PRODUCT11,
    )

val RECOMMENDED_PRODUCTS =
    listOf(
        ProductsItem.ProductItem(
            null,
            PRODUCT2,
        ),
    )
