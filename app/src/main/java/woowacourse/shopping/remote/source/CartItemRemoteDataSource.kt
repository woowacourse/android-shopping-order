package woowacourse.shopping.remote.source

import woowacourse.shopping.data.model.CartItemData
import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.remote.model.request.CartItemRequest
import woowacourse.shopping.remote.model.response.toData
import woowacourse.shopping.remote.service.CartItemApiService

class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) :
    ShoppingCartDataSource {
    override suspend fun findByProductId(productId: Long): Result<ProductIdsCountData> =
        runCatching {
            val allCartItems = cartItemApiService.requestCartItems().content
            val find =
                allCartItems.find { it.product.id == productId }
                    ?: throw NoSuchElementException("there is no product with id $productId")
            ProductIdsCountData(find.product.id, find.quantity)
        }

    override suspend fun findCartItemByProductId(productId: Long): Result<CartItemData> =
        runCatching {
            val allCartItems = cartItemApiService.requestCartItems().content
            val find =
                allCartItems.find { it.product.id == productId }
                    ?: throw NoSuchElementException("there is no product with id $productId")
            CartItemData(find.id, find.quantity, find.product.toData())
        }

    override suspend fun loadAllCartItems(): Result<List<CartItemData>> =
        runCatching {
            val response = cartItemApiService.requestCartItems().content
            response.map {
                CartItemData(
                    id = it.id,
                    quantity = it.quantity,
                    product = it.product.toData(),
                )
            }
        }

    override suspend fun addNewProduct(productIdsCountData: ProductIdsCountData): Result<Unit> =
        runCatching {
            cartItemApiService.addCartItem(
                CartItemRequest(
                    productIdsCountData.productId,
                    productIdsCountData.quantity,
                ),
            )
        }

    override suspend fun removeCartItem(cartItemId: Long): Result<Unit> =
        runCatching {
            cartItemApiService.removeCartItem(cartItemId)
        }

    override suspend fun updateProductsCount(
        cartItemId: Long,
        newQuantity: Int,
    ): Result<Unit> =
        runCatching {
            cartItemApiService.updateCartItemQuantity(cartItemId, newQuantity)
        }

    companion object {
        private const val TAG = "CartItemRemoteDataSource"
    }
}
