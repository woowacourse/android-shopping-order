package woowacourse.shopping.data.datasource.remote.product

import woowacourse.shopping.data.model.product.ProductDto

interface ProductRemoteDataSource {

    fun loadAll(
        onSuccess: (List<ProductDto>) -> Unit,
        onFailure: () -> Unit
    )
}
