package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.data.service.CartItemService
import woowacourse.shopping.exception.ShoppingError
import woowacourse.shopping.exception.ShoppingException
import kotlin.concurrent.thread

class CartRemoteDataSourceImpl(private val service: CartItemService) : CartRemoteDataSource {
    override fun getCartItems(
        page: Int,
        size: Int,
    ): Result<ResponseCartItemsGetDto> =
        runCatching {
            var cartsDto: ResponseCartItemsGetDto? = null
            thread {
                cartsDto = service.getCartItems(page = page, size = size).execute().body()
            }.join()
            cartsDto ?: throw ShoppingException(ShoppingError.CartNotFound)
        }

    override fun postCartItems(request: RequestCartItemPostDto): Result<Unit> =
        runCatching {
            thread {
                service.postCartItem(request = request).execute().body()
            }.join()
        }

    override fun deleteCartItems(id: Long): Result<Unit> =
        runCatching {
            thread {
                service.deleteCartItem(id = id).execute().body()
            }.join()
        }

    override fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    ): Result<Unit> =
        runCatching {
            thread {
                service.patchCartItem(id = id, request = request).execute().body()
            }.join()
        }

    override fun getCartItemCounts(): Result<ResponseCartItemCountsGetDto> =
        runCatching {
            var cartCountDto: ResponseCartItemCountsGetDto? = null
            thread {
                cartCountDto =
                    service.getCartItemCounts().execute().body()
                        ?: throw ShoppingException(ShoppingError.CartItemCountConfirmError)
            }.join()
            cartCountDto ?: throw ShoppingException(ShoppingError.CartItemCountConfirmError)
        }
}
