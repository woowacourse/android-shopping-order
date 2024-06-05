package woowacourse.shopping.data.repository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import woowacourse.shopping.data.datasource.RemoteProductDataSource
import woowacourse.shopping.data.model.Product
import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.model.toProductDomain
import woowacourse.shopping.data.model.toProductItemDomain
import woowacourse.shopping.domain.model.ProductDomain
import woowacourse.shopping.domain.model.ProductItemDomain
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val remoteProductDataSource: RemoteProductDataSource,
) : ProductRepository {
    override fun getProducts(
        category: String?,
        page: Int,
        size: Int,
        sort: String,
        onSuccess: (ProductDomain) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        remoteProductDataSource.getProducts(category, page, size, sort).enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    onSuccess(response.body()?.toProductDomain() ?: throw HttpException(response))
                }

                override fun onFailure(
                    call: Call<ProductResponse>,
                    t: Throwable,
                ) {
                    onFailure(t)
                }
            },
        )
//        thread {
//            runCatching {
//                val response = remoteProductDataSource.getProducts(category, page, size, sort).execute()
//                response.body()?.toProductDomain() ?: throw HttpException(response)
//            }.onSuccess(onSuccess).onFailure(onFailure)
//        }
    }

    override fun getProductById(
        id: Int,
        onSuccess: (ProductItemDomain) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        remoteProductDataSource.getProductById(id).enqueue(
            object : Callback<Product> {
                override fun onResponse(
                    call: Call<Product>,
                    response: Response<Product>,
                ) {
                    onSuccess(response.body()?.toProductItemDomain() ?: throw HttpException(response))
                }

                override fun onFailure(
                    call: Call<Product>,
                    t: Throwable,
                ) {
                    onFailure(t)
                }
            },
        )
//        thread {
//            runCatching {
//                val response = remoteProductDataSource.getProductById(id).execute()
//
//            }.onSuccess(onSuccess).onFailure(onFailure)
//        }
    }
}
