package woowacourse.shopping.data.history

import woowacourse.shopping.data.ResponseResult
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
        val productIds = productHistoryDataSource.loadAllProductHistory()

        return productIds.map {
            when(val response = productDataSource.findById(it)) {
                is ResponseResult.Success -> response.data.toDomain(quantity = DEFAULT_QUANTITY)
                is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
                is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
            }
        }
    }

    override fun loadProductHistory(productId: Long): Product {
        val id =
            productHistoryDataSource.loadProductHistory(productId)
                ?: throw NoSuchElementException("there is no product history with id $productId")
        return when(val response = productDataSource.findById(id)) {
            is ResponseResult.Success -> response.data.toDomain(quantity = DEFAULT_QUANTITY)
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
    }

    override fun loadLatestProduct(): Product {
        val productId: Long = productHistoryDataSource.loadLatestProduct()
        return when(val response = productDataSource.findById(productId)) {
            is ResponseResult.Success -> response.data.toDomain(quantity = DEFAULT_QUANTITY)
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
    }

    override fun deleteAllProductHistory() {
        productHistoryDataSource.deleteAllProductHistory()
    }

    companion object {
        private const val TAG = "DefaultProductHistoryRe"
        private const val DEFAULT_QUANTITY = 0
    }
}
