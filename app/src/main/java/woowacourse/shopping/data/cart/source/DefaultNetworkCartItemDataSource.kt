package woowacourse.shopping.data.cart.source

class DefaultNetworkCartItemDataSource(
    private val cartItemRemoteService: CartItemRemoteService
) : NetworkCartItemDataSource {
    override fun loadCartItems(): Result<List<NetworkCartItem>> {
        return kotlin.runCatching {
            val response = cartItemRemoteService.requestCartItems().execute()
            if (response.isSuccessful.not()) throw Throwable(response.errorBody()?.string())
            response.body() ?: throw Throwable(response.message())
        }
    }

    override fun saveCartItem(productId: Long): Result<Long> {
        return kotlin.runCatching {
            val response = cartItemRemoteService.requestToSave(
                CartItemSaveRequestBody(productId)
            ).execute()
            if (response.isSuccessful.not()) throw Throwable(response.errorBody()?.toString())
            response.headers()["Location"]
                ?.substringAfterLast("/")
                ?.toLong()
                ?: throw Throwable(response.message())
        }
    }

    override fun updateCartItemQuantity(cartItemId: Long, quantity: Int): Result<Unit> {
        return kotlin.runCatching {
            val response = cartItemRemoteService.requestToUpdateQuantity(
                cartItemId,
                CartItemQuantityUpdateRequestBody(quantity)
            ).execute()
            if (response.isSuccessful.not()) throw Throwable(response.errorBody()?.toString())
        }
    }

    override fun deleteCartItem(cartItemId: Long): Result<Unit> {
        return kotlin.runCatching {
            val response = cartItemRemoteService.requestToDelete(
                cartItemId,
            ).execute()
            if (response.isSuccessful.not()) throw Throwable(response.errorBody()?.toString())
        }
    }
}
