package woowacourse.shopping.ui

import woowacourse.shopping.domain.Price
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.RecentProduct

object RecentProductFixture {

    fun createProducts() = listOf(
        RecentProduct(
            1,
            Product(1, "더미입니다만", Price(1000), "url")
        ),
        RecentProduct(
            2,
            Product(2, "더미입니다만", Price(1000), "url")
        ),
        RecentProduct(
            3,
            Product(3, "더미입니다만", Price(1000), "url")
        ),
        RecentProduct(
            4,
            Product(4, "더미입니다만", Price(1000), "url")
        )
    )
}
