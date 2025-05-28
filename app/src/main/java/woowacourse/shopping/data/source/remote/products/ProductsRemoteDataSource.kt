package woowacourse.shopping.data.source.remote.products

import woowacourse.shopping.data.model.ProductResponse
import woowacourse.shopping.data.model.ProductsResponse
import woowacourse.shopping.data.source.remote.api.ProductsApiService
import woowacourse.shopping.data.source.remote.util.enqueueResult

class ProductsRemoteDataSource(
    private val api: ProductsApiService,
) : ProductsDataSource {
    override fun getProducts(
        page: Int,
        size: Int,
        onResult: (Result<ProductsResponse>) -> Unit,
    ) {
        api.getProducts(page = page, size = size).enqueueResult(onResult)
    }

    override fun getProductById(
        id: Int,
        onResult: (Result<ProductResponse>) -> Unit,
    ) {
        api.getProductById(id = id).enqueueResult(onResult)
    }
}
