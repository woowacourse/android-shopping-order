package woowacourse.shopping.data.remote.shopping

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.remote.dto.response.ProductDto
import woowacourse.shopping.data.remote.dto.response.ProductResponse
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.repository.ProductRepository

class RemoteProductRepositoryImpl(private val remoteProductDataSource: RemoteProductDataSource = RemoteProductDataSourceImpl()) :
    ProductRepository {
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
