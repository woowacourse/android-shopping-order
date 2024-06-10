package woowacourse.shopping.data.remote.repository

import woowacourse.shopping.data.remote.datasource.cartItem.CartItemDataSource
import woowacourse.shopping.data.remote.datasource.cartItem.DefaultCartItemDataSource
import woowacourse.shopping.data.remote.dto.mapper.toDomain
import woowacourse.shopping.data.remote.dto.request.CartItemRequest
import woowacourse.shopping.data.remote.dto.request.QuantityRequest
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.repository.CartItemRepository
import woowacourse.shopping.utils.toIdOrNull

class CartItemRepositoryImpl(
    private val cartItemDataSource: CartItemDataSource = DefaultCartItemDataSource(),
) : CartItemRepository {
    override suspend fun getAllByPaging(
        page: Int,
        size: Int,
    ): Result<List<CartProduct>> =
        runCatching {
            val response = cartItemDataSource.getAllByPaging(page, size)
            if (response.isSuccessful) {
                return Result.success(response.body()?.content?.map { it.toDomain() } ?: emptyList())
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun post(cartItemRequest: CartItemRequest): Result<Int> =
        runCatching {
            val response = cartItemDataSource.post(cartItemRequest)
            if (response.isSuccessful) {
                return Result.success(
                    response.toIdOrNull() ?: 0,
                )
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun patch(
        id: Int,
        quantityRequestDto: QuantityRequest,
    ): Result<Unit> =
        runCatching {
            val response = cartItemDataSource.patch(id, quantityRequestDto)
            if (response.isSuccessful) {
                return Result.success(Unit)
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun delete(id: Int): Result<Unit> =
        runCatching {
            val response = cartItemDataSource.delete(id)
            if (response.isSuccessful) {
                return Result.success(Unit)
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }

    override suspend fun getCount(): Result<Int> =
        runCatching {
            val response = cartItemDataSource.getCount()
            if (response.isSuccessful) {
                return Result.success(response.body()?.quantity ?: 0)
            }
            return Result.failure(Throwable(response.errorBody().toString()))
        }
}
