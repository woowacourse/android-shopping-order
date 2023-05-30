package woowacourse.shopping.data.datasource.basket.remote

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.basket.BasketDataSource
import woowacourse.shopping.data.model.DataBasketProduct
import woowacourse.shopping.data.model.DataProduct
import woowacourse.shopping.data.remote.RetrofitModule
import woowacourse.shopping.data.remote.request.BasketAddRequest
import java.io.IOException
import okhttp3.Call as okhttpCall
import okhttp3.Callback as okhttpCallback
import okhttp3.Response as okhttpResponse

class RemoteBasketDataSource : BasketDataSource.Remote {
    override fun getAll(onReceived: (List<DataBasketProduct>) -> Unit) {
        RetrofitModule.basketService.getAll().enqueue(
            object : Callback<List<DataBasketProduct>> {
                override fun onResponse(
                    call: Call<List<DataBasketProduct>>,
                    response: Response<List<DataBasketProduct>>
                ) {
                    onReceived(response.body() ?: emptyList())
                }

                override fun onFailure(call: Call<List<DataBasketProduct>>, t: Throwable) {}
            }
        )
    }

    override fun add(product: DataProduct, onReceived: (Int) -> Unit) {
        RetrofitModule.basketService.add(BasketAddRequest(product.id)).enqueue(
            object : Callback<Unit> {
                override fun onResponse(
                    call: Call<Unit>,
                    response: Response<Unit>
                ) {
                    val basketId = response.headers()["Location"]?.split("/")?.last()?.toInt()

                    basketId?.let {
                        onReceived(it)
                    }
                }
                override fun onFailure(call: Call<Unit>, t: Throwable) {}
            }
        )
    }

    override fun update(basketProduct: DataBasketProduct) {
        val url = "${RetrofitModule.BASE_URL}/cart-items/${basketProduct.id}"
        val requestBody = "{\"quantity\":\"${basketProduct.count.value}\"}"
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url(url)
            .patch(requestBody)
            .build()

        RetrofitModule.shoppingOkHttpClient.newCall(request).enqueue(object : okhttpCallback {
            override fun onFailure(call: okhttpCall, e: IOException) {}
            override fun onResponse(call: okhttpCall, response: okhttpResponse) {}
        })
    }

    override fun remove(basketProduct: DataBasketProduct) {
        val url = "${RetrofitModule.BASE_URL}/cart-items/${basketProduct.id}"
        val request = Request.Builder()
            .url(url)
            .delete()
            .build()

        RetrofitModule.shoppingOkHttpClient.newCall(request).enqueue(object : okhttpCallback {
            override fun onFailure(call: okhttpCall, e: IOException) {}

            override fun onResponse(call: okhttpCall, response: okhttpResponse) {}
        })
    }
}
