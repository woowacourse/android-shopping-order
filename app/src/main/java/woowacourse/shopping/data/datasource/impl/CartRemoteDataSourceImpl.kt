package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.data.service.CartItemService
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
            cartsDto ?: error("장바구니 정보를 불러올 수 없습니다.")
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
                    service.getCartItemCounts().execute().body() ?: error("장바구니 아이템 수량을 조회할 수 없습니다.")
            }.join()
            cartCountDto ?: error("장바구니 아이템 수량을 조회할 수 없습니다.")
        }
}
