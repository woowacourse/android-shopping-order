package woowacourse.shopping.fixture

import woowacourse.shopping.domain.repository.ViewedItemRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel

class FakeViewedItemRepository(
    private val size: Int,
) : ViewedItemRepository {
    private val viewed =
        MutableList(size) { index ->
            ProductUiModel(
                id = index + 1L,
                name = "${index + 1} 아이스 카페 아메리카노",
                imageUrl = "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[110563]_20210426095937947.jpg",
                price = 1000 * (index + 1),
                quantity = 0,
            )
        }.toMutableList()

    override fun insertViewedItem(
        product: ProductUiModel,
        onComplete: () -> Unit,
    ) {
        viewed.removeAll { it.id == product.id }
        viewed.add(0, product)
        onComplete()
    }

    override fun getViewedItems(callback: (List<ProductUiModel>) -> Unit) {
        callback(viewed.take(10))
    }

    override fun getLastViewedItem(callback: (ProductUiModel?) -> Unit) {
        callback(viewed.firstOrNull())
    }
}
