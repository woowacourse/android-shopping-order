package woowacourse.shopping.fixture

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ViewedItemRepository

class FakeViewedItemRepository(
    private val size: Int,
) : ViewedItemRepository {
    private val viewed =
        MutableList(size) { index ->
            Product(
                id = index + 1L,
                name = "${index + 1} 아이스 카페 아메리카노",
                imageUrl = "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[110563]_20210426095937947.jpg",
                price = 1000 * (index + 1),
                quantity = 0,
                category = "",
            )
        }.toMutableList()

    override suspend fun insertViewedItem(product: Product) {
        viewed.removeAll { it.id == product.id }
        viewed.add(0, product)
    }

    override suspend fun getViewedItems(): Result<List<Product>> = Result.success(viewed.take(10).toList())

    override suspend fun getLastViewedItem(): Result<Product?> = Result.success(viewed.firstOrNull())
}
