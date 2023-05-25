package woowacourse.shopping.data.respository.product.source.remote

import woowacourse.shopping.data.model.ProductEntity

interface ProductRemoteDataSource {
    fun requestDatas(
        onFailure: () -> Unit,
        onSuccess: (products: List<ProductEntity>) -> Unit,
    )
    fun requestData(
        productId: Long,
        onFailure: () -> Unit,
        onSuccess: (products: ProductEntity) -> Unit,
    )
}
