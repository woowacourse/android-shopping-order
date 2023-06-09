package woowacourse.shopping.data.order

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Assert
import org.junit.Test
import retrofit2.Retrofit
import woowacourse.shopping.data.order.request.PostOrderRequest
import woowacourse.shopping.data.server.OrderRemoteDataSource
import java.util.concurrent.CountDownLatch

internal class DefaultOrderRemoteDataSourceTest {
    private val orderService: OrderService = Retrofit.Builder()
        .baseUrl(MockOrderServer.server.url(""))
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(OrderService::class.java)
    private val orderRemoteDataSource: OrderRemoteDataSource = DefaultOrderRemoteDataSource(orderService)

    @Test
    fun addOrder() {
        // given
        val countDownLatch = CountDownLatch(1)

        // when
        var actual = 0
        orderRemoteDataSource.addOrder(
            PostOrderRequest(emptyList(), 1000, 500),
            onSuccess = {
                actual = it
                countDownLatch.countDown()
            },
            onFailure = { countDownLatch.countDown() }
        )
        countDownLatch.await()

        // then
        val expected = 1
        Assert.assertEquals(expected, actual)
    }
}