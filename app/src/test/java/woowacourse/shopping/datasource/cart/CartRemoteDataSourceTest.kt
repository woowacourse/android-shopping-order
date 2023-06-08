package woowacourse.shopping.datasource.cart

import android.util.Base64
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.impl.CartRemoteDataSource
import woowacourse.shopping.data.remote.dto.CartProductDTO
import woowacourse.shopping.data.remote.result.DataResult
import java.util.concurrent.CountDownLatch

class CartRemoteDataSourceTest {
    private lateinit var mockServer: CartMockServer
    private lateinit var cartRemoteDataSource: CartRemoteDataSource

    @Before
    fun setUp() {
        mockkStatic("android.util.Base64")
        every { Base64.encodeToString(any(), any()) } returns "ZG9vbHlAZG9vbHkuY29tOjEyMzQ="

        val thread = Thread { mockServer = CartMockServer() }
        thread.start()
        thread.join()

        cartRemoteDataSource = CartRemoteDataSource(mockServer.url)
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
        var result: DataResult<List<CartProductDTO>>? = null
        cartRemoteDataSource.getAll {
            result = it
            countDownLatch.countDown()
        }
        countDownLatch.await()
        // then
        assertTrue(result is DataResult.Success && (result as DataResult.Success<List<CartProductDTO>>).response.all { it.isNotNull })
    }

    @Test
    fun insertTest() {
        // given
        val countDownLatch = CountDownLatch(1)
        // when
        var result: DataResult<Int>? = null
        cartRemoteDataSource.insert(1, 1) {
            result = it
            countDownLatch.countDown()
        }
        countDownLatch.await()
        // then
        assertTrue(result is DataResult.Success && (result as DataResult.Success<Int>).response == 1)
    }

    @Test
    fun updateTest() {
        // given
        val countDownLatch = CountDownLatch(1)
        // when
        var result: DataResult<Boolean>? = null
        cartRemoteDataSource.update(1, 10) {
            result = it
            countDownLatch.countDown()
        }
        countDownLatch.await()
        // then
        assertTrue(result is DataResult.Success && (result as DataResult.Success<Boolean>).response)
    }

    @Test
    fun removeTest() {
        // given
        val countDownLatch = CountDownLatch(1)
        // when
        var result: DataResult<Boolean>? = null
        cartRemoteDataSource.remove(1) {
            result = it
            countDownLatch.countDown()
        }
        countDownLatch.await()
        // then
        assertTrue(result is DataResult.Success && (result as DataResult.Success<Boolean>).response)
    }
}
