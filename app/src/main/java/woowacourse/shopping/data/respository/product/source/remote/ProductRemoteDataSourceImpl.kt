package woowacourse.shopping.data.respository.product.source.remote

import woowacourse.shopping.data.model.dto.request.ProductIdRequest
import woowacourse.shopping.data.model.dto.response.ProductResponse
import woowacourse.shopping.data.respository.product.service.ProductService
import woowacourse.shopping.data.util.responseCallback

class ProductRemoteDataSourceImpl(
    private val productService: ProductService,
) : ProductRemoteDataSource {

    override fun requestDatas(
        onFailure: (Throwable) -> Unit,
        onSuccess: (products: List<ProductResponse>) -> Unit,
    ) {
        productService.requestDatas().enqueue(
            responseCallback<List<ProductResponse>>(
                onFailure = onFailure,
                onSuccess = onSuccess,
            )
        )
    }

    override fun requestData(
        productId: Long,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (product: ProductResponse) -> Unit,
    ) {
        val productIdRequest = ProductIdRequest(productId)
        productService.requestData(productIdRequest).enqueue(
            responseCallback(
                onFailure = onFailure,
                onSuccess = onSuccess,
            )
        )
    }
}
