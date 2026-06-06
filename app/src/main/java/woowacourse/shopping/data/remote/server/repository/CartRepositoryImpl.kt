package woowacourse.shopping.data.remote.server.repository

import kotlinx.coroutines.CancellationException
import retrofit2.HttpException
import woowacourse.shopping.data.remote.server.apiresult.ApiResult
import woowacourse.shopping.data.remote.server.dto.cart.items.PatchQuantityRequest
import woowacourse.shopping.data.remote.server.dto.cart.items.PostCartRequest
import woowacourse.shopping.data.remote.server.dto.cart.items.toDomain
import woowacourse.shopping.data.remote.server.service.CartService
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.domain.PurchaseProducts

class CartRepositoryImpl(
    private val cartService: CartService,
) : CartRepository {
    override suspend fun insert(purchaseProduct: PurchaseProduct): ApiResult<Unit> =
        try {
            cartService.postCartItems(
                PostCartRequest(
                    productId = purchaseProduct.id,
                    quantity = purchaseProduct.count,
                ),
            )
            ApiResult.Success(Unit)
        } catch (e: HttpException) {
            ApiResult.Error(e.code(), e.message)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ApiResult.Exception(e)
        }

    override suspend fun updateCount(
        cartItemId: Long,
        newQuantity: Int,
    ): ApiResult<Unit> =
        try {
            cartService.patchQuantity(
                cartItemId = cartItemId,
                request = PatchQuantityRequest(newQuantity),
            )
            ApiResult.Success(Unit)
        } catch (e: HttpException) {
            ApiResult.Error(e.code(), e.message)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ApiResult.Exception(e)
        }

    override suspend fun deleteCartItem(purchaseProductId: Long): ApiResult<Unit> =
        try {
            cartService.deleteProduct(
                productId = purchaseProductId,
            )
            ApiResult.Success(Unit)
        } catch (e: HttpException) {
            ApiResult.Error(e.code(), e.message)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ApiResult.Exception(e)
        }

    override suspend fun getProductCount(): ApiResult<Int> =
        try {
            val response = cartService.requestQuantity().quantity
            ApiResult.Success(response)
        } catch (e: HttpException) {
            ApiResult.Error(
                code = e.code(),
                message = e.message,
            )
        } catch (e: Exception) {
            ApiResult.Exception(e)
        } catch (e: CancellationException) {
            throw e
        }

    override suspend fun getPagedCart(
        page: Int,
        size: Int,
    ): ApiResult<PurchaseProducts> =
        try {
            val response = cartService.requestCartItems(page, size)
            val cartItems =
                response.content.map { content ->
                    content.toDomain()
                }
            ApiResult.Success(PurchaseProducts(cartItems))
        } catch (e: HttpException) {
            ApiResult.Error(e.code(), e.message)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ApiResult.Exception(e)
        }

    override suspend fun getCartItemCount(): ApiResult<Int> =
        try {
            val response = cartService.requestCartItems(0, 1).totalElements.toInt()
            ApiResult.Success(response)
        } catch (e: HttpException) {
            ApiResult.Error(e.code(), e.message)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            ApiResult.Exception(e)
        }
}
