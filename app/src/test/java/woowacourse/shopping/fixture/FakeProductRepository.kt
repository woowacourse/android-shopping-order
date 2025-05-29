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

    override fun getProductById(
        id: Int,
        onResult: (Result<Product?>) -> Unit,
    ) {
        onResult(Result.success(fakeProducts.find { it.id == id }))
    }

    override fun getProductsByIds(
        ids: List<Int>,
        onResult: (Result<List<Product>?>) -> Unit,
    ) {
        val result = ids.mapNotNull { id -> fakeProducts.find { it.id == id } }
        onResult(Result.success(result))
    }

    override fun getPagedProducts(
        page: Int?,
        size: Int?,
        onResult: (Result<PagedResult<Product>>) -> Unit,
    ) {
        if (page == null || size == null) {
            onResult(Result.success(PagedResult(emptyList(), false)))
            return
        }
        val pagedItems = fakeProducts.drop(page * size).take(size)
        val hasNext = page * size + pagedItems.size < fakeProducts.size
        onResult(Result.success(PagedResult(pagedItems, hasNext)))
    }
}
