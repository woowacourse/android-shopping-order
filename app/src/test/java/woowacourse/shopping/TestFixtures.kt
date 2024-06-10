package woowacourse.shopping

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.remote.dto.response.AvailableTimeDTO
import woowacourse.shopping.remote.dto.response.CouponResponse

val dummyProduct = Product(10L, "바닐라 라떼", "", 5_000, "")

val dummyProducts =
    List(3) { id ->
        Product(
            id = id.toLong(),
            name = "상품 $id",
            imgUrl = "",
            price = 1_000,
            category = "",
        )
    }

val dummyCarts: List<Cart> =
    List(3) {
        Cart(
            cartId = it.toLong(),
            product = dummyProducts[it],
            quantity = 1,
        )
    }

val dummyProductList =
    List(30) { id ->
        Product(
            id = id.toLong(),
            name = "상품 $id",
            imgUrl = "",
            price = 1_000,
            category = "",
        )
    }

val dummyCoupons =
    listOf(
        CouponResponse(
            id = 1,
            code = "FIXED5000",
            description = "5,000원 할인 쿠폰",
            expirationDate = "2024-11-30",
            discount = 5000,
            minimumAmount = 100000,
            discountType = "fixed",
        ),
        CouponResponse(
            id = 2,
            code = "BOGO",
            description = "2개 구매 시 1개 무료 쿠폰",
            expirationDate = "2024-05-30",
            buyQuantity = 2,
            getQuantity = 1,
            discountType = "buyXgetY",
        ),
        CouponResponse(
            id = 3,
            code = "FREESHIPPING",
            description = "5만원 이상 구매 시 무료 배송 쿠폰",
            expirationDate = "2024-08-31",
            minimumAmount = 50000,
            discountType = "freeShipping",
        ),
        CouponResponse(
            id = 4,
            code = "MIRACLESALE",
            description = "미라클모닝 30% 할인 쿠폰",
            expirationDate = "2024-07-31",
            discount = 30,
            availableTime =
                AvailableTimeDTO(
                    start = "04:00:00",
                    end = "07:00:00",
                ),
            discountType = "percentage",
        ),
    )
