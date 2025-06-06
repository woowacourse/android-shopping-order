package woowacourse.shopping.fixture

import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository

class FakeProductRepository : ProductRepository {
    private val fakeProducts =
        List(100) { index ->
            Product(
                id = index,
                imageUrl = "",
                name = "Product $index",
                price = (index + 1) * 1000,
            )
        }

    override suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<Product>> =
        if (page == null || size == null) {
            Result.success(PagedResult(emptyList(), false))
        } else {
            val pagedItems = fakeProducts.drop(page * size).take(size)
            val hasNext = page * size + pagedItems.size < fakeProducts.size
            Result.success(PagedResult(pagedItems, hasNext))
        }
}
