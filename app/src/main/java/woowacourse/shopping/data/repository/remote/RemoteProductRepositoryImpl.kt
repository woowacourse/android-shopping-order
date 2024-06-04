package woowacourse.shopping.data.repository.remote

import woowacourse.shopping.data.remote.source.ProductDataSourceImpl
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.utils.DtoMapper.toProduct
import woowacourse.shopping.utils.DtoMapper.toProducts
import woowacourse.shopping.utils.exception.LatchUtils.executeWithLatch
import woowacourse.shopping.utils.exception.ErrorEvent

class RemoteProductRepositoryImpl(
    private val productDataSource: ProductDataSource = ProductDataSourceImpl(),
) : ProductRepository {
    override fun loadPagingProducts(offset: Int): Result<List<Product>> {
        var products: List<Product>? = null
        executeWithLatch {
            val page = offset / PRODUCT_LOAD_PAGING_SIZE
            val response =
                productDataSource.loadProducts(page, PRODUCT_LOAD_PAGING_SIZE).execute()
            if (response.isSuccessful && response.body() != null) {
                products = response.body()?.toProducts()
            }
        }
        return Result.success(products ?: throw ErrorEvent.LoadDataEvent())
    }

    override fun loadCategoryProducts(
        size: Int,
        category: String,
    ): Result<List<Product>> {
        var products: List<Product>? = null
        executeWithLatch {
            val response =
                productDataSource.loadCategoryProducts(
                    page = DEFAULT_PAGE,
                    size = RECOMMEND_PRODUCT_LOAD_PAGING_SIZE,
                    category = category,
                ).execute()
            if (response.isSuccessful && response.body() != null) {
                products = response.body()?.toProducts()
            }
        }
        return Result.success(products ?: throw ErrorEvent.LoadDataEvent())
    }

    override fun getProduct(productId: Long): Result<Product> {
        var product: Product? = null
        executeWithLatch {
            val response = productDataSource.loadProduct(productId.toInt()).execute()
            if (response.isSuccessful && response.body() != null) {
                product = response.body()?.toProduct()
            }
        }
        return Result.success(product ?: throw ErrorEvent.LoadDataEvent())
    }

    companion object {
        const val DEFAULT_PAGE = 0
        const val RECOMMEND_PRODUCT_LOAD_PAGING_SIZE = 10
        const val PRODUCT_LOAD_PAGING_SIZE = 20
    }
}
