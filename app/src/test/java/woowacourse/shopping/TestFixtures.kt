package woowacourse.shopping

import woowacourse.shopping.data.dto.response.AvailableTime
import woowacourse.shopping.domain.BuyXGetYCoupon
import woowacourse.shopping.domain.Cart
import woowacourse.shopping.domain.Coupon
import woowacourse.shopping.domain.DiscountType
import woowacourse.shopping.domain.FixedCoupon
import woowacourse.shopping.domain.FreeShippingCoupon
import woowacourse.shopping.domain.PercentageCoupon
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.ProductListItem
import woowacourse.shopping.domain.RecentProductItem
import java.time.LocalDate
import java.time.LocalDateTime

val dummyProducts =
    List(3) { id ->
        Product(
            id = id.toLong(),
            imgUrl = "",
            name = "$id",
            price = 10000,
            category = "",
        )
    }

val dummyProduct: Product = dummyProducts.first()

val dummyRecentProducts =
    listOf(
        RecentProductItem(
            productId = 0,
            name = "0",
            imgUrl = "",
            dateTime = LocalDateTime.of(2023, 5, 23, 11, 42),
            category = "",
        ),
    )

val dummyCartProducts: List<Cart> =
    List(3) {
        Cart(
            product = dummyProducts[it],
            quantity = 1,
        )
    }

val cart: Cart = dummyCartProducts.first()

val dummyShoppingProducts =
    ProductListItem.ShoppingProductItem.fromProductsAndCarts(
        dummyProducts,
        dummyCartProducts,
    )

fun buildShoppingProduct(
    price: Long,
    quantity: Int,
) = ProductListItem.ShoppingProductItem(
    id = 0,
    name = "",
    imgUrl = "",
    price = price,
    quantity = quantity,
    category = "",
)

fun buildFixedCoupon(date: LocalDate) =
    FixedCoupon(
        id = 0,
        code = "",
        description = "",
        expirationDate = date,
        discountType = DiscountType.Fixed,
        isChecked = false,
        discount = 1000,
        minimumAmount = 10000,
    )

fun buildBuyXGetYCoupon(date: LocalDate) =
    BuyXGetYCoupon(
        id = 0,
        code = "",
        description = "",
        expirationDate = date,
        discountType = DiscountType.BuyXGetY,
        isChecked = false,
        buyQuantity = 2,
        getQuantity = 1,
    )

fun buildFreeShippingCoupon(date: LocalDate) =
    FreeShippingCoupon(
        id = 0,
        code = "",
        description = "",
        expirationDate = date,
        discountType = DiscountType.FreeShipping,
        isChecked = false,
    )

fun buildPercentageCoupon(
    date: LocalDate,
    availableTime: AvailableTime,
) = PercentageCoupon(
    id = 0,
    code = "",
    description = "",
    expirationDate = date,
    discountType = DiscountType.Percentage,
    isChecked = false,
    discount = 10,
    availableTime = availableTime,
)

fun buildCoupons(
    expirationDate: LocalDate,
    availableTime: AvailableTime,
): List<Coupon> =
    listOf(
        buildFixedCoupon(expirationDate),
        buildBuyXGetYCoupon(expirationDate),
        buildFreeShippingCoupon(expirationDate),
        buildPercentageCoupon(expirationDate, availableTime),
    )
