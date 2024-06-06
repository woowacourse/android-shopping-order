package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import woowacourse.shopping.data.datasource.RemoteOrderDataSource
import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.CartItemIds
import woowacourse.shopping.domain.repository.OrderRepository

class OrderRepositoryImpl(
    private val remoteOrderDataSource: RemoteOrderDataSource,
) : OrderRepository {
    override suspend fun postOrder(cartItemIds: List<Int>): Result<Unit> {
        return runCatching {
            remoteOrderDataSource.postOrder(CartItemIds(cartItemIds))
        }
    }
    //    override fun postOrder(
//        cartItemIds: List<Int>,
//        onSuccess: () -> Unit,
//        onFailure: (Throwable) -> Unit,
//    ) {
//        remoteOrderDataSource.postOrder(CartItemIds(cartItemIds)).enqueue(
//            object : Callback<Unit> {
//                override fun onResponse(
//                    call: Call<Unit>,
//                    response: Response<Unit>,
//                ) {
//                    if (response.code() != 201) throw HttpException(response)
//                    onSuccess()
//                }
//
//                override fun onFailure(
//                    call: Call<Unit>,
//                    t: Throwable,
//                ) {
//                    onFailure(t)
//                }
//            },
//        )
//        thread {
//            runCatching {
//                val response = remoteOrderDataSource.postOrder(CartItemIds(cartItemIds)).execute()
//                if (response.code() != 201) throw HttpException(response)
//            }.onSuccess {
//                onSuccess()
//            }.onFailure(onFailure)
//        }
//}
}
