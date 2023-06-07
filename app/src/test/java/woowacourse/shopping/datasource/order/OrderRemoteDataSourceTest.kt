package woowacourse.shopping.datasource.order

import android.util.Base64
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.impl.OrderRemoteDataSource
import woowacourse.shopping.data.remote.dto.OrderCartItemsDTO
import woowacourse.shopping.data.remote.dto.OrdersDTO
import woowacourse.shopping.data.remote.result.DataResult
import java.util.concurrent.CountDownLatch

class OrderRemoteDataSourceTest {
    private lateinit var mockServer: OrderMockServer
    private lateinit var orderRemoteDataSource: OrderRemoteDataSource

    @Before
    fun setUp() {
        mockkStatic("android.util.Base64")
        every { Base64.encodeToString(any(), any()) } returns "ZG9vbHlAZG9vbHkuY29tOjEyMzQ="

        val thread = Thread { mockServer = OrderMockServer() }
        thread.start()
        thread.join()

        orderRemoteDataSource = OrderRemoteDataSource(mockServer.url)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun getAllTest() {
        // given
        val countDownLatch = CountDownLatch(1)
        // when
        var result: DataResult<OrdersDTO>? = null
        orderRemoteDataSource.getAll {
            result = it
            countDownLatch.countDown()
        }
        countDownLatch.await()
        // then
        assertTrue(result is DataResult.Success && (result as DataResult.Success<OrdersDTO>).response.isNotNull)
    }

    @Test
    fun getOrderTest() {
        // given
        val countDownLatch = CountDownLatch(1)
        // when
        var result: DataResult<OrdersDTO.OrderDTO>? = null
        orderRemoteDataSource.getOrder(1) {
            result = it
            countDownLatch.countDown()
        }
        countDownLatch.await()
        // then
        assertTrue(result is DataResult.Success && (result as DataResult.Success<OrdersDTO.OrderDTO>).response.isNotNull)
    }

    @Test
    fun orderTest() {
        // given
        val countDownLatch = CountDownLatch(1)
        val orderProduct = OrderCartItemsDTO(
            listOf(
                OrderCartItemsDTO.OrderCartItemDTO(
                    1,
                    "치킨",
                    10000,
                    "https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80",
                ),
                OrderCartItemsDTO.OrderCartItemDTO(
                    2,
                    "피자",
                    13000,
                    "https://images.unsplash.com/photo-1595854341625-f33ee10dbf94?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1740&q=80",
                ),
            ),
        )
        // when
        var result: DataResult<Int?>? = null
        orderRemoteDataSource.order(orderProduct) {
            result = it
            countDownLatch.countDown()
        }
        countDownLatch.await()
        // then
        assertTrue(result is DataResult.Success && (result as DataResult.Success<Int?>).response == 1)
    }
}
