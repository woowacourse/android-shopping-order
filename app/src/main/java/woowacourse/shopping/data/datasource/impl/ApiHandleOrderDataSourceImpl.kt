package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.ApiHandleOrderDataSource
import woowacourse.shopping.data.remote.api.ApiResult
import woowacourse.shopping.data.remote.api.ShoppingRetrofit
import woowacourse.shopping.data.remote.api.handleApi
import woowacourse.shopping.data.remote.dto.request.RequestOrderPostDto

class ApiHandleOrderDataSourceImpl:ApiHandleOrderDataSource {
    override suspend fun postOrder(request: RequestOrderPostDto):ApiResult<Unit> = handleApi {
        ShoppingRetrofit.orderService.postOrders(request)
    }
}
