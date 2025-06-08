package woowacourse.shopping.domain.repository

import woowacourse.shopping.data.datasource.OrderDataSource

interface OrderRepository

class OrderRepositoryImpl(
    private val orderDataSource: OrderDataSource,
) : OrderRepository
