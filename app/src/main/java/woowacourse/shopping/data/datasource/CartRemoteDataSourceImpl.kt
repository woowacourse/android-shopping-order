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
    ): Long =
        withContext(Dispatchers.IO) {
            val response =
                cartItemService
                    .postCartItem(
                        UpdateCartItemRequest(productId = productId, quantity = quantity),
                    )

            if (response.isSuccessful) {
                productId
            } else {
                throw Exception("서버 응답 실패 : ${response.code()}")
            }
        }

    override suspend fun deleteProduct(productId: Long) {
        withContext(Dispatchers.IO) {
            val response = cartItemService.deleteCartItem(productId)

            if (!response.isSuccessful) {
                throw Exception("상품 삭제 실패 : ${response.code()}")
            }
        }
    }

    override suspend fun fetchProducts(
        page: Int,
        size: Int,
    ): ProductResponse =
        withContext(Dispatchers.IO) {
            val response =
                cartItemService.requestCartItems(
                    page = page,
                    size = size,
                )

            if (response.isSuccessful) {
                response.body() ?: throw Exception("장바구니 상품 가져오기에 실패했습니다.")
            } else {
                throw Exception("서버 응답 실패 : ${response.code()}")
            }
        }

    override suspend fun updateProduct(
        cartItemId: Long,
        quantity: Int,
    ) = withContext(Dispatchers.IO) {
        val response =
            cartItemService.patchCartItemQuantity(
                cartItemId = cartItemId,
                quantity = Quantity(quantity),
            )

        if (!response.isSuccessful) {
            throw Exception("장바구니 상품 업데이트 실패 : ${response.code()}")
        }
    }

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

    override suspend fun fetchCartItemsCount(): Int =
        withContext(Dispatchers.IO) {
            val response = cartItemService.getCartItemsCount()

            if (response.isSuccessful) {
                response.body()?.value ?: throw Exception("장바구니 수량이 없습니다.")
            } else {
                throw Exception("서버 응답 실패 : ${response.code()}")
            }
        }
}
