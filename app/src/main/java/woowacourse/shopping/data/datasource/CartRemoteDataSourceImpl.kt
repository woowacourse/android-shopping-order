package woowacourse.shopping.data.datasource

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import woowacourse.shopping.data.dto.cartitem.ProductResponse
import woowacourse.shopping.data.dto.cartitem.Quantity
import woowacourse.shopping.data.dto.cartitem.UpdateCartItemRequest
import woowacourse.shopping.data.service.CartItemService

class CartRemoteDataSourceImpl(
    private val cartItemService: CartItemService,
) : CartRemoteDataSource {
    override suspend fun insertProduct(
        productId: Long,
        quantity: Int,
    ): Long {
        val response =
            cartItemService
                .postCartItem(
                    UpdateCartItemRequest(productId = productId, quantity = quantity),
                )

        return if (response.isSuccessful) {
            val locationHeader = response.headers()["location"]
            val id = locationHeader?.substringAfterLast("/")?.toLongOrNull()
            id ?: throw Exception("cartItemId 가져오기에 실패했습니다.")
        } else {
            throw Exception("서버 응답 실패 : ${response.code()}")
        }
    }

    override suspend fun deleteProduct(productId: Long) {
        cartItemService.deleteCartItem(productId)
    }

    override suspend fun fetchProducts(
        page: Int,
        size: Int,
    ): ProductResponse {
        val response =
            cartItemService.requestCartItems(
                page = page,
                size = size,
            )

        return if (response.isSuccessful) {
            response.body() ?: throw Exception("장바구니 상품 가져오기에 실패했습니다.")
        } else {
            throw Exception("서버 응답 실패 : ${response.code()}")
        }
    }

    override suspend fun updateProduct(
        cartItemId: Long,
        quantity: Int,
    ) = cartItemService.patchCartItemQuantity(
        cartItemId = cartItemId,
        quantity = Quantity(quantity),
    )

    override suspend fun fetchCartTotalElements(): Long =
        withContext(Dispatchers.IO) {
            val response =
                cartItemService.requestCartItems(
                    page = null,
                    size = null,
                )

            if (response.isSuccessful) {
                response.body()?.totalElements ?: throw Exception("totalElements가 없습니다.")
            } else {
                throw Exception("서버 응답 실패 : ${response.code()}")
            }
        }

    override suspend fun fetchCartItemsCount(): Int = cartItemService.getCartItemsCount().value
}
