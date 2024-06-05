package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.CartRemoteDataSource
import woowacourse.shopping.data.dto.request.RequestCartItemPostDto
import woowacourse.shopping.data.dto.request.RequestCartItemsPatchDto
import woowacourse.shopping.data.dto.response.ResponseCartItemCountsGetDto
import woowacourse.shopping.data.dto.response.ResponseCartItemsGetDto
import woowacourse.shopping.data.service.CartItemService
import kotlin.concurrent.thread

class CartRemoteDataSourceImpl(private val service: CartItemService) : CartRemoteDataSource {
    override suspend fun getCartItems(
        page: Int,
        size: Int,
    ): Result<ResponseCartItemsGetDto> =
        runCatching {
            service.getCartItems(page = page, size = size)
        }

    override suspend fun postCartItems(request: RequestCartItemPostDto): Result<Unit> =
        runCatching {
            service.postCartItem(request = request)
        }

    override fun deleteCartItems(id: Long): Result<Unit> =
        runCatching {
            thread {
                service.deleteCartItem(id = id).execute().body()
            }.join()
        }

    override suspend fun patchCartItems(
        id: Long,
        request: RequestCartItemsPatchDto,
    ): Result<Unit> =
        runCatching {
            service.patchCartItem(id = id, request = request)
        }

    override suspend fun getCartItemCounts(): Result<ResponseCartItemCountsGetDto> =
        runCatching {
            service.getCartItemCounts()
        }
}
