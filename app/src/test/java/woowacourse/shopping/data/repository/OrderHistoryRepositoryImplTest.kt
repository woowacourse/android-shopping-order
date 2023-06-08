package woowacourse.shopping.data.repository

import com.example.domain.model.Order
import io.mockk.clearAllMocks
import org.junit.After
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.remote.orderhistory.OrderHistoryRemoteSourceImpl
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.mockwebserver.OrderMockWebserver

internal class OrderHistoryRepositoryImplTest {
    private lateinit var orderHistoryRepositoryImpl: OrderHistoryRepositoryImpl
    private lateinit var mockWebserver: OrderMockWebserver

    @Before
    fun setup() {
        mockWebserver = OrderMockWebserver()
        RetrofitClient.getInstance(mockWebserver.url)
        orderHistoryRepositoryImpl = OrderHistoryRepositoryImpl(OrderHistoryRemoteSourceImpl())
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `주문 내역을 불러온다`() {
        // when
        var lock = true
        var orderHistories = emptyList<Order>()
        orderHistoryRepositoryImpl.getOrderHistory {
            orderHistories = it
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assert(orderHistories.size == 10)
        orderHistories.forEachIndexed { i, it ->
            assert(it.id == 1L)
        }
    }
}
