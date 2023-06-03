package woowacourse.shopping.data.remote.shoppingCart

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dataSource.ShoppingCartDataSource
import woowacourse.shopping.data.remote.ServicePool
import woowacourse.shopping.data.remote.dto.request.InsertingProductDto
import woowacourse.shopping.data.remote.dto.request.UpdatingProductDto
import woowacourse.shopping.data.remote.dto.response.ShoppingCartDto
import woowacourse.shopping.domain.util.Error
import woowacourse.shopping.domain.util.WoowaResult
import woowacourse.shopping.util.enqueueUtil

class ShoppingCartRemoteDataSource : ShoppingCartDataSource {
    override fun fetchAll(callback: (WoowaResult<List<ShoppingCartDto>>) -> Unit) {
        ServicePool.retrofitService.getAllCartItems().enqueueUtil(
            onSuccess = { callback(WoowaResult.SUCCESS(it)) },
            onFailure = { callback(WoowaResult.FAIL(it)) },
        )
    }

    override fun delete(callback: (WoowaResult<Boolean>) -> Unit, id: Long) {
        ServicePool.retrofitService.deleteCartItem(id).enqueueUtil(
            onSuccess = { callback(WoowaResult.SUCCESS(true)) },
            onFailure = {
                when (it) {
                    is Error.ResponseBodyNull -> callback(WoowaResult.SUCCESS(true))
                    else -> callback(WoowaResult.FAIL(it))
                }
            },
        )
    }

    override fun insert(callback: (WoowaResult<Long>) -> Unit, productId: Long, quantity: Int) {
        val TAG = "INSERT"
        ServicePool.retrofitService.insertCartItem(InsertingProductDto(productId, quantity))
            .enqueue(object :
                Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful) {
                        val id: Long? = response.headers()["Location"]
                            ?.substringAfterLast("/")
                            ?.toLong()
                        if (id != null) {
                            callback(WoowaResult.SUCCESS(id))
                            return
                        }
                        callback(WoowaResult.FAIL(Error.ResponseBodyNull))
                        Log.e(TAG, "code: ${response.code()}, messsage: ${response.message()}")
                        return
                    }
                    callback(WoowaResult.FAIL(Error.ResponseFailure))
                    Log.e(TAG, "code: ${response.code()}, messsage: ${response.message()}")
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callback(WoowaResult.FAIL(Error.ServerConnectError))
                    Log.e(TAG, "throwable: ${t.cause}, messsage: ${t.message}")
                }
            })
    }

    override fun update(
        callback: (WoowaResult<Boolean>) -> Unit,
        id: Long,
        updatedQuantity: Int,
    ) {
        ServicePool.retrofitService
            .updateCartItem(id, UpdatingProductDto(updatedQuantity))
            .enqueueUtil(
                onSuccess = { callback(WoowaResult.SUCCESS(true)) },
                onFailure = { callback(WoowaResult.FAIL(it)) },
            )
    }
}
