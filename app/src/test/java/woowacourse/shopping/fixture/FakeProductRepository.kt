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

    override suspend fun getProductById(id: Int): Result<Product?> {
        return Result.success(fakeProducts.find { it.id == id })
    }

    override suspend fun getProductsByIds(ids: List<Int>): Result<List<Product>?> {
        val result = ids.mapNotNull { id -> fakeProducts.find { it.id == id } }
        return Result.success(result)
    }

    override suspend fun getPagedProducts(
        page: Int?,
        size: Int?,
    ): Result<PagedResult<Product>> {
        if (page == null || size == null) {
            return Result.success(PagedResult(emptyList(), false))
        }
        val pagedItems = fakeProducts.drop(page * size).take(size)
        val hasNext = page * size + pagedItems.size < fakeProducts.size
        return Result.success(PagedResult(pagedItems, hasNext))
    }
}
