package woowacourse.shopping.remote.source

import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.ProductIdsCount
import woowacourse.shopping.remote.service.CartItemApiService
import woowacourse.shopping.remote.model.CartItemRequest
import woowacourse.shopping.remote.model.CartItemDto

class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) :
    ShoppingCartDataSource {
    override fun findByProductId(productId: Long): ProductIdsCountData? {
        val allCartItems =
            cartItemApiService.requestCartItems().execute().body()?.content
                ?: throw IllegalStateException()
        val find = allCartItems.find { it.product.id == productId } ?: return null

        return ProductIdsCountData(find.product.id, find.quantity)
    }

    override fun loadAllCartItems(): List<CartItemDto> {
        val response =
            cartItemApiService.requestCartItems().execute().body()?.content
                ?: throw NoSuchElementException("there is no product")

        return response.map {
            CartItemDto(
                id = it.id,
                quantity = it.quantity,
                product = it.product,
            )
        }
    }

    override fun addNewProduct(productIdsCount: ProductIdsCount) {
        val call = cartItemApiService.addCartItem(
            CartItemRequest(
                productIdsCount.productId,
                productIdsCount.quantity,
            ),
        )
        call.execute()
    }

    override fun removeCartItem(cartItemId: Long) {
        cartItemApiService.removeCartItem(cartItemId).execute()
    }

    override fun plusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    ) {
        cartItemApiService.updateCartItemQuantity(cartItemId, quantity).execute()
    }

    override fun minusProductsIdCount(
        cartItemId: Long,
        quantity: Int,
    ) {
        cartItemApiService.updateCartItemQuantity(cartItemId, quantity).execute()
    }

    override fun updateProductsCount(cartItemId: Long, newQuantity: Int) {
        cartItemApiService.updateCartItemQuantity(cartItemId, newQuantity).execute()
    }

    companion object {
        private const val TAG = "CartItemRemoteDataSource"
    }
}
