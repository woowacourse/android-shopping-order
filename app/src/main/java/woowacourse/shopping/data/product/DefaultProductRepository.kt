package woowacourse.shopping.data.product

import woowacourse.shopping.data.cart.CartItemDataSource
import woowacourse.shopping.data.common.ResponseHandlingUtils.handleResponse
import woowacourse.shopping.data.common.ResponseResult
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductsPage
import woowacourse.shopping.domain.repository.product.ProductRepository
import woowacourse.shopping.remote.product.ProductDto.Companion.toDomain

class DefaultProductRepository(
    private val productDataSource: ProductDataSource,
    private val cartItemDataSource: CartItemDataSource,
) : ProductRepository {
    override suspend fun loadProducts(page: Int): ResponseResult<ProductsPage> {
        return when (val response = productDataSource.loadByPaged(page)) {
            is ResponseResult.Success -> {
                val products = response.data.content.map { productDto ->
                    productDto.toDomain(findCartItemQuantity(productDto.id))
                }
                val isFinalPage: Boolean = (page + 1) == response.data.totalPages
                ResponseResult.Success(ProductsPage(products, isFinalPage))
            }
            is ResponseResult.Error -> ResponseResult.Error(response.code, "서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> ResponseResult.Exception(response.e)
        }
    }

    override suspend fun loadProduct(id: Long): ResponseResult<Product> {
        return when(val response = productDataSource.loadById(id)) {
            is ResponseResult.Success -> {
                val data = response.data.toDomain(findCartItemQuantity(id))
                ResponseResult.Success(data)
            }
            is ResponseResult.Error -> ResponseResult.Error(response.code, "서버와 통신 중에 오류가 발생했습니다.")
            is ResponseResult.Exception -> ResponseResult.Exception(response.e)
        }
    }

    private suspend fun findCartItemQuantity(productId: Long): Int =
        handleResponse(cartItemDataSource.fetchCartItems()).content.find { it.product.id == productId }?.quantity ?: DEFAULT_QUANTITY

    companion object {
        private const val TAG = "DefaultShoppingProductR"
        private const val DEFAULT_QUANTITY = 0
    }
}