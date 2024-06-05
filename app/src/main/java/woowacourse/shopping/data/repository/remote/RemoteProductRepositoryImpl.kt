package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.ProductDataSourceImpl
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.utils.DtoMapper.toProduct
import woowacourse.shopping.utils.LatchUtils.executeWithLatch
import woowacourse.shopping.utils.exception.NoSuchDataException

class RemoteProductRepositoryImpl(
    private val productDataSource: ProductDataSource = ProductDataSourceImpl(),
) : ProductRepository {
    override fun loadPagingProducts(offset: Int): Result<List<Product>> {
        return executeWithLatch {
            val page = offset / PRODUCT_LOAD_PAGING_SIZE
            val response = productDataSource.loadProducts(page, PRODUCT_LOAD_PAGING_SIZE).execute()
            if (response.isSuccessful && response.body() != null) {
                response.body()?.productDto?.map { it.toProduct() }
            } else {
                throw NoSuchDataException()
            }
        }
    }

    override fun loadCategoryProducts(
        size: Int,
        category: String,
    ): Result<List<Product>> {
        return executeWithLatch {
            val response = productDataSource.loadCategoryProducts(DEFAULT_PAGE, RECOMMEND_PRODUCT_LOAD_PAGING_SIZE, category).execute()
            if (response.isSuccessful && response.body() != null) {
                response.body()?.productDto?.map { it.toProduct() }
            } else {
                throw NoSuchDataException()
            }
        }
    }

    override fun getProduct(productId: Long): Result<Product> {
        return executeWithLatch {
            val response = productDataSource.loadProduct(productId.toInt()).execute()
            if (response.isSuccessful && response.body() != null) {
                response.body()?.toProduct()
            } else {
                throw NoSuchDataException()
            }
        }
    }

    companion object {
        const val DEFAULT_PAGE = 0
        const val RECOMMEND_PRODUCT_LOAD_PAGING_SIZE = 10
        const val PRODUCT_LOAD_PAGING_SIZE = 20
    }
}
