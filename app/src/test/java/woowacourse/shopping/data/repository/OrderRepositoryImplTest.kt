package woowacourse.shopping.data.repository

import com.example.domain.model.Order
import io.mockk.clearAllMocks
import org.junit.After
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.remote.order.OrderRemoteDataSourceImpl
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.mockwebserver.OrderMockWebserver

internal class OrderRepositoryImplTest {
    private lateinit var orderRepositoryImpl: OrderRepositoryImpl
    private lateinit var mockWebserver: OrderMockWebserver

    @Before
    fun setup() {
        mockWebserver = OrderMockWebserver()
        RetrofitClient.getInstance(mockWebserver.url)
        orderRepositoryImpl = OrderRepositoryImpl(OrderRemoteDataSourceImpl())
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `쿠폰을 적용한 주문을 넣을 수 있다`() {
        // given
        val cartItemids = listOf(1L, 2L)
        val couponId = 1L

        // when
        var lock = true
        var order: Order? = null
        orderRepositoryImpl.insertOrderWithCoupon(cartItemids, couponId) {
            order = it
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assert(order?.id == 1L)
    }
}
