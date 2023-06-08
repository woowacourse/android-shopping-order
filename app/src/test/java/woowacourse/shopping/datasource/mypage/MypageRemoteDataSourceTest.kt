package woowacourse.shopping.datasource.mypage

import android.util.Base64
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.impl.MypageRemoteDataSource
import woowacourse.shopping.data.remote.result.DataResult
import java.util.concurrent.CountDownLatch

class MypageRemoteDataSourceTest {
    private lateinit var mockServer: MypageMockServer
    private lateinit var mypageRemoteDataSource: MypageRemoteDataSource

    @Before
    fun setUp() {
        mockkStatic("android.util.Base64")
        every { Base64.encodeToString(any(), any()) } returns "ZG9vbHlAZG9vbHkuY29tOjEyMzQ="

        val thread = Thread { mockServer = MypageMockServer() }
        thread.start()
        thread.join()

        mypageRemoteDataSource = MypageRemoteDataSource(mockServer.url)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun getCashTest() {
        // given
        val countDownLatch = CountDownLatch(1)
        // when
        var result: DataResult<Int>? = null
        mypageRemoteDataSource.getCash {
            result = it
            countDownLatch.countDown()
        }
        countDownLatch.await()
        // then
        assertTrue(result is DataResult.Success && (result as DataResult.Success<Int>).response != -1)
    }

    @Test
    fun chargeCashTest() {
        // given
        val countDownLatch = CountDownLatch(1)
        // when
        var result: DataResult<Int>? = null
        mypageRemoteDataSource.chargeCash(10000) {
            result = it
            countDownLatch.countDown()
        }
        countDownLatch.await()
        // then
        assertTrue(result is DataResult.Success && (result as DataResult.Success<Int>).response != -1)
    }
}
