package woowacourse.shopping.data.repository

import woowacourse.shopping.data.db.product.MockProductService
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.service.ProductService

class ProductRepositoryImpl : ProductRepository {
    private val mockProductService: ProductService = MockProductService()

    override suspend fun loadPagingProducts(offset: Int): Result<List<Product>> {
        return Result.runCatching {
            mockProductService.findPagingProducts(
                offset,
                PRODUCT_LOAD_PAGING_SIZE
            )
        }
    }

    override suspend fun loadCategoryProducts(
        size: Int,
        category: String,
    ): Result<List<Product>> {
        val pagingData =
            mockProductService.findPagingProducts(DEFAULT_ITEM_SIZE, PRODUCT_LOAD_PAGING_SIZE)
        return Result.runCatching {
            pagingData.filter { product ->
                product.category == category
            }
        }
    }

    override suspend fun getProduct(productId: Long): Result<Product> {
        return Result.runCatching { mockProductService.findProductById(productId) }
    }

    companion object {
        const val PRODUCT_LOAD_PAGING_SIZE = 20
        const val DEFAULT_ITEM_SIZE = 0
    }
}
