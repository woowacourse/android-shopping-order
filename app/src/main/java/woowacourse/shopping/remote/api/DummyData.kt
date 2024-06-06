package woowacourse.shopping.remote.api

import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.model.remote.AvailableTimeDto
import woowacourse.shopping.data.model.remote.CartDto
import woowacourse.shopping.data.model.remote.CouponDto
import woowacourse.shopping.data.model.remote.ProductDto
import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.domain.model.Pageable
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.Sort
import woowacourse.shopping.presentation.model.CartsWrapper

object DummyData {
    const val STUB_IMAGE_URL_A =
        "https://i.namu.wiki/i/VnSgJ92KZ4dSRF2_x3LAYiE-zafxvNochXYrt6QD88DNtVziOxYUVKploFydbFNY7rcmOBUEra42XObzSuBwww.webp"
    val STUB_PRODUCT_A =
        ProductDto(id = 1L, name = "홍차", price = 10000, category = "", imageUrl = STUB_IMAGE_URL_A)
    val STUB_CART_A = CartDto(id = Cart.EMPTY_CART_ID, quantity = 0, product = STUB_PRODUCT_A)

    const val STUB_IMAGE_URL_B =
        "https://img.danawa.com/prod_img/500000/451/474/img/3474451_1.jpg?_v=20210323160420"
    val STUB_PRODUCT_B =
        ProductDto(
            id = 2,
            name = "스위트 콘",
            price = 12000,
            category = "",
            imageUrl = STUB_IMAGE_URL_B,
        )
    val STUB_CART_B = CartDto(id = 2, quantity = 0, product = STUB_PRODUCT_B)

    const val STUB_IMAGE_URL_C =
        "https://i.namu.wiki/i/fhsBMFdIgnB_D4KHQpaG0n2yk5X26rVpfsYeoIaJxwb3gLbQDJ9C7rgVQEZWKfhUwE0bR_2yT0Y1FCOwkDePJg.webp"
    val STUB_PRODUCT_C =
        ProductDto(id = 3, name = "신라면", price = 15000, category = "", imageUrl = STUB_IMAGE_URL_C)
    val STUB_CART_C = CartDto(id = 3, quantity = 0, product = STUB_PRODUCT_C)

    val PRODUCT_LIST =
        mutableListOf(
            STUB_PRODUCT_A,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_A,
            STUB_PRODUCT_A,
            STUB_PRODUCT_A,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_A,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_A,
            STUB_PRODUCT_A,
            STUB_PRODUCT_A,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_A,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_A,
            STUB_PRODUCT_A,
            STUB_PRODUCT_A,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_A,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_A,
            STUB_PRODUCT_A,
            STUB_PRODUCT_A,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
            STUB_PRODUCT_B,
            STUB_PRODUCT_C,
        ).mapIndexed { index, product ->
            product.copy(
                id = (index + 1).toLong(),
                name = "$index ${product.name}",
            )
        }

    private val sort = Sort(sorted = false, unSorted = true, empty = true)
    val pageable =
        Pageable(sort, pageNumber = 0, pageSize = 20, offset = 0, paged = true, unPaged = false)

    val PRODUCTS =
        Products(
            content = PRODUCT_LIST.map { it.toDomain() },
            pageable = pageable,
        )

    val CARTS =
        Carts(
            totalElements = 0,
            last = false,
            pageable = pageable,
            content = emptyList(),
        )

    val CARTS_PULL =
        Carts(
            totalElements = PRODUCT_LIST.size,
            last = false,
            pageable = pageable,
            content =
                PRODUCT_LIST.mapIndexed { index, productDto ->
                    Cart(id = index, product = productDto.toDomain())
                },
        )

    val PRODUCT =
        Product(id = 1L, name = "홍차", price = 20000, category = "", imageUrl = STUB_IMAGE_URL_A)

    val CARTS_WRAPPER =
        CartsWrapper(
            listOf(
                Cart(
                    id = 1,
                    quantity = 3,
                    product = PRODUCT.copy(id = 1L),
                ),
                Cart(
                    id = 2,
                    quantity = 3,
                    product = PRODUCT.copy(id = 2L),
                ),
                Cart(
                    id = 3,
                    quantity = 3,
                    product = PRODUCT.copy(id = 3L),
                ),
            ),
        )

    val COUPONS =
        listOf(
            CouponDto(
                id = 1,
                code = "FIXED5000",
                description = "5,000원 할인 쿠폰",
                expirationDate = "2024-11-30",
                discount = 5000,
                minimumAmount = 100000,
                discountType = "fixed",
            ),
            CouponDto(
                id = 2,
                code = "BOGO",
                description = "2개 구매 시 1개 무료 쿠폰",
                expirationDate = "2024-05-30",
                buyQuantity = 2,
                getQuantity = 1,
                discountType = "buyXgetY",
            ),
            CouponDto(
                id = 3,
                code = "FREESHIPPING",
                description = "5만원 이상 구매 시 무료 배송 쿠폰",
                expirationDate = "2024-08-31",
                minimumAmount = 50000,
                discountType = "freeShipping",
            ),
            CouponDto(
                id = 4,
                code = "MIRACLESALE",
                description = "미라클모닝 30% 할인 쿠폰",
                expirationDate = "2024-07-31",
                discount = 30,
                availableTime =
                    AvailableTimeDto(
                        start = "04:00:00",
                        end = "07:00:00",
                    ),
                discountType = "percentage",
            ),
        )

    val COUPONS_STATE = COUPONS.map { it.toDomain() }
}
