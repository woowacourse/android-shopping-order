package woowacourse.shopping.data.history

import woowacourse.shopping.data.common.ResponseHandlingUtils.handleResponse
import woowacourse.shopping.data.product.ProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.history.ProductHistoryRepository
import woowacourse.shopping.remote.product.ProductDto.Companion.toDomain

class DefaultProductHistoryRepository(
    private val productHistoryDataSource: ProductHistoryDataSource,
    private val productDataSource: ProductDataSource,
) : ProductHistoryRepository {
    override fun saveProductHistory(productId: Long) {
        productHistoryDataSource.saveProductHistory(productId)
    }

    override fun loadProductsHistory(): List<Product> {
        val productIds = productHistoryDataSource.fetchProductsHistory()
        return productIds.map { loadProduct(it) }
    }

    override fun loadProductHistory(productId: Long): Product {
        val id =
            productHistoryDataSource.fetchProductHistory(productId)
                ?: throw NoSuchElementException("there is no product history with id $productId")
        return loadProduct(id)
    }

    override fun loadLatestProduct(): Product {
        val productId: Long = productHistoryDataSource.fetchLatestProduct()
        return loadProduct(productId)
    }

    override fun deleteProductsHistory() {
        productHistoryDataSource.deleteProductsHistory()
    }

    private fun loadProduct(productId: Long): Product =
        handleResponse(productDataSource.loadById(productId)).toDomain(quantity = DEFAULT_QUANTITY)

    companion object {
        private const val TAG = "DefaultProductHistoryRe"
        private const val DEFAULT_QUANTITY = 0
    }
}
