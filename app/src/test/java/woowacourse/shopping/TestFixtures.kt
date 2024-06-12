package woowacourse.shopping

import woowacourse.shopping.domain.model.Cart
import woowacourse.shopping.domain.model.Product

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
