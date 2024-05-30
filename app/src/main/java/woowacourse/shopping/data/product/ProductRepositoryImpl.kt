package woowacourse.shopping.data.product

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.response.ProductDto
import woowacourse.shopping.data.dto.response.ProductResponse
import woowacourse.shopping.data.product.remote.RemoteProductDataSource
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImpl(private val remoteProductDataSource: RemoteProductDataSource = RemoteProductDataSource()) :
    ProductRepository {
    override fun loadWithCategory(
        category: String,
        startPage: Int,
        pageSize: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteProductDataSource.loadWithCategory(category, startPage, pageSize).enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it.content.map { it.toProduct() })
                        } ?: onSuccess(emptyList())
                    } else {
                    }
                }

                override fun onFailure(
                    call: Call<ProductResponse>,
                    t: Throwable,
                ) {
                    onFailure()
                }
            },
        )
    }

    override fun load(
        startPage: Int,
        pageSize: Int,
        onSuccess: (List<Product>) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteProductDataSource.load(startPage, pageSize).enqueue(
            object : Callback<ProductResponse> {
                override fun onResponse(
                    call: Call<ProductResponse>,
                    response: Response<ProductResponse>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it.content.map { it.toProduct() })
                        } ?: onSuccess(emptyList())
                    }
                }

                override fun onFailure(
                    call: Call<ProductResponse>,
                    t: Throwable,
                ) {
                    onFailure()
                }
            },
        )
    }

    override fun loadById(
        id: Long,
        onSuccess: (Product) -> Unit,
        onFailure: () -> Unit,
    ) {
        remoteProductDataSource.loadById(id).enqueue(
            object : Callback<ProductDto> {
                override fun onResponse(
                    call: Call<ProductDto>,
                    response: Response<ProductDto>,
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let {
                            onSuccess(it.toProduct())
                        }
                    } else {
                        onFailure()
                    }
                }

                override fun onFailure(
                    call: Call<ProductDto>,
                    t: Throwable,
                ) {
                    onFailure()
                }
            },
        )
    }
}
