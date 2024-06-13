package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.remote.api.ShoppingRetrofit
import woowacourse.shopping.data.remote.api.handleApi
import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result

class RemoteCartDataSource : CartDataSource {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<ResponseCartItemsGetDto, DataError> =
        handleApi { ShoppingRetrofit.cartItemService.getCartItems(page = page, size = size) }

    override suspend fun postCartItems(request: RequestCartItemPostDto): Result<Unit, DataError> =
        handleApi { ShoppingRetrofit.cartItemService.postCartItem(request = request) }

    override suspend fun deleteCartItems(id: Long): Result<Unit, DataError> =
        handleApi {
            ShoppingRetrofit.cartItemService.deleteCartItem(id = id)
        }

    override suspend fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    ): Result<Unit, DataError> =
        handleApi { ShoppingRetrofit.cartItemService.patchCartItem(id = id, request = request) }

    override suspend fun getCartItemCounts(): Result<ResponseCartItemCountsGetDto, DataError> =
        handleApi { ShoppingRetrofit.cartItemService.getCartItemCounts() }
}
