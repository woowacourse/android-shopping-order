package woowacourse.shopping.remote.source

import woowacourse.shopping.data.model.CartItemData
import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.remote.model.request.CartItemRequest
import woowacourse.shopping.remote.model.response.toData
import woowacourse.shopping.remote.service.CartItemApiService

class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) :
    ShoppingCartDataSource {
    override suspend fun findByProductId2(productId: Long): Result<ProductIdsCountData> =
        runCatching {
            val allCartItems = cartItemApiService.requestCartItems2().content
            val find =
                allCartItems.find { it.product.id == productId }
                    ?: throw NoSuchElementException("there is no product with id $productId")
            ProductIdsCountData(find.product.id, find.quantity)
        }

    override suspend fun loadAllCartItems2(): Result<List<CartItemData>> =
        runCatching {
            val response = cartItemApiService.requestCartItems2().content
            response.map {
                CartItemData(
                    id = it.id,
                    quantity = it.quantity,
                    product = it.product.toData(),
                )
            }
        }

    override suspend fun addNewProduct2(productIdsCountData: ProductIdsCountData): Result<Unit> =
        runCatching {
            cartItemApiService.addCartItem2(
                CartItemRequest(
                    productIdsCountData.productId,
                    productIdsCountData.quantity,
                ),
            )
        }

    override suspend fun removeCartItem2(cartItemId: Long): Result<Unit> =
        runCatching {
            cartItemApiService.removeCartItem2(cartItemId)
        }

    override suspend fun plusProductsIdCount2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            cartItemApiService.updateCartItemQuantity2(cartItemId, quantity)
        }

    override suspend fun minusProductsIdCount2(
        cartItemId: Long,
        quantity: Int,
    ): Result<Unit> =
        runCatching {
            cartItemApiService.updateCartItemQuantity2(cartItemId, quantity)
        }

    override suspend fun updateProductsCount2(
        cartItemId: Long,
        newQuantity: Int,
    ): Result<Unit> =
        runCatching {
            cartItemApiService.updateCartItemQuantity2(cartItemId, newQuantity)
        }

    companion object {
        private const val TAG = "CartItemRemoteDataSource"
    }
}
