package woowacourse.shopping.data.history

import woowacourse.shopping.data.product.ProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.history.ProductHistoryRepository
import woowacourse.shopping.remote.product.ProductDto
import woowacourse.shopping.remote.product.ProductDto.Companion.toDomain

class DefaultProductHistoryRepository(
    private val productHistoryDataSource: ProductHistoryDataSource,
    private val productDataSource: ProductDataSource,
) : ProductHistoryRepository {
    override fun saveProductHistory(productId: Long) {
        productHistoryDataSource.saveProductHistory(productId)
    }

    override fun loadAllProductHistory(): List<Product> {
        val productIds = productHistoryDataSource.loadAllProductHistory()
        return productIds.map {
            productDataSource.findById(it).toDomain(quantity = 0)
        }
    }

    override fun loadProductHistory(productId: Long): Product {
        val id =
            productHistoryDataSource.loadProductHistory(productId)
                ?: throw NoSuchElementException("there is no product history with id $productId")
        val productDto: ProductDto = productDataSource.findById(id)
        return productDto.toDomain(quantity = 0)
    }

    override fun loadLatestProduct(): Product {
        val productId: Long = productHistoryDataSource.loadLatestProduct()
        val productDto: ProductDto = productDataSource.findById(productId)
        return productDto.toDomain(quantity = 0)
    }

    override fun deleteAllProductHistory() {
        productHistoryDataSource.deleteAllProductHistory()
    }

    companion object {
        private const val TAG = "DefaultProductHistoryRe"
    }
}
