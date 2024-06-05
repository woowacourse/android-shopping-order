package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.CartDataSource
import woowacourse.shopping.data.remote.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.remote.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.remote.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.data.remote.api.ShoppingRetrofit

class CartDataSourceImpl : CartDataSource {
    override fun getCartItems(
        page: Int,
        size: Int,
    ): ResponseCartItemsGetDto? = ShoppingRetrofit.cartItemService.getCartItems(page = page, size = size).execute().body()

    override fun postCartItems(request: RequestCartItemPostDto) {
        ShoppingRetrofit.cartItemService.postCartItem(request = request).execute().body()
    }

    override fun deleteCartItems(id: Long) {
        ShoppingRetrofit.cartItemService.deleteCartItem(id = id).execute().body()
    }

    override fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    ) {
        ShoppingRetrofit.cartItemService.patchCartItem(id = id, request = request).execute().body()
    }

    override fun getCartItemCounts(): ResponseCartItemCountsGetDto? = ShoppingRetrofit.cartItemService.getCartItemCounts().execute().body()
}
