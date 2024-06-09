package woowacourse.shopping.data.remote.repository

import android.util.Log
import woowacourse.shopping.data.remote.datasource.cartItem.CartItemDataSource
import woowacourse.shopping.data.remote.datasource.cartItem.DefaultCartItemDataSource
import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartItemRepository
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.utils.toIdOrNull

class CartItemRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource = DefaultCartItemDataSource()
): CartItemRepository {

    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> =
        runCatching {
            val response = cartItemDataSource.getCartItems(page, size)
            if (response.isSuccessful) {
                return Result.success(response.body()?.content?.map { it.toDomain() } ?: emptyList())
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun postCartItem(cartItemRequest: CartItemRequest): Result<Int> =
        runCatching {
            val response = cartItemDataSource.postCartItem(cartItemRequest)
            if (response.isSuccessful) {
                return Result.success(
                    response.toIdOrNull() ?: 0,
                )
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun patchCartItem(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Result<Unit> =
        runCatching {
            Log.d("SDFEFS", "${id}")
            val response = cartItemDataSource.patchCartItem(id, quantityRequestDto)
            if (response.isSuccessful) {
                return Result.success(Unit)
            }
            Log.d("SDFEFS", response.toString())
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun deleteCartItem(id: Int): Result<Unit> =
        runCatching {
            val response = cartItemDataSource.deleteCartItem(id)
            if (response.isSuccessful) {
                return Result.success(Unit)
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun getCartItemsCounts(): Result<Int> =
        runCatching {
            val response = cartItemDataSource.getCartItemsCounts()
            if (response.isSuccessful) {
                return Result.success(response.body()?.quantity ?: 0)
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }
}