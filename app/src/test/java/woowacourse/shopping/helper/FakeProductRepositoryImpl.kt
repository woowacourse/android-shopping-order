package woowacourse.shopping.helper

import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.view.home.HomeViewModel

class FakeProductRepositoryImpl(
    private val products: List<Product> = emptyList(),
) : ProductRepository {
    private var offset = 0

    override fun findProductsByPage(): List<Product> {
        val size = products.size
        val start = offset
        offset = Integer.min(offset + HomeViewModel.PAGE_SIZE, size)
        return products.subList(start, offset)
    }

    override fun findProductById(id: Int): Product? {
        return products.firstOrNull { it.id == id }
    }

    override fun canLoadMore(): Boolean {
        val size = products.size
        return !(size < HomeViewModel.PAGE_SIZE || offset == size)
    }
}
