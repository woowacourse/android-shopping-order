package woowacourse.shopping.data.remote.product

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.domain.model.Product

class ProductRepository {
    fun fetchProducts(
        onSuccess: (List<Product>) -> Unit,
        onError: (Throwable) -> Unit
    ) {
        ProductClient.getRetrofitService().requestGoods().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(call: Call<ProductResponse>, response: Response<ProductResponse>) {
                if (response.isSuccessful) {
                    onSuccess(response.body()?.content.orEmpty())
                } else {
                    onError(Throwable("응답 실패"))
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                onError(t)
            }
        })
    }
}