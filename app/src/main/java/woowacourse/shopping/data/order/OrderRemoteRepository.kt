package woowacourse.shopping.data.order

import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.cart.CartItemDataSource
import woowacourse.shopping.data.history.ProductHistoryDataSource
import woowacourse.shopping.data.product.ProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.order.OrderRepository
import woowacourse.shopping.remote.product.ProductDto
import woowacourse.shopping.remote.product.ProductDto.Companion.toDomain

class OrderRemoteRepository(
    private val orderDataSource: OrderRemoteDataSource,
    private val productDataSource: ProductDataSource,
    private val productHistoryDataSource: ProductHistoryDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : OrderRepository {
    override fun order(cartItemIds: List<Long>) {
        handleResponse(orderDataSource.order(cartItemIds))
    }

    override fun recommendedProducts(): List<Product> {
        val productId: Long = productHistoryDataSource.loadLatestProduct()
        val category: String = handleResponse(productDataSource.findById(productId)).category
        val cartItemsProductDto: List<ProductDto> = handleResponse(cartItemDataSource.loadAllCartItems()).content.map { it.product }

        return handleResponse(productDataSource.findByCategory(category)).content.filterNot { cartItemsProductDto.contains(it) }
            .map { productDto -> productDto.toDomain() }.take(10)
    }

    private fun <T : Any> handleResponse(response: ResponseResult<T>): T {
        return when (response) {
            is ResponseResult.Success -> response.data
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
    }
}
