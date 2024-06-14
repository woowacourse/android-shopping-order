package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.remote.api.ShoppingRetrofit
import woowacourse.shopping.data.remote.api.handleApi
import woowacourse.shopping.data.remote.dto.request.RequestOrderPostDto
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result

class RemoteOrderDataSource : OrderDataSource {
    override suspend fun postOrder(request: RequestOrderPostDto): Result<Unit, DataError> =
        handleApi {
            ShoppingRetrofit.orderService.postOrders(request)
        }
}
