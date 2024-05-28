package woowacourse.shopping.data.product.remote.retrofit

import woowacourse.shopping.data.remote.RetrofitClient.retrofitApi
import woowacourse.shopping.domain.datasource.ProductDataSource
import woowacourse.shopping.domain.model.PagingProduct
import java.util.concurrent.CompletableFuture

class RemoteProductDataSource : ProductDataSource {
    override fun getPagingProduct(
        page: Int,
        size: Int,
    ): CompletableFuture<PagingProduct> {
        val productResponse = retrofitApi.requestProducts(page = page, size = size)
        return productResponse.handle { result, throwable ->
            if (result != null) return@handle result.toPagingProduct()
            throw error("error")
        }
    }
}
