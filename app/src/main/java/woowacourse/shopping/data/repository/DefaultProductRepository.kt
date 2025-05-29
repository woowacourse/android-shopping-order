package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.ProductsDataSource
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.domain.product.ProductSinglePage
import woowacourse.shopping.domain.repository.ProductRepository

class DefaultProductRepository(
    private val dataSource: ProductsDataSource,
) : ProductRepository {
    override fun loadSinglePage(
        page: Int,
        pageSize: Int,
        callback: (Result<ProductSinglePage>) -> Unit,
    ) {
        dataSource.singlePage(page, pageSize) { result ->
            result.fold(
                onSuccess = { response ->
                    if (response != null) {
                        val productSinglePage = response.toDomain()
                        callback(Result.success(productSinglePage))
                    } else {
                        callback(Result.failure(NullPointerException()))
                    }
                },
                onFailure = { throwable ->
                    callback(Result.failure(throwable))
                },
            )
        }
    }

    override fun loadProduct(
        productId: Long,
        callback: (Result<Product>) -> Unit,
    ) {
        dataSource.getProduct(productId) { result ->
            result.fold(
                onSuccess = { response ->
                    if (response != null) {
                        val productSinglePage = response.toDomain()
                        callback(Result.success(productSinglePage))
                    } else {
                        callback(Result.failure(NullPointerException()))
                    }
                },
                onFailure = { throwable ->
                    callback(Result.failure(throwable))
                },
            )
        }
    }
}
