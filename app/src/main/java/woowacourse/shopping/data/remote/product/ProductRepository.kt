package woowacourse.shopping.data.remote.product

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.util.toDomain

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

    fun fetchRecommendProducts(
        latestProductId: Long,
        cartProductIds: List<Long>,
        onSuccess: (List<CartProduct>) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        fetchAllProducts(
            onSuccess = { response ->
                val matchedProduct = response.content.find { it.id == latestProductId }
                val category = matchedProduct?.category

                if (category == null) {
                    onSuccess(emptyList())
                    return@fetchAllProducts
                }

                val recommendProducts =
                    response.content
                        .filter {
                            it.category == category &&
                                it.id != latestProductId &&
                                it.id !in cartProductIds
                        }.take(10)

                onSuccess(
                    recommendProducts.map {
                        CartProduct(id = 0, product = it.toDomain(), quantity = 0)
                    },
                )
            },
            onError = { onError(it) },
        )
    }

    fun fetchAllProducts(
        onSuccess: (ProductResponse) -> Unit,
        onError: (Throwable) -> Unit,
    ) {
        ProductClient
            .getRetrofitService()
            .requestGoods(size = Int.MAX_VALUE)
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

    fun requestProductDetails(
        onSuccess: (ProductResponse.Content) -> Unit,
        onError: (Throwable) -> Unit,
        productId: Long,
    ) {
        ProductClient
            .getRetrofitService()
            .requestProductDetails(productId = productId)
            .enqueue(
                object : Callback<ProductResponse.Content> {
                    override fun onResponse(
                        call: Call<ProductResponse.Content?>,
                        response: Response<ProductResponse.Content?>,
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
                        call: Call<ProductResponse.Content?>,
                        t: Throwable,
                    ) {
                        onError(t)
                    }
                },
            )
    }
}
