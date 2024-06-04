package woowacourse.shopping.data.repository

import woowacourse.shopping.data.db.product.MockProductService
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.service.ProductService
import woowacourse.shopping.utils.exception.ErrorEvent
import kotlin.concurrent.thread

class ProductRepositoryImpl : ProductRepository {
    private val mockProductService: ProductService = MockProductService()

    override suspend fun loadPagingProducts(offset: Int): Result<List<Product>> {
        return runCatching {
            mockProductService.findPagingProducts(offset, PRODUCT_LOAD_PAGING_SIZE)
        }
    }

    override suspend fun loadCategoryProducts(
        size: Int,
        category: String,
    ): Result<List<Product>> {
        return runCatching {
            mockProductService.findPagingProducts(DEFAULT_ITEM_SIZE, PRODUCT_LOAD_PAGING_SIZE)
        }
    }

    override suspend fun getProduct(productId: Long): Result<Product> {
        return runCatching {
            mockProductService.findProductById(productId)
        }.mapCatching {
            it ?: throw ErrorEvent.LoadDataEvent()
        }.recoverCatching {
            throw ErrorEvent.LoadDataEvent()
        }
    }

    companion object {
        const val PRODUCT_LOAD_PAGING_SIZE = 20
        const val DEFAULT_ITEM_SIZE = 0
    }
}
