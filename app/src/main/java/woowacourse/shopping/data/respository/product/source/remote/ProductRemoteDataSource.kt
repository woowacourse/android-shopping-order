package woowacourse.shopping.data.respository.product.source.remote

import woowacourse.shopping.data.model.dto.response.ProductResponse

interface ProductRemoteDataSource {
    fun requestDatas(
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (products: List<ProductResponse>) -> Unit,
    )

    fun requestData(
        productId: Long,
        onFailure: (throwable: Throwable) -> Unit,
        onSuccess: (products: ProductResponse) -> Unit,
    )
}
