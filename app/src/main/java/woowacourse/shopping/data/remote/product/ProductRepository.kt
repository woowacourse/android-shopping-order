package woowacourse.shopping.data.remote.product

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductRepository {
    fun fetchProducts(
        onSuccess: (ProductResponse) -> Unit,
        onError: (Throwable) -> Unit,
        page: Int,
    ) {
        ProductClient
            .getRetrofitService()
            .requestGoods(page = page)
            .enqueue(
                object : Callback<ProductResponse> {
                    override fun onResponse(
                        call: Call<ProductResponse>,
                        response: Response<ProductResponse>,
                    ) {
                        if (response.isSuccessful) {
                            response.body()?.let {
                                onSuccess(it)
                            } ?: onError(Throwable("응답 본문 없음"))
                        } else {
                            onError(Throwable("응답 실패: ${response.code()}"))
                        }
                    }

                    override fun onFailure(
                        call: Call<ProductResponse>,
                        t: Throwable,
                    ) {
                        onError(t)
                    }
                },
            )
    }
}
