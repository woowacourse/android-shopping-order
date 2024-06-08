package woowacourse.shopping.remote.source

import woowacourse.shopping.data.model.CartItemData
import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.remote.model.request.CartItemRequest
import woowacourse.shopping.remote.model.response.toData
import woowacourse.shopping.remote.service.CartItemApiService

class CartItemRemoteDataSource(private val cartItemApiService: CartItemApiService) :
    ShoppingCartDataSource {
    override fun findByProductId(productId: Long): ProductIdsCountData? {
        val allCartItems =
            cartItemApiService.requestCartItems().execute().body()?.content
                ?: throw IllegalStateException()
        val find = allCartItems.find { it.product.id == productId } ?: return null

        return ProductIdsCountData(find.product.id, find.quantity)
    }

    override fun loadAllCartItems(): List<CartItemData> {
        val response =
            cartItemApiService.requestCartItems().execute().body()?.content
                ?: throw NoSuchElementException("there is no product")

        return response.map {
            CartItemData(
                id = it.id,
                quantity = it.quantity,
                product = it.product.toData(),
            )
        }
    }

    override fun addNewProduct(productIdsCountData: ProductIdsCountData) {
        val call =
            cartItemApiService.addCartItem(
                CartItemRequest(
                    productIdsCountData.productId,
                    productIdsCountData.quantity,
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

    override fun updateProductsCount(
        cartItemId: Long,
        newQuantity: Int,
    ) {
        cartItemApiService.updateCartItemQuantity(cartItemId, newQuantity).execute()
    }

    companion object {
        private const val TAG = "CartItemRemoteDataSource"
    }
}
