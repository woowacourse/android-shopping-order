package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.ApiHandleCartDataSource
import woowacourse.shopping.data.remote.api.ApiResult
import woowacourse.shopping.data.remote.api.ShoppingRetrofit
import woowacourse.shopping.data.remote.api.handleApi
import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto

class ApiHandleCartDataSourceImpl : ApiHandleCartDataSource {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): ApiResult<ResponseCartItemsGetDto> = handleApi { ShoppingRetrofit.cartItemService.getCartItems(page = page, size = size) }

    override suspend fun postCartItems(request: RequestCartItemPostDto): ApiResult<Unit> =
        handleApi { ShoppingRetrofit.cartItemService.postCartItem(request = request) }

    override suspend fun deleteCartItems(id: Long): ApiResult<Unit> =
        handleApi {
            ShoppingRetrofit.cartItemService.deleteCartItem(id = id)
        }

    override suspend fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    ): ApiResult<Unit> = handleApi { ShoppingRetrofit.cartItemService.patchCartItem(id = id, request = request) }

    override suspend fun getCartItemCounts(): ApiResult<ResponseCartItemCountsGetDto> =
        handleApi { ShoppingRetrofit.cartItemService.getCartItemCounts() }
}
