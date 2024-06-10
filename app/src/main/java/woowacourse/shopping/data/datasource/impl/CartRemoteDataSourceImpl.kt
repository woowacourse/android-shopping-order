package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.data.remote.service.CartItemService
import woowacourse.shopping.exception.ShoppingError
import woowacourse.shopping.exception.ShoppingException

class CartRemoteDataSourceImpl(private val service: CartItemService) : CartRemoteDataSource {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<ResponseCartItemsGetDto> =
        runCatching {
            service.getCartItems(page = page, size = size).body() ?: throw ShoppingException(
                ShoppingError.CartNotFound,
            )
        }

    override suspend fun postCartItems(request: RequestCartItemPostDto): Result<Unit> =
        runCatching {
            service.postCartItem(request = request).body()
        }

    override suspend fun deleteCartItems(id: Long): Result<Unit> =
        runCatching {
            service.deleteCartItem(id = id).body()
        }

    override suspend fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    ): Result<Unit> =
        runCatching {
            service.patchCartItem(id = id, request = request).body()
        }

    override suspend fun getCartItemCounts(): Result<ResponseCartItemCountsGetDto> =
        runCatching {
            service.getCartItemCounts().body()
                ?: throw ShoppingException(ShoppingError.CartItemCountConfirmError)
        }
}
