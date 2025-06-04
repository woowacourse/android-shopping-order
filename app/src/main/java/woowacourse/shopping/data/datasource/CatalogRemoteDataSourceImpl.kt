package woowacourse.shopping.data.datasource

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.product.Content
import woowacourse.shopping.data.dto.product.ProductResponse
import woowacourse.shopping.data.service.ProductService

class CatalogRemoteDataSourceImpl(
    private val productService: ProductService,
) : CatalogRemoteDataSource {
    override fun fetchProducts(
        category: String?,
        page: Int,
        size: Int,
        onSuccess: (ProductResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        productService
            .requestProducts(category = category, page = page, size = size)
            .enqueue(
                object : Callback<ProductResponse> {
                    override fun onResponse(
                        call: Call<ProductResponse?>,
                        response: Response<ProductResponse?>,
                    ) {
                        Log.d("getProductsByPage", "page= $page, size = $size, DataSource: ${response.body()?.content}")
                        response.body()?.let { onSuccess(it) }
                            ?: onFailure(Throwable("추천 상품이 없습니다."))
                    }

                    override fun onFailure(
                        call: Call<ProductResponse?>,
                        t: Throwable,
                    ) {
                        onFailure(t)
                    }
                },
            )
    }

    override fun fetchAllProducts(
        onSuccess: (ProductResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        productService
            .requestProducts()
            .enqueue(
                object : Callback<ProductResponse> {
                    override fun onResponse(
                        call: Call<ProductResponse?>,
                        response: Response<ProductResponse?>,
                    ) {
                        response.body()?.let { onSuccess(it) }
                            ?: onFailure(Throwable("전체 상품이 없습니다."))
                    }

                    override fun onFailure(
                        call: Call<ProductResponse?>,
                        t: Throwable,
                    ) {
                        onFailure(t)
                    }
                },
            )
    }

    override fun fetchProductDetail(
        id: Long,
        onSuccess: (Content) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        productService
            .requestDetailProduct(id)
            .enqueue(
                object : Callback<Content> {
                    override fun onResponse(
                        call: Call<Content?>,
                        response: Response<Content?>,
                    ) {
                        response.body()?.let { onSuccess(it) }
                            ?: onFailure(Throwable("상품 상세가 없습니다."))
                    }

                    override fun onFailure(
                        call: Call<Content?>,
                        t: Throwable,
                    ) {
                        onFailure(t)
                    }
                },
            )
    }
}
