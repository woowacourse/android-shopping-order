package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.data.model.ProductEntity

interface ProductRemoteDataSource {

    fun getPartially(
        size: Int,
        lastId: Int,
        onReceived: (products: List<ProductEntity>) -> Unit,
    )
}
