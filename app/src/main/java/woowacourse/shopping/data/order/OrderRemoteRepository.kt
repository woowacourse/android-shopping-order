package woowacourse.shopping.data.order

import woowacourse.shopping.data.ResponseResult
import woowacourse.shopping.data.cart.CartItemDataSource
import woowacourse.shopping.data.history.ProductHistoryDataSource
import woowacourse.shopping.data.product.ProductDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.order.OrderRepository
import woowacourse.shopping.remote.product.ProductDto.Companion.toDomain

class OrderRemoteRepository(
    private val orderDataSource: OrderRemoteDataSource,
    private val productDataSource: ProductDataSource,
    private val productHistoryDataSource: ProductHistoryDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : OrderRepository {

    override fun order(cartItemIds: List<Long>) {
        when(val response = orderDataSource.order(cartItemIds)) {
            is ResponseResult.Success -> return
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}:  예기치 않은 오류가 발생했습니다.")
        }
    }

    override fun recommendedProducts(): List<Product> {
        val productId: Long = productHistoryDataSource.loadLatestProduct()
        val category = when(val response = productDataSource.findById(productId)) {
            is ResponseResult.Success -> response.data.category
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
        val cartItems = when(val response = cartItemDataSource.loadAllCartItems()) {
            is ResponseResult.Success -> response.data.content.map { it.product }
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }

        return when(val response = productDataSource.findByCategory(category)) {
            is ResponseResult.Success -> response.data.content.filterNot { cartItems.contains(it) }.map { productDto -> productDto.toDomain() }.take(10)
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
    }
}
