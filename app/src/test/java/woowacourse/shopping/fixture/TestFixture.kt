package woowacourse.shopping.fixture

import woowacourse.shopping.domain.order.Coupon
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.shoppingCart.ShoppingCartProduct
import java.time.LocalDate

object TestFixtures {
    val PRODUCT_AIR_FORCE =
        Product(
            id = 1,
            name = "에어포스1",
            price = 100_000,
            imageUrl = "https://i.namu.wiki/i/ExNTyOB5363wFnhGLSfRPOSj9G5VwSQiISkjICuIVI-8S8djFN8cJLB44Mb7jzqQMu-8OJxtuPTmE3FLkq4ebg.webp",
            category = "패션잡화",
        )

    val PRODUCTS_TO_ORDER_BONUS_COUPON =
        listOf(
            ShoppingCartProduct(
                id = 1,
                product = PRODUCT_AIR_FORCE,
                quantity = 3,
            ),
        )

    val PRODUCTS_TO_ORDER_PRICE_DISCOUNT =
        listOf(
            ShoppingCartProduct(
                id = 1,
                product = PRODUCT_AIR_FORCE,
                quantity = 4,
            ),
        )

    val BONUS_COUPONS: List<Coupon> =
        listOf(
            Coupon.Bonus(
                id = 1,
                code = "MOONGCHI",
                description = "테스트용 쿠폰",
                expirationDate = LocalDate.of(2025, 7, 1),
                buyQuantity = 2,
                getQuantity = 1,
            ),
        )

    val PRICE_DISCOUNT_COUPONS: List<Coupon> =
        listOf(
            Coupon.PriceDiscount(
                id = 1,
                code = "MOONGCHI",
                description = "테스트용 쿠폰",
                expirationDate = LocalDate.of(2025, 7, 1),
                discount = 1000,
                minimumAmount = 50000,
            ),
        )
}
