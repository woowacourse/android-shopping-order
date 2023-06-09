package woowacourse.shopping.data.member

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import woowacourse.shopping.createCartProduct
import woowacourse.shopping.createOrder
import woowacourse.shopping.createOrderHistory
import woowacourse.shopping.createProduct
import woowacourse.shopping.data.server.MemberRemoteDataSource
import woowacourse.shopping.domain.Order
import woowacourse.shopping.domain.OrderHistory
import woowacourse.shopping.domain.URL
import java.util.concurrent.CountDownLatch

internal class DefaultMemberRemoteDataSourceTest {
    private val memberService: MemberService = Retrofit.Builder()
        .baseUrl(MockMemberServer.server.url(""))
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(MemberService::class.java)
    private val memberRemoteDataSource: MemberRemoteDataSource =
        DefaultMemberRemoteDataSource(memberService)

    @Test
    fun getPoints() {
        // given
        val countDownLatch = CountDownLatch(1)

        // when
        var actual = 0
        memberRemoteDataSource.getPoints(
            onSuccess = {
                actual = it
                countDownLatch.countDown()
            },
            onFailure = { countDownLatch.countDown() }
        )
        countDownLatch.await()

        // then
        val expected = 5000
        assertEquals(expected, actual)
    }

    @Test
    fun getOrderHistories() {
        // given
        val countDownLatch = CountDownLatch(1)

        // when
        var actual: List<OrderHistory> = emptyList()
        memberRemoteDataSource.getOrderHistories(
            onSuccess = {
                actual = it
                countDownLatch.countDown()
            },
            onFailure = { countDownLatch.countDown() }
        )
        countDownLatch.await()

        // then
        val expected: List<OrderHistory> = listOf(
            createOrderHistory(1, 25000, 2, "PET보틀-정사각(370ml)"),
            createOrderHistory(2, 1400, 3, "[든든] 동원 스위트콘")
        )
        assertEquals(expected, actual)
    }

    @Test
    fun getOrder() {
        // given
        val countDownLatch = CountDownLatch(1)

        // when
        var actual: Order? = null
        memberRemoteDataSource.getOrder(
            id = 10,
            onSuccess = {
                actual = it
                countDownLatch.countDown()
            },
            onFailure = { countDownLatch.countDown() }
        )
        countDownLatch.await()

        // then
        val expected: Order = createOrder(
            products = listOf(
                createCartProduct(
                    0,
                    2,
                    true,
                    createProduct(0, URL("http://image/test1.png"), "[든든] 동원 스위트콘", 99800)
                ),
                createCartProduct(
                    0,
                    3,
                    true,
                    createProduct(0, URL("http://image/test2.png"), "PET보틀-원형(500ml)", 84400)
                )
            ),
            originalPrice = 184400,
            usedPoints = 1000,
            finalPrice = 183400
        )
        assertEquals(expected, actual)
    }
}