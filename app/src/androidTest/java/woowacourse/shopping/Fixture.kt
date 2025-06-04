package woowacourse.shopping

import woowacourse.shopping.data.entity.CartEntity
import woowacourse.shopping.data.entity.RecentlyViewedProduct
import woowacourse.shopping.domain.model.Price
import woowacourse.shopping.domain.model.Product

object Fixture {
    val dummyCartEntity =
        CartEntity(
            0,
            10,
        )

    val dummyCartEntity2 =
        CartEntity(
            1,
            1,
        )

    val dummyRecentlyViewedProduct =
        RecentlyViewedProduct(
            productId = 0,
            name = "맥심 모카골드 마일드",
            price = 100,
            imageUrl = "aa",
            category = "aa",
            viewedAt = 1234,
        )

    val dummyRecentlyViewedProductList =
        listOf(
            RecentlyViewedProduct(
                productId = 0,
                name = "맥심 모카골드 마일드",
                price = 100,
                imageUrl = "aa",
                category = "aa",
                viewedAt = 1234,
            ),
            RecentlyViewedProduct(
                productId = 1,
                name = "맥심 모카골드 마일드2",
                price = 1000,
                imageUrl = "bb",
                category = "bb",
                viewedAt = 5555,
            ),
            RecentlyViewedProduct(
                productId = 2,
                name = "맥심 모카골드 마일드3",
                price = 10000,
                imageUrl = "cc",
                category = "cc",
                viewedAt = 9999,
            ),
        )

    val dummyProduct =
        Product(
            0,
            "맥심 모카골드 마일드",
            Price(12000),
            "https://sitem.ssgcdn.com/64/93/82/item/0000006829364_i1_464.jpg",
            "커피",
        )
}
