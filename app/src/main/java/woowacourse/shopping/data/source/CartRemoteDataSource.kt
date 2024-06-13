package woowacourse.shopping.data.source

import woowacourse.shopping.data.model.CartItemData
import woowacourse.shopping.data.model.ProductIdsCountData
import woowacourse.shopping.remote.model.request.toRequest
import woowacourse.shopping.remote.model.response.CartItemResponse
import woowacourse.shopping.remote.model.response.toData
import woowacourse.shopping.remote.service.CartApiService

class CartRemoteDataSource(
    private val cartApi: CartApiService,
) : ShoppingCartDataSource {
    override suspend fun findByProductId(productId: Long): Result<ProductIdsCountData> = runCatching {
        val allCartItems = cartApi.requestCartItems().content
        val foundItem = allCartItems.find { it.product.id == productId }
            ?: throw NoSuchElementException("there is no product with id $productId")
        ProductIdsCountData(foundItem.product.id, foundItem.quantity)
    }

    override suspend fun findCartItemByProductId(productId: Long): Result<CartItemData> = runCatching {
        val allCartItems = cartApi.requestCartItems().content
        val find = allCartItems.find { it.product.id == productId }
            ?: throw NoSuchElementException("there is no product with id $productId")

        CartItemData(find.id, find.quantity, find.product.toData())
    }


    override suspend fun loadAllCartItems(): Result<List<CartItemData>> = runCatching {
        val response = cartApi.requestCartItems().content
        response.map(CartItemResponse::toData)
    }

    override suspend fun addNewProduct(productIdsCountData: ProductIdsCountData): Result<Unit> = runCatching {
        cartApi.addCartItem(productIdsCountData.toRequest())
    }

    override suspend fun removeCartItem(cartItemId: Long): Result<Unit> = runCatching {
        cartApi.removeCartItem(cartItemId)
    }

    override suspend fun updateProductsCount(cartItemId: Long, newQuantity: Int): Result<Unit> = runCatching {
        cartApi.updateCartItemQuantity(cartItemId, newQuantity)
    }
}

