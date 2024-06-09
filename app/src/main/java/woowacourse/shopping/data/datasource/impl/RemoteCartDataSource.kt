package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.remote.api.ShoppingRetrofit
import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto

class RemoteCartDataSource : CartDataSource {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): ResponseCartItemsGetDto =
        ShoppingRetrofit.cartItemService.getCartItems(page, size).body() ?: error("body가 비어있습니다.")

    override suspend fun postCartItems(request: RequestCartItemPostDto) {
        ShoppingRetrofit.cartItemService.postCartItem(request)
    }

    override suspend fun deleteCartItems(id: Long) {
        ShoppingRetrofit.cartItemService.deleteCartItem(id)
    }

    override suspend fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    ) {
        ShoppingRetrofit.cartItemService.patchCartItem(id, request)
    }

    override suspend fun getCartItemCounts(): ResponseCartItemCountsGetDto =
        ShoppingRetrofit.cartItemService.getCartItemCounts().body() ?: error("body가 비어있습니다.")
}
