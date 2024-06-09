package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.remote.api.ApiResponse
import woowacourse.shopping.data.remote.api.ShoppingRetrofit
import woowacourse.shopping.data.remote.api.handleApi
import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto

class RemoteCartDataSource : CartDataSource {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): ApiResponse<ResponseCartItemsGetDto> = handleApi { ShoppingRetrofit.cartItemService.getCartItems(page = page, size = size) }

    override suspend fun postCartItems(request: RequestCartItemPostDto): ApiResponse<Unit> =
        handleApi { ShoppingRetrofit.cartItemService.postCartItem(request = request) }

    override suspend fun deleteCartItems(id: Long): ApiResponse<Unit> =
        handleApi {
            ShoppingRetrofit.cartItemService.deleteCartItem(id = id)
        }

    override suspend fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    ): ApiResponse<Unit> = handleApi { ShoppingRetrofit.cartItemService.patchCartItem(id = id, request = request) }

    override suspend fun getCartItemCounts(): ApiResponse<ResponseCartItemCountsGetDto> =
        handleApi { ShoppingRetrofit.cartItemService.getCartItemCounts() }
}
