package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.remote.RemoteProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.remote.NetworkResult

class ProductRepositoryImpl(private val remoteProductDataSource: RemoteProductDataSource) : ProductRepository {
    override suspend fun loadWithCategory(
        category: String,
        startPage: Int,
        pageSize: Int,
    ): Result<List<Product>> {
        return runCatching {
            val result = remoteProductDataSource.getProductsByCategory(category, startPage, pageSize)
            when (result) {
                is NetworkResult.Success -> {
                    result.data.content.map { it.toProduct() }
                }
                is NetworkResult.Error -> {
                    throw result.exception
                }
            }
        }
    }

    override suspend fun load(
        startPage: Int,
        pageSize: Int,
    ): Result<List<Product>> {
        return runCatching {
            when (val result = remoteProductDataSource.getProducts(startPage, pageSize)) {
                is NetworkResult.Success -> {
                    result.data.content.map { it.toProduct() }
                }
                is NetworkResult.Error -> {
                    throw result.exception
                }
            }
        }
    }

    override suspend fun loadById(id: Long): Result<Product> {
        return runCatching {
            when (val result = remoteProductDataSource.getProductById(productId = id)) {
                is NetworkResult.Success -> {
                    result.data.toProduct()
                }
                is NetworkResult.Error -> {
                    throw result.exception
                }
            }
        }
    }
}
