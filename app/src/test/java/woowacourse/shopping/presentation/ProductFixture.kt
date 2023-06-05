package woowacourse.shopping.presentation

import woowacourse.shopping.presentation.model.ProductModel

internal object ProductFixture {
    fun getDatas(): List<ProductModel> {
        return listOf(
            ProductModel(
                id = 1L,
                title = "[선물세트][밀크바오밥] 퍼퓸 화이트 4종 선물세트 (샴푸+트리트먼트+바디워시+바디로션)",
                price = 24_900,
                imageUrl = "https://product-image.kurly.com/product/image/2c392328-104a-4fef-8222-c11be9c5c35f.jpg",
            ),
            ProductModel(
                id = 2L,
                title = "치킨치킨",
                price = 15_000,
                imageUrl = "https://product-image.kurly.com/product/image/2c392328-104a-4fef-8222-c11be9c5c35f.jpg",
            ),
            ProductModel(
                id = 3L,
                title = "피자피자",
                price = 10_000,
                imageUrl = "https://product-image.kurly.com/product/image/2c392328-104a-4fef-8222-c11be9c5c35f.jpg",
            ),
        )
    }

    fun getData(): ProductModel {
        return ProductModel(
            id = 1L,
            title = "[선물세트][밀크바오밥] 퍼퓸 화이트 4종 선물세트 (샴푸+트리트먼트+바디워시+바디로션)",
            price = 24_900,
            imageUrl = "https://product-image.kurly.com/product/image/2c392328-104a-4fef-8222-c11be9c5c35f.jpg",
        )
    }
}