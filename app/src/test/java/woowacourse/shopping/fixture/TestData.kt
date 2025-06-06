package woowacourse.shopping.fixture

import woowacourse.shopping.domain.coupon.BuyXGetY
import woowacourse.shopping.domain.coupon.FreeShipping
import woowacourse.shopping.view.product.ProductsItem
import woowacourse.shopping.view.shoppingCart.ShoppingCartItem
import java.time.LocalDate

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

val SHOPPING_CART_PRODUCTS_TO_ORDER =
    listOf(
        SHOPPING_CART_PRODUCT1,
        SHOPPING_CART_PRODUCT2,
    )

val COUPONS =
    listOf(
        FreeShipping(
            id = 0,
            description = "무료배송",
            explanationDate = LocalDate.of(2099, 1, 1),
            code = "adsf",
            minimumAmount = 0,
        ),
        BuyXGetY(
            id = 1,
            description = "10000원 쿠폰",
            explanationDate = LocalDate.of(2099, 1, 1),
            code = "adsf",
            buyQuantity = 2,
            getQuantity = 1,
        ),
    )
