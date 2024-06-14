package woowacourse.shopping.remote.api

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Carts
import woowacourse.shopping.domain.model.Pageable
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.model.Sort
import woowacourse.shopping.domain.model.coupons.BOGO
import woowacourse.shopping.domain.model.coupons.FIXED5000
import woowacourse.shopping.domain.model.coupons.FREESHIPPING
import woowacourse.shopping.domain.model.coupons.MIRACLESALE
import java.time.LocalDate
import java.time.LocalTime

object DummyData {
    const val STUB_IMAGE_URL_A =
        "https://i.namu.wiki/i/VnSgJ92KZ4dSRF2_x3LAYiE-zafxvNochXYrt6QD88DNtVziOxYUVKploFydbFNY7rcmOBUEra42XObzSuBwww.webp"
    val STUB_PRODUCT_A =
        Product(id = 1L, name = "홍차", price = 10000, category = "", imageUrl = STUB_IMAGE_URL_A)
    val STUB_CART_A = Cart(id = Cart.EMPTY_CART_ID, quantity = 0, product = STUB_PRODUCT_A)

    const val STUB_IMAGE_URL_B =
        "https://img.danawa.com/prod_img/500000/451/474/img/3474451_1.jpg?_v=20210323160420"
    val STUB_PRODUCT_B =
        Product(
            id = 2,
            name = "스위트 콘",
            price = 12000,
            category = "",
            imageUrl = STUB_IMAGE_URL_B,
        )
    val STUB_CART_B = Cart(id = 2, quantity = 0, product = STUB_PRODUCT_B)

    const val STUB_IMAGE_URL_C =
        "https://i.namu.wiki/i/fhsBMFdIgnB_D4KHQpaG0n2yk5X26rVpfsYeoIaJxwb3gLbQDJ9C7rgVQEZWKfhUwE0bR_2yT0Y1FCOwkDePJg.webp"
    val STUB_PRODUCT_C =
        Product(id = 3, name = "신라면", price = 15000, category = "", imageUrl = STUB_IMAGE_URL_C)
    val STUB_CART_C = Cart(id = 3, quantity = 0, product = STUB_PRODUCT_C)

    val STUB_COUPON_A =
        FIXED5000(
            id = 1L,
            description = "",
            expirationDate = LocalDate.now(),
            discountType = "",
            discount = 0,
            minimumAmount = 0,
        )
    val STUB_COUPON_B =
        BOGO(
            id = 2L,
            description = "",
            expirationDate = LocalDate.now(),
            discountType = "",
            buyQuantity = 0,
            getQuantity = 0,
        )
    val STUB_COUPON_C =
        FREESHIPPING(
            id = 3L,
            description = "",
            expirationDate = LocalDate.now(),
            discountType = "",
            minimumAmount = 0,
        )
    val STUB_COUPON_D =
        MIRACLESALE(
            id = 4L,
            description = "",
            expirationDate = LocalDate.now(),
            discountType = "",
            discount = 0,
            availableTime = MIRACLESALE.AvailableLocalTime(LocalTime.now(), LocalTime.now()),
        )

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
            content = PRODUCT_LIST,
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
                PRODUCT_LIST.mapIndexed { index, product ->
                    Cart(id = index, product = product)
                },
        )

    val CART_ARRAY =
        arrayOf(
            Cart(
                id = 1,
                quantity = 3,
                product = STUB_PRODUCT_A.copy(id = 1L),
            ),
            Cart(
                id = 2,
                quantity = 3,
                product = STUB_PRODUCT_B.copy(id = 2L),
            ),
            Cart(
                id = 3,
                quantity = 3,
                product = STUB_PRODUCT_C.copy(id = 3L),
            ),
        )

    val COPOUNS = listOf(STUB_COUPON_A, STUB_COUPON_B, STUB_COUPON_C, STUB_COUPON_D)
}
