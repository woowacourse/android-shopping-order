package woowacourse.shopping.data.repository

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.product.Content
import woowacourse.shopping.data.dto.product.ProductResponse
import woowacourse.shopping.data.service.ProductService
import woowacourse.shopping.data.service.RetrofitProductService
import woowacourse.shopping.product.catalog.ProductUiModel

class RemoteCatalogProductRepositoryImpl : CatalogProductRepository {
    val retrofitService = RetrofitProductService.INSTANCE.create(ProductService::class.java)

    override fun getAllProductsSize(callback: (Int) -> Unit) {
        retrofitService.requestProducts().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                if (response.isSuccessful) {
                    val body: ProductResponse? = response.body()
                    callback(body?.totalElements?.toInt() ?: 0)
                    println("body : $body")
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                println("error : $t")
            }
        })
    }

    override fun getProductsInRange(
        startIndex: Int,
        endIndex: Int,
        callback: (List<ProductUiModel>) -> Unit
    ) {
        // 구현 안함
        retrofitService.requestProducts().enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                if (response.isSuccessful) {
                    val body = response.body()
                    println("body : $body")
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                println("error : $t")
            }
        })

    }

    override fun getCartProductsByUids(
        uids: List<Int>,
        callback: (List<ProductUiModel>) -> Unit
    ) {
        TODO("Not yet implemented")
    }

    override fun getProductsByPage(
        page: Int,
        size: Int,
        callback: (List<ProductUiModel>) -> Unit
    ) {
        retrofitService.requestProducts(
            page = page,
            size = size,
        ).enqueue(object : Callback<ProductResponse> {
            override fun onResponse(
                call: Call<ProductResponse>,
                response: Response<ProductResponse>
            ) {
                if (response.isSuccessful) {
                    val body: ProductResponse? = response.body()
                    val content: List<Content>? = body?.content
                    val products: List<ProductUiModel>? = content?.mapNotNull {
                        ProductUiModel(
                            id = it.id.toInt(),
                            imageUrl = it.imageUrl,
                            name = it.name,
                            price = it.price,
                        )
                    }
                    callback(products ?: emptyList())
                    println("body : $body")
                }
            }

            override fun onFailure(call: Call<ProductResponse>, t: Throwable) {
                println("error : $t")
            }
        })
    }
}
