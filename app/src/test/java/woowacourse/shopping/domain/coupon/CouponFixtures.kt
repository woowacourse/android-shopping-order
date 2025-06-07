package woowacourse.shopping.domain.coupon

import woowacourse.shopping.domain.Quantity
import woowacourse.shopping.domain.cart.ShoppingCart
import woowacourse.shopping.domain.product.Price
import woowacourse.shopping.domain.product.Product
import java.time.LocalDate
import java.time.LocalTime

val BOGO_COUPON = BogoCoupon(
    description = "2개 구매 시 1개 무료 쿠폰",
    expirationDate = LocalDate.of(2025, 6, 30),
    buyQuantity = 2,
    getQuantity = 1
)

val FIXED_COUPON = FixedCoupon(
    description = "5,000원 할인 쿠폰",
    expirationDate = LocalDate.of(2025, 6, 30),
    discount = 5000,
    minimumAmount = 100_000,
)

val FREE_SHIPPING_COUPON = FreeShippingCoupon(
    description = "5만원 이상 구매 시 무료 배송 쿠폰",
    expirationDate = LocalDate.of(2025, 6, 30),
    minimumAmount = 50_000,
)

val MIRACLE_SALE_COUPON = MiracleSaleCoupon(
    description = "미라클모닝",
    expirationDate = LocalDate.of(2025, 6, 30),
    discount = 30,
    availableTime = AvailableTime(LocalTime.of(4,0), LocalTime.of(7, 0)),
)


val COUPONS: List<Coupon> = listOf(BOGO_COUPON, FIXED_COUPON, FREE_SHIPPING_COUPON, MIRACLE_SALE_COUPON)

fun createCartItem(price: Int, quantity: Int): ShoppingCart {
    return ShoppingCart(
        id = 1L,
        product = Product(
            id = 1L,
            name = "name",
            imgUrl = "url",
            category = "카테고리",
            price = Price(price),
            quantity = Quantity(3),
        ),
        quantity = Quantity(quantity),
    )
}
