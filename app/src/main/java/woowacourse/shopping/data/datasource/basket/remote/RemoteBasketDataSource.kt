package woowacourse.shopping.data.datasource.basket.remote

import android.util.Log
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.NullOnEmptyConvertFactory
import woowacourse.shopping.data.datasource.basket.BasketDataSource
import woowacourse.shopping.data.model.DataBasketProduct
import woowacourse.shopping.data.model.DataProduct
import woowacourse.shopping.data.remote.OkHttpModule

class RemoteBasketDataSource : BasketDataSource.Remote {

    private val url = OkHttpModule.BASE_URL

    private val basketProductService = Retrofit.Builder()
        .baseUrl(url)
        .addConverterFactory(NullOnEmptyConvertFactory)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(BasketProductService::class.java)

    override fun getAll(onReceived: (List<DataBasketProduct>) -> Unit) {
        basketProductService.requestBasketProducts(
            // todo: userInfo 어떻게 관리할지도 생각
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo)
        ).enqueue(object : retrofit2.Callback<List<DataBasketProduct>> {

            override fun onResponse(
                call: retrofit2.Call<List<DataBasketProduct>>,
                response: retrofit2.Response<List<DataBasketProduct>>
            ) {
                response.body()?.let {
                    onReceived(it)
                }
                Log.d("woogi", "onResponse: 장바구니 상품 조회에 성공했습니다.")
            }

            override fun onFailure(call: retrofit2.Call<List<DataBasketProduct>>, t: Throwable) {
                Log.d("woogi", "onFailure: ${t.message}")
                Log.d("woogi", "onResponse: 장바구니 상품 조회에 실패했습니다.")
            }
        })
    }

    override fun add(
        product: DataProduct,
        onReceived: (Int) -> Unit
    ) {
        basketProductService.addBasketProduct(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo),
            productId = product.id
        ).enqueue(object : retrofit2.Callback<retrofit2.Response<ResponseBody>> {

            override fun onResponse(
                call: retrofit2.Call<retrofit2.Response<ResponseBody>>,
                response: retrofit2.Response<retrofit2.Response<ResponseBody>>
            ) {
                response.headers()["Location"]?.let {
                    val productId = it.split("/").last().toInt()

                    onReceived(productId)
                }
                Log.d("woogi", "onResponse: 상품 추가에 성공했습니다.")
            }

            override fun onFailure(
                call: retrofit2.Call<retrofit2.Response<ResponseBody>>,
                t: Throwable
            ) {
                Log.d("woogi", "onFailure: ${t.message}")
                Log.d("woogi", "onResponse: 상품 추가에 실패했습니다.")
            }
        })
    }

    override fun update(basketProduct: DataBasketProduct) {
        basketProductService.updateBasketProduct(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo),
            cartItemId = basketProduct.id.toString(),
            quantity = basketProduct.count.value
        ).enqueue(object : retrofit2.Callback<retrofit2.Response<ResponseBody>> {

            override fun onResponse(
                call: retrofit2.Call<retrofit2.Response<ResponseBody>>,
                response: retrofit2.Response<retrofit2.Response<ResponseBody>>
            ) {
                Log.d("woogi", "onResponse: 수량 변경에 성공했습니다.")
            }

            override fun onFailure(
                call: retrofit2.Call<retrofit2.Response<ResponseBody>>,
                t: Throwable
            ) {
                Log.d("woogi", "onFailure: ${t.message}")
                Log.d("woogi", "onResponse: 수량 변경에 실패했습니다.")
            }
        })
    }

    override fun remove(basketProduct: DataBasketProduct) {
        basketProductService.removeBasketProduct(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo),
            cartItemId = basketProduct.id.toString(),
        ).enqueue(object : retrofit2.Callback<retrofit2.Response<ResponseBody>> {
            override fun onResponse(
                call: retrofit2.Call<retrofit2.Response<ResponseBody>>,
                response: retrofit2.Response<retrofit2.Response<ResponseBody>>
            ) {
                Log.d("woogi", "onResponse: 상품 삭제에 성공했습니다.")
            }

            override fun onFailure(
                call: retrofit2.Call<retrofit2.Response<ResponseBody>>,
                t: Throwable
            ) {
                Log.d("woogi", "onFailure: ${t.message}")
                Log.d("woogi", "onResponse: 상품 삭제에 실패했습니다.")
            }
        })
    }
}
