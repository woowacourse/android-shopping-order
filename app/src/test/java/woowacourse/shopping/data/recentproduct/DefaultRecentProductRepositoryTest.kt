package woowacourse.shopping.data.recentproduct

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import retrofit2.Retrofit
import woowacourse.shopping.createProduct
import woowacourse.shopping.createRecentProduct
import woowacourse.shopping.createRecentProductEntity
import woowacourse.shopping.data.database.dao.RecentProductDao
import woowacourse.shopping.data.product.DefaultProductRemoteDataSource
import woowacourse.shopping.data.product.MockProductServer
import woowacourse.shopping.data.product.ProductService
import woowacourse.shopping.data.server.ProductRemoteDataSource
import woowacourse.shopping.domain.RecentProduct
import woowacourse.shopping.domain.RecentProducts
import woowacourse.shopping.domain.URL
import woowacourse.shopping.domain.repository.RecentProductRepository
import java.time.LocalDateTime
import java.util.concurrent.CountDownLatch

internal class DefaultRecentProductRepositoryTest {
    private val recentProductDao: RecentProductDao = mockk()
    private val productService: ProductService = Retrofit.Builder()
        .baseUrl(MockProductServer.server.url(""))
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ProductService::class.java)
    private val productRemoteDataSource: ProductRemoteDataSource =
        DefaultProductRemoteDataSource(productService)
    private val recentProductRepository: RecentProductRepository =
        DefaultRecentProductRepository(recentProductDao, productRemoteDataSource)

    @Test
    fun getAll() {
        // given
        val countDownLatch = CountDownLatch(1)
        val id = 1
        val time = LocalDateTime.now()
        every { recentProductDao.selectAll() } returns listOf(createRecentProductEntity(id, time))

        // when
        var actual: RecentProducts? = null
        recentProductRepository.getAll(
            onSuccess = {
                actual = it
                countDownLatch.countDown()
            },
            onFailure = { countDownLatch.countDown() }
        )
        countDownLatch.await()

        // then
        val expected = RecentProducts(
            listOf(
                createRecentProduct(
                    time,
                    createProduct(1, URL("http://example.com/chicken.jpg"), "치킨", 10000)
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun addRecentProduct() {
        // given
        every { recentProductDao.insertRecentProduct(any()) } just runs

        // when
        val time = LocalDateTime.now()
        val productId = 10
        val recentProduct = createRecentProduct(time, createProduct(id = productId))
        recentProductRepository.addRecentProduct(recentProduct)

        // then
        val expected = createRecentProductEntity(productId, time)
        verify { recentProductDao.insertRecentProduct(expected) }
    }

    @Test
    fun updateRecentProduct() {
        // given
        every { recentProductDao.updateRecentProduct(any()) } just runs

        // when
        val time = LocalDateTime.now()
        val productId = 10
        val recentProduct = createRecentProduct(time, createProduct(id = productId))
        recentProductRepository.updateRecentProduct(recentProduct)

        // then
        verify { recentProductDao.updateRecentProduct(createRecentProductEntity(productId, time)) }
    }

    @Test
    fun getLatestRecentProduct() {
        // given
        val countDownLatch = CountDownLatch(1)
        val id = 1
        val time = LocalDateTime.now()
        every { recentProductDao.selectLatestRecentProduct() } returns createRecentProductEntity(
            id,
            time
        )

        // when
        var actual: RecentProduct? = null
        recentProductRepository.getLatestRecentProduct(
            onSuccess = {
                actual = it
                countDownLatch.countDown()
            },
            onFailure = { countDownLatch.countDown() }
        )
        countDownLatch.await()

        // then
        val expected = createRecentProduct(
            time,
            createProduct(1, URL("http://example.com/chicken.jpg"), "치킨", 10000)
        )
        assertEquals(expected, actual)
    }

    @Test
    fun `최근 본 상품이다`() {
        // given
        every { recentProductDao.selectProduct(any()) } returns createRecentProductEntity()

        // when
        val actual = recentProductRepository.isExist(1)

        // then
        verify { recentProductDao.selectProduct(1) }
        assert(actual)
    }

    @Test
    fun `최근 본 상품이 아니다`() {
        // given
        every { recentProductDao.selectProduct(any()) } returns null

        // when
        val actual = recentProductRepository.isExist(1)

        // then
        verify { recentProductDao.selectProduct(1) }
        assertFalse(actual)
    }
}