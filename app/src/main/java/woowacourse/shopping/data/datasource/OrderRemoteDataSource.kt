package woowacourse.shopping.data.datasource

import woowacourse.shopping.data.model.DataOrderRecord

interface OrderRemoteDataSource {

    fun getOrderRecord(
        orderId: Int,
        onReceived: (DataOrderRecord) -> Unit,
    )
}
