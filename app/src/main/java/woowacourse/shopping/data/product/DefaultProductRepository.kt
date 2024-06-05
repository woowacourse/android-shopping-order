package woowacourse.shopping.data.product

import woowacourse.shopping.data.ResponseResult
import woowacourse.shopping.data.cart.CartItemDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.product.ProductRepository
import woowacourse.shopping.remote.product.ProductDto.Companion.toDomain

class DefaultProductRepository(
    private val productDataSource: ProductDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : ProductRepository {

    override fun loadAllProducts(page: Int): List<Product> {
        when(val response = productDataSource.findByPaged(page)) {
            is ResponseResult.Success -> {
                return response.data.content.map { productDto ->
                    productDto.toDomain(productQuantity(productDto.id))
                }
            }
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
    }

    override fun loadProduct(id: Long): Product {
        when(val response = productDataSource.findById(id)) {
            is ResponseResult.Success -> return response.data.toDomain(productQuantity(id))
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
    }

    override fun isFinalPage(page: Int): Boolean {
        when(val response = productDataSource.findByPaged(page)) {
            is ResponseResult.Success -> {
                return (page + 1) == response.data.totalPages
            }
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
    }

    override fun shoppingCartProductQuantity(): Int =
        when(val response = cartItemDataSource.loadAllCartItems()) {
            is ResponseResult.Success -> response.data.content.sumOf { it.quantity }
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }

    private fun productQuantity(productId: Long): Int {
        when(val response = cartItemDataSource.loadAllCartItems()) {
            is ResponseResult.Success -> {
                val cartItem = response.data.content.find { it.product.id == productId }
                return cartItem?.quantity ?: 0
            }
            is ResponseResult.Error -> throw IllegalStateException("${response.code}: 서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> throw IllegalStateException("${response.e}: 예기치 않은 오류가 발생했습니다.")
        }
    }

    companion object {
        private const val TAG = "DefaultShoppingProductR"
    }
}
