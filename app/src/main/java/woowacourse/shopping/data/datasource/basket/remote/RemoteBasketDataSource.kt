package woowacourse.shopping.data.datasource.basket.remote

import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import woowacourse.shopping.data.datasource.basket.BasketDataSource
import woowacourse.shopping.data.model.DataBasketProduct
import woowacourse.shopping.data.remote.OkHttpModule
import java.io.IOException

class RemoteBasketDataSource : BasketDataSource.Remote {
    override fun getAll(onReceived: (List<DataBasketProduct>) -> Unit) {
        val url = "${OkHttpModule.BASE_URL}/cart-items"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        OkHttpModule.shoppingOkHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {}

            override fun onResponse(call: Call, response: Response) {
                onReceived(
                    OkHttpModule.gson.fromJson(
                        response.body?.string(),
                        Array<DataBasketProduct>::class.java
                    ).toList()
                )
            }
        })
    }

    override fun getByProductId(productId: Int): DataBasketProduct? {
        TODO("Not yet implemented")
    }

    override fun add(basketProduct: DataBasketProduct) {
        TODO("Not yet implemented")
    }

    override fun minus(basketProduct: DataBasketProduct) {
        TODO("Not yet implemented")
    }

    override fun overWriteUpdate(basketProduct: DataBasketProduct) {
        TODO("Not yet implemented")
    }

    override fun remove(basketProduct: DataBasketProduct) {
        TODO("Not yet implemented")
    }
}
