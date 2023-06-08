package woowacourse.shopping.data.product

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Assert
import org.junit.Test
import retrofit2.Retrofit
import woowacourse.shopping.createProduct
import woowacourse.shopping.data.server.ProductRemoteDataSource
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.URL
import java.util.concurrent.CountDownLatch

internal class DefaultProductRemoteDataSourceTest {
    private val productService: ProductService = Retrofit.Builder()
        .baseUrl(MockProductServer.server.url(""))
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(ProductService::class.java)
    private val productRemoteDataSource: ProductRemoteDataSource = DefaultProductRemoteDataSource(productService)

    @Test
    fun getProducts() {
        // given
        val countDownLatch = CountDownLatch(1)

        // when
        var actual: List<Product> = emptyList()
        productRemoteDataSource.getProducts(
            onSuccess = {
                actual = it
                countDownLatch.countDown()
            },
            onFailure = { countDownLatch.countDown() }
        )
        countDownLatch.await()

        // then
        val expected: List<Product> = listOf(
            createProduct(1, URL("http://example.com/chicken.jpg"), "치킨", 10000),
            createProduct(2, URL("http://example.com/pizza.jpg"), "피자", 20000)
        )
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun getProduct() {
        // given
        val countDownLatch = CountDownLatch(1)

        // when
        var actual: Product? = null
        productRemoteDataSource.getProduct(
            id = 1,
            onSuccess = {
                actual = it
                countDownLatch.countDown()
            },
            onFailure = { countDownLatch.countDown() }
        )
        countDownLatch.await()

        // then
        val expected: Product = createProduct(1, URL("http://example.com/chicken.jpg"), "치킨", 10000)
        Assert.assertEquals(expected, actual)
    }
}