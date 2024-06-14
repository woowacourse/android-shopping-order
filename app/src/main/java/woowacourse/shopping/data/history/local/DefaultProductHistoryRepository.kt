package woowacourse.shopping.data.history.local

import woowacourse.shopping.data.common.ApiResponseHandler.handleResponse
import woowacourse.shopping.data.history.local.datasource.ProductHistoryDataSource
import woowacourse.shopping.data.product.remote.datasource.ProductDataSource
import woowacourse.shopping.data.product.remote.dto.ProductDto.Companion.toDomain
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.history.ProductHistoryRepository

class DefaultProductHistoryRepository(
    private val productHistoryDataSource: ProductHistoryDataSource,
    private val productDataSource: ProductDataSource,
) : ProductHistoryRepository {
    override suspend fun saveProductHistory(productId: Long): Result<Long> {
        return runCatching {
            val id =
                productHistoryDataSource.saveProductHistory(productId).getOrElse {
                    throw NoSuchElementException()
                }
            id
        }
    }

    override suspend fun loadProductsHistory(): Result<List<Product>> {
        return runCatching {
            val productIds =
                productHistoryDataSource.fetchProductsHistory().getOrElse {
                    throw NoSuchElementException()
                }
            productIds.map { loadProduct(it) }
        }
    }

    override suspend fun loadProductHistory(productId: Long): Result<Product> {
        return runCatching {
            val id =
                productHistoryDataSource.fetchProductHistory(productId).getOrElse {
                    throw NoSuchElementException()
                } ?: throw NoSuchElementException("there is no product history with id $productId")
            loadProduct(id)
        }
    }

    override suspend fun loadLatestProduct(): Result<Product> {
        return runCatching {
            val productId =
                productHistoryDataSource.fetchLatestProduct().getOrElse {
                    throw NoSuchElementException("No product history available")
                }
            loadProduct(productId)
        }
    }

    override suspend fun deleteProductsHistory() {
        productHistoryDataSource.deleteProductsHistory()
    }

    private suspend fun loadProduct(productId: Long): Product =
        handleResponse(productDataSource.loadById(productId)).toDomain(quantity = DEFAULT_QUANTITY)

    companion object {
        private const val TAG = "DefaultProductHistoryRe"
        private const val DEFAULT_QUANTITY = 0
    }
}
