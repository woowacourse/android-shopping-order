package woowacourse.shopping.data.repository.real

import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.remote.source.ProductDataSourceImpl
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.utils.DtoMapper.toProduct

class RealProductRepositoryImpl(
    private val productDataSource: ProductDataSource = ProductDataSourceImpl(NetworkManager.getApiClient()),
) : ProductRepository {
    override suspend fun loadPagingProducts(offset: Int): Result<List<Product>> {
        val page = offset / PRODUCT_LOAD_PAGING_SIZE
        val response = productDataSource.loadProducts(page, PRODUCT_LOAD_PAGING_SIZE)
        return response.mapCatching { it ->
            it.productDto.map { it.toProduct() }
        }
    }

    override suspend fun loadCategoryProducts(
        size: Int,
        category: String,
    ): Result<List<Product>> {
        val response =
            productDataSource.loadCategoryProducts(
                page = DEFAULT_PAGE,
                size = RECOMMEND_PRODUCT_LOAD_PAGING_SIZE,
                category = category,
            )
        return response.mapCatching { it ->
            it.productDto.map { it.toProduct() }
        }
    }

    override suspend fun getProduct(productId: Long): Result<Product> {
        val response = productDataSource.loadProduct(productId.toInt())
        return response.mapCatching { it.toProduct() }
    }

    companion object {
        const val DEFAULT_PAGE = 0
        const val RECOMMEND_PRODUCT_LOAD_PAGING_SIZE = 10
        const val PRODUCT_LOAD_PAGING_SIZE = 20
    }
}
