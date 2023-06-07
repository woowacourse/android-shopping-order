package woowacourse.shopping.datasource.product

import android.util.Base64
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.datasource.impl.ProductRemoteDataSource
import woowacourse.shopping.data.remote.dto.ProductWithCartInfoDTO
import woowacourse.shopping.data.remote.dto.ProductsWithCartItemDTO
import woowacourse.shopping.data.remote.result.DataResult
import java.util.concurrent.CountDownLatch

class ProductRemoteDataSourceTest {
    private lateinit var mockServer: ProductMockServer
    private lateinit var productRemoteDataSource: ProductRemoteDataSource

    @Before
    fun setUp() {
        mockkStatic("android.util.Base64")
        every { Base64.encodeToString(any(), any()) } returns "ZG9vbHlAZG9vbHkuY29tOjEyMzQ="

        val thread = Thread { mockServer = ProductMockServer() }
        thread.start()
        thread.join()

        productRemoteDataSource = ProductRemoteDataSource(mockServer.url)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
    }

    @Test
    fun getProductsByRangeTest() {
        // given
        val countDownLatch = CountDownLatch(1)
        // when
        var result: DataResult<ProductsWithCartItemDTO>? = null
        productRemoteDataSource.getProductsByRange(0, 3) {
            result = it
            countDownLatch.countDown()
        }
        countDownLatch.await()
        // then
        assertTrue(result is DataResult.Success && (result as DataResult.Success<ProductsWithCartItemDTO>).response.isNotNull)
    }

    @Test
    fun getProductByIdTest() {
        // given
        val countDownLatch = CountDownLatch(1)
        // when
        var result: DataResult<ProductWithCartInfoDTO>? = null
        productRemoteDataSource.getProductById(1) {
            result = it
            countDownLatch.countDown()
        }
        countDownLatch.await()
        // then
        assertTrue(result is DataResult.Success && (result as DataResult.Success<ProductWithCartInfoDTO>).response.isNotNull)
    }
}
