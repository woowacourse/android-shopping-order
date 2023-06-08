package woowacourse.shopping.data.cart

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Retrofit
import woowacourse.shopping.createCartProduct
import woowacourse.shopping.createProduct
import woowacourse.shopping.data.server.CartRemoteDataSource
import woowacourse.shopping.domain.CartProduct
import woowacourse.shopping.domain.URL
import java.util.concurrent.CountDownLatch

internal class DefaultCartRemoteDataSourceTest {
    private val cartService: CartService = Retrofit.Builder()
        .baseUrl(MockCartServer.server.url(""))
        .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
        .build()
        .create(CartService::class.java)
    private val cartRemoteDataSource: CartRemoteDataSource = DefaultCartRemoteDataSource(cartService)

    @Test
    fun getCartProducts() {
        // given
        val countDownLatch = CountDownLatch(1)

        // when
        var actual: List<CartProduct> = emptyList()
        cartRemoteDataSource.getCartProducts(
            onSuccess = {
                actual = it
                countDownLatch.countDown()
            },
            onFailure = { countDownLatch.countDown() }
        )
        countDownLatch.await()

        // then
        val expected: List<CartProduct> = listOf(
            createCartProduct(
                1, 10, true,
                createProduct(
                    1,
                    URL("https://images.unsplash.com/photo-1626082927389-6cd097cdc6ec?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80"),
                    "치킨",
                    10000
                )
            ),
            createCartProduct(
                2, 4, true,
                createProduct(
                    2,
                    URL("https://images.unsplash.com/photo-1512621776951-a57141f2eefd?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2370&q=80"),
                    "샐러드",
                    2000
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test
    fun addCartProduct() {
        // given
        val countDownLatch = CountDownLatch(1)

        // when
        var actual: Int = -1
        cartRemoteDataSource.addCartProduct(1, 10,
            onSuccess = {
                actual = it
                countDownLatch.countDown()
            },
            onFailure = { countDownLatch.countDown() }
        )
        countDownLatch.await()

        // then
        val expected = 1
        assertEquals(expected, actual)
    }

    @Test
    fun updateCartProductQuantity() {
        // given
        val countDownLatch = CountDownLatch(1)

        // when
        var isSuccessful = false
        cartRemoteDataSource.updateCartProductQuantity(10, 10,
            onSuccess = {
                isSuccessful = true
                countDownLatch.countDown()
            },
            onFailure = { countDownLatch.countDown() }
        )
        countDownLatch.await()

        // then
        assert(isSuccessful)
    }

    @Test
    fun deleteCartProduct() {
        // given
        val countDownLatch = CountDownLatch(1)

        // when
        var isSuccessful = false
        cartRemoteDataSource.deleteCartProduct(10,
            onSuccess = {
                isSuccessful = true
                countDownLatch.countDown()
            },
            onFailure = { countDownLatch.countDown() }
        )
        countDownLatch.await()

        // then
        assert(isSuccessful)
    }
}