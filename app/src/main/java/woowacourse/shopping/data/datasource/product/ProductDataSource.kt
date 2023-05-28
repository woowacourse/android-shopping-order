package woowacourse.shopping.data.datasource.product

import woowacourse.shopping.data.model.DataProduct

interface ProductDataSource {

    interface Remote {
        fun getPartially(
            size: Int,
            lastId: Int,
            onReceived: (products: List<DataProduct>) -> Unit
        )
    }
}
