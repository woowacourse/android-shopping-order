package woowacourse.shopping.data.datasource.impl

import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.dto.request.RequestOrderPostDto
import woowacourse.shopping.data.service.ShoppingRetrofit

class OrderDataSourceImpl:OrderDataSource {
    override fun postOrder(request: RequestOrderPostDto) {
        ShoppingRetrofit.orderService.postOrders(request = request).execute().body()
    }
}
