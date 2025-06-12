package woowacourse.shopping.data.datasource.order

import retrofit2.HttpException
import woowacourse.shopping.data.api.OrderApi
import woowacourse.shopping.data.model.request.OrderProductsRequest

class OrderRemoteDataSourceImpl(
    private val api: OrderApi,
) : OrderRemoteDataSource {
    override suspend fun postOrderProducts(cartIds: List<Long>) {
        val request = OrderProductsRequest(cartIds)
        val response = api.postOrderProducts(request)

        if (!response.isSuccessful) throw HttpException(response)
    }
}
