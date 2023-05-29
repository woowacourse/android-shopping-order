package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.OrderRemoteDataSource
import woowacourse.shopping.data.mapper.toOrderRecord
import woowacourse.shopping.ui.model.OrderRecord

class OrderRepositoryImpl(
    private val orderRemoteDataSource: OrderRemoteDataSource,
) : OrderRepository {

    override fun getOrderRecord(
        orderId: Int,
        onReceived: (orderRecord: OrderRecord) -> Unit,
    ) {
        orderRemoteDataSource.getOrderRecord(orderId) { dataOrderRecord ->
            val orderRecord = dataOrderRecord.toOrderRecord()

            onReceived(orderRecord)
        }
    }
}
