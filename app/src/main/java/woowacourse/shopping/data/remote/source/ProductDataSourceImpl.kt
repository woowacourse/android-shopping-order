package woowacourse.shopping.data.remote.source

import woowacourse.shopping.data.remote.api.NetworkManager
import woowacourse.shopping.data.remote.api.ProductApiService
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.domain.model.product.Product
import woowacourse.shopping.utils.mapper.ProductMapper.toProduct
import woowacourse.shopping.utils.mapper.ProductMapper.toProducts

class ProductDataSourceImpl(
    private val productApiService: ProductApiService = NetworkManager.productService(),
) : ProductDataSource {
    override suspend fun loadProducts(
        page: Int,
        size: Int,
    ): Result<List<Product>> {
        return runCatching {
            productApiService.requestProducts(
                page = page,
                size = size,
            ).toProducts()
        }
    }

    override suspend fun loadCategoryProducts(
        page: Int,
        size: Int,
        category: String,
    ): Result<List<Product>> {
        return runCatching {
            productApiService.requestCategoryProducts(
                page = page,
                size = size,
                category = category,
            ).toProducts()
        }
    }

    override suspend fun loadProduct(id: Int): Result<Product> {
        return runCatching {
            productApiService.requestProduct(id = id).toProduct()
        }
    }
}
