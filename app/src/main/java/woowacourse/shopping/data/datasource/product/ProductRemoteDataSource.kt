package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.data.model.DataProduct

interface ProductRemoteDataSource {

    fun getPartially(
        size: Int,
        lastId: Int,
        onReceived: (products: List<DataProduct>) -> Unit
    )
}
