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
    override suspend fun saveProductHistory(productId: Long) {
        productHistoryDataSource.saveProductHistory(productId)
    }

    override suspend fun loadProductsHistory(): List<Product> {
        val productIds = productHistoryDataSource.fetchProductsHistory()
        return productIds.map { loadProduct(it) }
    }

    override suspend fun loadProductHistory(productId: Long): Product {
        val id =
            productHistoryDataSource.fetchProductHistory(productId)
                ?: throw NoSuchElementException("there is no product history with id $productId")
        return loadProduct(id)
    }

    override suspend fun loadLatestProduct(): Product {
        val productId: Long = productHistoryDataSource.fetchLatestProduct()
        return loadProduct(productId)
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
