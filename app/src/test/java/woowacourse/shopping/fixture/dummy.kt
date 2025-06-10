package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.History
import woowacourse.shopping.domain.model.Product

val dummyProductDto =
    Product(
        id = 1L,
        name = "Mock 상품",
        price = 1000,
        imageUrl = "https://example.com/image.jpg",
        category = "mockCategory",
    )

val dummyCartProduct =
    CartProduct(
        id = 1L,
        product = dummyProductDto,
        quantity = 2,
    )

val dummyHistory =
    History(
        id = 1L,
        name = "테스트상품",
        thumbnailUrl = "",
    )
