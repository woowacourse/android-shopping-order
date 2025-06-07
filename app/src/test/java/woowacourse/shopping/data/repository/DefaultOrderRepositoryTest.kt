package woowacourse.shopping.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.datasource.OrderDataSource
import woowacourse.shopping.data.network.request.OrderRequest
import woowacourse.shopping.domain.exception.NetworkResult

class DefaultOrderRepositoryTest {
    private val orderDataSource = mockk<OrderDataSource>()
    private lateinit var repository: DefaultOrderRepository

    @BeforeEach
    fun setUp() {
        repository = DefaultOrderRepository(orderDataSource)
    }

    @Test
    fun `주문 요청에 성공하면 success를 반환한다`() =
        runTest {
            // given
            val cartItemIds = listOf(1L, 2L, 3L)
            val request = OrderRequest(cartItemIds)
            val expected = NetworkResult.Success(Unit)

            coEvery { orderDataSource.createOrder(request) } returns expected

            // when
            val result = repository.createOrder(cartItemIds)

            // then
            assertEquals(expected, result)
            coVerify(exactly = 1) { orderDataSource.createOrder(request) }
        }
}
