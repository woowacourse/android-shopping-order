package woowacourse.shopping.data.history

import woowacourse.shopping.data.common.ResponseResult
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

    override fun loadAllProductHistory(): List<Product> {
        val productIds = productHistoryDataSource.fetchProductsHistory()
        return productIds.map { handleResponse(it) }
    }

    override fun loadProductHistory(productId: Long): Product {
        val id =
            productHistoryDataSource.fetchProductHistory(productId)
                ?: throw NoSuchElementException("there is no product history with id $productId")
        return handleResponse(id)
    }

    override fun loadLatestProduct(): Product {
        val productId: Long = productHistoryDataSource.fetchLatestProduct()
        return handleResponse(productId)
    }

    private fun handleResponse(productId: Long): Product {
        return when (val response = productDataSource.loadById(productId)) {
            is ResponseResult.Success -> response.data.toDomain(quantity = DEFAULT_QUANTITY)
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
    }

    override fun deleteAllProductHistory() {
        productHistoryDataSource.deleteProductsHistory()
    }

    companion object {
        private const val TAG = "DefaultProductHistoryRe"
        private const val DEFAULT_QUANTITY = 0
    }
}
