package woowacourse.shopping.data.datasource.basket

import android.util.Log
import okhttp3.ResponseBody
import woowacourse.shopping.data.NetworkModule
import woowacourse.shopping.data.model.BasketProductEntity
import woowacourse.shopping.data.model.ProductEntity
import woowacourse.shopping.data.remote.OkHttpModule

class BasketRemoteDataSourceImpl : BasketRemoteDataSource {

    private val basketProductService: BasketProductService = NetworkModule.getService()

    override fun getAll(onReceived: (List<BasketProductEntity>) -> Unit) {
        basketProductService.requestBasketProducts(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo)
        ).enqueue(object : retrofit2.Callback<List<BasketProductEntity>> {

            override fun onResponse(
                call: retrofit2.Call<List<BasketProductEntity>>,
                response: retrofit2.Response<List<BasketProductEntity>>,
            ) {
                response.body()?.let {
                    onReceived(it)
                }
            }

            override fun onFailure(call: retrofit2.Call<List<BasketProductEntity>>, t: Throwable) {
            }
        })
    }

    override fun add(
        product: ProductEntity,
        onReceived: (Int) -> Unit,
    ) {
        basketProductService.addBasketProduct(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo),
            productId = product.id
        ).enqueue(object : retrofit2.Callback<retrofit2.Response<ResponseBody>> {

            override fun onResponse(
                call: retrofit2.Call<retrofit2.Response<ResponseBody>>,
                response: retrofit2.Response<retrofit2.Response<ResponseBody>>,
            ) {
                response.headers()["Location"]?.let {
                    val productId = it.split("/").last().toInt()

                    onReceived(productId)
                }
                Log.d("woogi", "onResponse: 상품 추가에 성공했습니다.")
            }

            override fun onFailure(
                call: retrofit2.Call<retrofit2.Response<ResponseBody>>,
                t: Throwable,
            ) {
                Log.d("woogi", "onFailure: ${t.message}")
                Log.d("woogi", "onResponse: 상품 추가에 실패했습니다.")
            }
        })
    }

    override fun update(basketProduct: BasketProductEntity) {
        basketProductService.updateBasketProduct(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo),
            cartItemId = basketProduct.id.toString(),
            quantity = basketProduct.count
        ).enqueue(object : retrofit2.Callback<retrofit2.Response<ResponseBody>> {

            override fun onResponse(
                call: retrofit2.Call<retrofit2.Response<ResponseBody>>,
                response: retrofit2.Response<retrofit2.Response<ResponseBody>>,
            ) {
                Log.d("woogi", "onResponse: 수량 변경에 성공했습니다.")
            }

            override fun onFailure(
                call: retrofit2.Call<retrofit2.Response<ResponseBody>>,
                t: Throwable,
            ) {
                Log.d("woogi", "onFailure: ${t.message}")
                Log.d("woogi", "onResponse: 수량 변경에 실패했습니다.")
            }
        })
    }

    override fun remove(basketProduct: BasketProductEntity) {
        basketProductService.removeBasketProduct(
            authorization = OkHttpModule.AUTHORIZATION_FORMAT.format(OkHttpModule.encodedUserInfo),
            cartItemId = basketProduct.id.toString(),
        ).enqueue(object : retrofit2.Callback<retrofit2.Response<ResponseBody>> {
            override fun onResponse(
                call: retrofit2.Call<retrofit2.Response<ResponseBody>>,
                response: retrofit2.Response<retrofit2.Response<ResponseBody>>,
            ) {
                Log.d("woogi", "onResponse: 상품 삭제에 성공했습니다.")
            }

            override fun onFailure(
                call: retrofit2.Call<retrofit2.Response<ResponseBody>>,
                t: Throwable,
            ) {
                Log.d("woogi", "onFailure: ${t.message}")
                Log.d("woogi", "onResponse: 상품 삭제에 실패했습니다.")
            }
        })
    }
}
