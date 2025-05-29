package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.api.ProductApi
import woowacourse.shopping.data.mapper.toDomain
import woowacourse.shopping.data.model.response.ProductDetailResponse
import woowacourse.shopping.data.model.response.ProductsResponse
import woowacourse.shopping.domain.model.Page
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.ProductDetail.Companion.EMPTY_PRODUCT_DETAIL
import woowacourse.shopping.domain.model.Products
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepository(
    private val api: ProductApi,
) : ProductRepository {
    override fun fetchCatalogProduct(
        productId: Long,
        callback: (Result<Product?>) -> Unit,
    ) {
        api.getProductDetail(productId).enqueue(
            object : Callback<ProductDetailResponse> {
                override fun onResponse(
                    call: Call<ProductDetailResponse>,
                    response: Response<ProductDetailResponse>,
                ) {
                    val detail = response.body()?.toDomain() ?: EMPTY_PRODUCT_DETAIL
                    callback(Result.success(Product(detail)))
                }

                override fun onFailure(
                    call: Call<ProductDetailResponse>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    override fun fetchCatalogProducts(
        productIds: List<Int>,
        callback: (Result<List<Product>>) -> Unit,
    ) {
    }

    override fun fetchProducts(
        page: Int,
        size: Int,
        category: String?,
        callback: (Result<Products>) -> Unit,
    ) {
        api.getProducts(category, page, size).enqueue(
            object : Callback<ProductsResponse> {
                override fun onResponse(
                    call: Call<ProductsResponse>,
                    response: Response<ProductsResponse>,
                ) {
                    val body = response.body()
                    val items = body?.content?.map { it.toDomain() } ?: emptyList()
                    val pageInfo = Page(page, body?.first ?: false, body?.last ?: false)
                    callback(Result.success(Products(items, pageInfo)))
                }

                override fun onFailure(
                    call: Call<ProductsResponse>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }

    override fun fetchAllProducts(callback: (Result<List<Product>>) -> Unit) {
        val firstPage = 0
        val maxSize = Int.MAX_VALUE

        api.getProducts(page = firstPage, size = maxSize).enqueue(
            object : Callback<ProductsResponse> {
                override fun onResponse(
                    call: Call<ProductsResponse>,
                    response: Response<ProductsResponse>,
                ) {
                    val products = response.body()?.content?.map { it.toDomain() } ?: emptyList()
                    callback(Result.success(products))
                }

                override fun onFailure(
                    call: Call<ProductsResponse>,
                    t: Throwable,
                ) {
                    callback(Result.failure(t))
                }
            },
        )
    }
}
