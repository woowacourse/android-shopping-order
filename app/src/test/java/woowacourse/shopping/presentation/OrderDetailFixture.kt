package woowacourse.shopping.presentation

import woowacourse.shopping.presentation.model.CartModel
import woowacourse.shopping.presentation.model.OrderDetailModel
import woowacourse.shopping.presentation.model.ProductModel

object OrderDetailFixture {
    fun getFixture(): OrderDetailModel = OrderDetailModel(
        1L,
        0,
        100,
        "2023-06-04T12:00",
        listOf(
            CartModel(
                1L,
                ProductModel(
                    id = 1L,
                    title = "[선물세트][밀크바오밥] 퍼퓸 화이트 4종 선물세트 (샴푸+트리트먼트+바디워시+바디로션)",
                    price = 24_900,
                    imageUrl = "https://product-image.kurly.com/product/image/2c392328-104a-4fef-8222-c11be9c5c35f.jpg",
                ),
                1,
                true
            ),
            CartModel(
                2L,
                ProductModel(
                    id = 2L,
                    title = "치킨치킨",
                    price = 15_000,
                    imageUrl = "https://product-image.kurly.com/product/image/2c392328-104a-4fef-8222-c11be9c5c35f.jpg",
                ),
                1,
                true
            ),
        )
    )
}
