// package woowacourse.shopping.data.common
//
// import com.google.android.gms.common.api.ApiException
// import kotlinx.coroutines.DelicateCoroutinesApi
// import kotlinx.coroutines.Dispatchers
// import kotlinx.coroutines.GlobalScope
// import kotlinx.coroutines.launch
// import kotlinx.coroutines.withContext
// import okhttp3.Request
// import okio.Timeout
// import retrofit2.Call
// import retrofit2.Callback
// import retrofit2.HttpException
// import retrofit2.Response
// import woowacourse.shopping.data.common.ApiResponseHandler.handleApiResponse
//
// class NetworkResultCall<T : Any>(
//    private val proxy: Call<T>
// ) : Call<ResponseResult<T>> {
//    @OptIn(DelicateCoroutinesApi::class)
//    override fun enqueue(callback: Callback<ResponseResult<T>>) {
//        proxy.enqueue(object : Callback<T> {
//            override fun onResponse(call: Call<T>, response: Response<T>) {
//                GlobalScope.launch {
//                    val networkResult = handleApiResponse { response }
//                    withContext(Dispatchers.Main) {
//                        callback.onResponse(this@NetworkResultCall, Response.success(networkResult))
//                    }
//                }
//            }
//
//            override fun onFailure(call: Call<T>, t: Throwable) {
//                val networkResponse = when (t) {
//                    is HttpException -> ResponseResult.ServerError(t)
//                    else -> {}
//                }
//                val networkResult = HttpException(t)
//                callback.onResponse(this@NetworkResultCall, Response.success(networkResult))
//            }
//        })
//    }
//
//    override fun execute(): Response<ResponseResult<T>> = throw NotImplementedError()
//    override fun clone(): Call<ResponseResult<T>> = NetworkResultCall(proxy.clone())
//    override fun request(): Request = proxy.request()
//    override fun timeout(): Timeout = proxy.timeout()
//    override fun isExecuted(): Boolean = proxy.isExecuted
//    override fun isCanceled(): Boolean = proxy.isCanceled
//    override fun cancel() { proxy.cancel() }
// }
