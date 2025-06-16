package woowacourse.shopping.data.remote.cart

class CartRepository(
    private val cartService: CartService,
) {
    suspend fun fetchAllCart(): Result<CartResponse?> =
        try {
            val response = cartService.requestCart(page = null, size = null)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun fetchCart(
        page: Int?,
        size: Int?,
    ): Result<CartResponse?> =
        try {
            val response = cartService.requestCart(page = page, size = size)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun addToCart(cartRequest: CartRequest): Result<Long> =
        runCatching {
            val response = cartService.addToCart(cartRequest = cartRequest)

            val locationHeader =
                response
                    .headers()["Location"]
                    ?.split("/")
                    ?.last()
                    ?.toLongOrNull()
            if (locationHeader == null) throw Exception("Location header not found")
            locationHeader
        }

    suspend fun deleteCart(id: Long): Result<Unit> =
        try {
            cartService.deleteFromCart(id = id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun updateCart(
        id: Long,
        cartQuantity: CartQuantity,
    ): Result<Unit> =
        try {
            cartService.updateCart(id = id, cartQuantity = cartQuantity)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }

    suspend fun getCartCounts(): Result<CartQuantity> =
        try {
            val response = cartService.getCartCounts()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
}
