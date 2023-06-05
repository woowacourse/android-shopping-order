package woowacourse.shopping.data.datasource.basket.remote

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.datasource.basket.BasketDataSource
import woowacourse.shopping.data.httpclient.RetrofitModule
import woowacourse.shopping.data.httpclient.request.BasketAddRequest
import woowacourse.shopping.data.httpclient.request.BasketUpdateRequest
import woowacourse.shopping.data.model.DataBasketProduct
import woowacourse.shopping.data.model.DataProduct

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
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
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
        RetrofitModule.basketService.update(
            cartItemId = basketProduct.id,
            body = BasketUpdateRequest(basketProduct.count.value)
        ).enqueue(
            object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
                override fun onFailure(call: Call<Unit>, t: Throwable) {}
            }
        )
    }

    override fun delete(basketProduct: DataBasketProduct) {
        RetrofitModule.basketService.delete(cartItemId = basketProduct.id)
            .enqueue(
                object : Callback<Unit> {
                    override fun onResponse(call: Call<Unit>, response: Response<Unit>) {}
                    override fun onFailure(call: Call<Unit>, t: Throwable) {}
                }
            )
    }
}
