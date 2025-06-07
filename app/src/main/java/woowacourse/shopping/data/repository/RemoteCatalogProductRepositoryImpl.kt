package woowacourse.shopping.data.repository

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

    override fun getRecommendedProducts(
        category: String,
        page: Int,
        size: Int,
        callback: (List<ProductUiModel>) -> Unit,
    ) {
        retrofitService
            .requestProducts(
                category = category,
                page = page,
                size = size,
            ).enqueue(
                object : Callback<ProductResponse> {
                    override fun onResponse(
                        call: Call<ProductResponse>,
                        response: Response<ProductResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val body: ProductResponse? = response.body()
                            val content: List<Content>? = body?.content
                            val products: List<ProductUiModel>? =
                                content?.mapNotNull {
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

                    override fun onFailure(
                        call: Call<ProductResponse>,
                        t: Throwable,
                    ) {
                        println("error : $t")
                    }
                },
            )
    }

    override fun getAllProductsSize(callback: (Int) -> Unit) {
        retrofitService.requestProducts().enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    if (response.isSuccessful) {
                        val body: ProductResponse? = response.body()
                        callback(body?.totalElements?.toInt() ?: 0)
                        println("body : $body")
                    }
                }

                override fun onFailure(
                    call: Call<ProductResponse>,
                    t: Throwable,
                ) {
                    println("error : $t")
                }
            },
        )
    }

    override fun getCartProductsByUids(
        uids: List<Int>,
        callback: (List<ProductUiModel>) -> Unit,
    ) {
        val resultsMap = mutableMapOf<Int, ProductUiModel>()
        var completedCount = 0

        if (uids.isEmpty()) {
            callback(emptyList())
            return
        }

        uids.forEach { uid ->
            getProduct(uid) { product ->
                resultsMap[uid] = product
                completedCount++

                if (completedCount == uids.size) {
                    val orderedResults = uids.mapNotNull { resultsMap[it] }
                    callback(orderedResults)
                }
            }
        }
    }

    override fun getProductsByPage(
        page: Int,
        size: Int,
        callback: (List<ProductUiModel>) -> Unit,
    ) {
        retrofitService
            .requestProducts(
                page = page,
                size = size,
            ).enqueue(
                object : Callback<ProductResponse> {
                    override fun onResponse(
                        call: Call<ProductResponse>,
                        response: Response<ProductResponse>,
                    ) {
                        if (response.isSuccessful) {
                            val body: ProductResponse? = response.body()
                            val content: List<Content>? = body?.content
                            val products: List<ProductUiModel>? =
                                content?.mapNotNull {
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

                    override fun onFailure(
                        call: Call<ProductResponse>,
                        t: Throwable,
                    ) {
                        println("error : $t")
                    }
                },
            )
    }

    override fun getProduct(
        id: Int,
        callback: (ProductUiModel) -> Unit,
    ) {
        retrofitService
            .requestDetailProduct(
                id = id,
            ).enqueue(
                object : Callback<Content> {
                    override fun onResponse(
                        call: Call<Content>,
                        response: Response<Content>,
                    ) {
                        if (response.isSuccessful) {
                            val body: Content = response.body() ?: return
                            val product =
                                ProductUiModel(
                                    id = body.id.toInt(),
                                    imageUrl = body.imageUrl,
                                    name = body.name,
                                    price = body.price,
                                    category = body.category,
                                )
                            callback(product)
                            println("body : $body")
                        }
                    }

                    override fun onFailure(
                        call: Call<Content>,
                        t: Throwable,
                    ) {
                        println("error : $t")
                    }
                },
            )
    }
}
