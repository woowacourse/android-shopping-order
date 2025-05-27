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
        onSuccess: (Product?) -> Unit,
    ) {
        onSuccess(fakeProducts.find { it.id == id })
    }

    override fun getProductsByIds(
        ids: List<Int>,
        onSuccess: (List<Product>?) -> Unit,
    ) {
        val result = ids.mapNotNull { id -> fakeProducts.find { it.id == id } }
        onSuccess(result)
    }

    override fun getPagedProducts(
        page: Int,
        size: Int,
        onSuccess: (PagedResult<Product>) -> Unit,
    ) {
        val pagedItems = fakeProducts.drop(page * size).take(size)
        val hasNext = page * size + pagedItems.size < fakeProducts.size
        onSuccess(PagedResult(pagedItems, hasNext))
    }
}
