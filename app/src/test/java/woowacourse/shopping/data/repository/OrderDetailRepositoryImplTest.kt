package woowacourse.shopping.data.repository

import com.example.domain.model.Order
import io.mockk.clearAllMocks
import org.junit.After
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.remote.orderdetail.OrderRemoteDetailSourceImpl
import woowacourse.shopping.data.datasource.remote.retrofit.RetrofitClient
import woowacourse.shopping.mockwebserver.CartMockWebserver
import woowacourse.shopping.mockwebserver.OrderMockWebserver

internal class OrderDetailRepositoryImplTest {
    private lateinit var orderDetailRepositoryImpl: OrderDetailRepositoryImpl
    private lateinit var mockWebserver: OrderMockWebserver
    private lateinit var cartMockWebserver: CartMockWebserver

    @Before
    fun setup() {
        mockWebserver = OrderMockWebserver()
        RetrofitClient.getInstance(mockWebserver.url)
        orderDetailRepositoryImpl = OrderDetailRepositoryImpl(OrderRemoteDetailSourceImpl())
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `주문 상세 정보를 불러온다`() {
        // when
        var lock = true
        var orderDetail: Order? = null
        orderDetailRepositoryImpl.getById(1) {
            orderDetail = it
            lock = false
        }
        while (lock) {
            Thread.sleep(100)
        }

        // then
        assert(orderDetail?.id == 1L)
    }
}
