package woowacourse.shopping.data.product.remote

import woowacourse.shopping.data.cart.remote.datasource.CartItemDataSource
import woowacourse.shopping.data.common.ApiResponseHandler.handleResponse
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.data.product.remote.datasource.ProductDataSource
import woowacourse.shopping.data.product.remote.dto.ProductDto.Companion.toDomain
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductsPage
import woowacourse.shopping.domain.repository.product.ProductRepository

class DefaultProductRepository(
    private val productDataSource: ProductDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : ProductRepository {
    override suspend fun loadProducts(page: Int): ResponseResult<ProductsPage> {
        return when (val response = productDataSource.loadByPaged(page)) {
            is ResponseResult.Success -> {
                val products =
                    response.data.content.map { productDto ->
                        productDto.toDomain(findCartItemQuantity(productDto.id))
                    }
                val isFinalPage: Boolean = (page + 1) == response.data.totalPages
                ResponseResult.Success(ProductsPage(products, isFinalPage))
            }
            is ResponseResult.ServerError -> ResponseResult.ServerError(response.code, "서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> ResponseResult.Exception(response.e, "예기치 않은 오류가 발생했습니다")
        }
    }

    override suspend fun loadProduct(id: Long): ResponseResult<Product> {
        return when (val response = productDataSource.loadById(id)) {
            is ResponseResult.Success -> {
                val data = response.data.toDomain(findCartItemQuantity(id))
                ResponseResult.Success(data)
            }
            is ResponseResult.ServerError -> ResponseResult.ServerError(response.code, "서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> ResponseResult.Exception(response.e, "예기치 않은 오류가 발생했습니다")
        }
    }

    private suspend fun findCartItemQuantity(productId: Long): Int =
        handleResponse(cartItemDataSource.fetchCartItems()).content.find { it.product.id == productId }?.quantity ?: DEFAULT_QUANTITY

    companion object {
        private const val TAG = "DefaultShoppingProductR"
        private const val DEFAULT_QUANTITY = 0
    }
}
