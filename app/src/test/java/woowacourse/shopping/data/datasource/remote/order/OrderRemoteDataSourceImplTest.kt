package woowacourse.shopping.data.datasource.remote.order

import io.mockk.clearAllMocks
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.remote.orderdetail.OrderRemoteDetailSourceImpl
import woowacourse.shopping.data.datasource.remote.orderhistory.OrderHistoryRemoteSourceImpl
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.data.remote.request.OrderDTO
import woowacourse.shopping.mockwebserver.OrderMockWebserver

internal class OrderRemoteDataSourceImplTest {
    private lateinit var mockWebserver: OrderMockWebserver
    private lateinit var orderDataSource: OrderRemoteDataSourceImpl
    private lateinit var orderHistoryDataSource: OrderHistoryRemoteSourceImpl
    private lateinit var orderRemoteDetailSourceImpl: OrderRemoteDetailSourceImpl

    @Before
    fun setup() {
        mockWebserver = OrderMockWebserver()
        RetrofitClient.getInstance(mockWebserver.url)
        orderDataSource = OrderRemoteDataSourceImpl()
        orderHistoryDataSource = OrderHistoryRemoteSourceImpl()
        orderRemoteDetailSourceImpl = OrderRemoteDetailSourceImpl()
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `쿠폰을 적용하고 주문을 추가할 수 있다`() {
        // given
        val cartItemsIds = listOf(1L)
        val couponId = 1L

        // when
        var lock = true
        var orderDTO = OrderDTO(0, listOf(), "", "", 0, 0, 0)
        orderDataSource.postOrderWithCoupon(cartItemsIds, couponId) { result ->
            result.onSuccess { orderDTO = it }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(orderDTO.orderProducts.size, 1)
    }

    @Test
    fun `전체 주문 정보를 가져올 수 있다`() {
        // when
        var lock = true
        var orders = listOf(OrderDTO(0, listOf(), "", "", 0, 0, 0))
        orderHistoryDataSource.getOrderList { result ->
            result.onSuccess { orders = it }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(orders.size, 10)
    }

    @Test
    fun `주문 상세정보를 가져올 수 있다`() {
        // given
        val orderId = 1L

        // when
        var lock = true
        var orderDTO = OrderDTO(0, listOf(), "", "", 0, 0, 0)
        orderRemoteDetailSourceImpl.getById(orderId) { result ->
            result.onSuccess { orderDTO = it }
                .onFailure { e -> throw e }
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assertEquals(orderDTO.orderProducts.size, 1)
    }
}
