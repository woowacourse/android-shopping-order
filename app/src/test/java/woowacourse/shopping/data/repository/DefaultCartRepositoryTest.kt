package woowacourse.shopping.data.repository

import kotlinx.coroutines.test.runTest
import mockwebserver3.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.source.local.auth.AuthDataSource
import woowacourse.shopping.data.source.remote.CartRemoteDataSource
import woowacourse.shopping.data.source.remote.dto.cart.Product
import woowacourse.shopping.fake.FakeCartDispatcher

class DefaultCartRepositoryTest {
    private lateinit var server: MockWebServer
    private lateinit var defaultCartRepository: DefaultCartRepository

    private val product =
        Product(
            id = 1L,
            category = "장난감",
            name = "상품",
            price = 5000,
            imageUrl = "",
        )

    @BeforeEach
    fun setUp() {
        server = MockWebServer()
        server.dispatcher =
            FakeCartDispatcher(
                fixedProductContent = product,
            )
        server.start()

        defaultCartRepository =
            DefaultCartRepository(
                remoteDataSource =
                    CartRemoteDataSource(
                        baseUrl = server.url("/").toString(),
                        authDataSource =
                            object : AuthDataSource {
                                override val getAuthToken: String = FakeCartDispatcher.authToken
                            },
                    ),
            )
    }

    @Test
    fun `장바구니에 등록된 상품을 조회한다`() =
        runTest {
            val cart = defaultCartRepository.getCart()
            assertThat(cart.items.isEmpty()).isTrue

            defaultCartRepository.addItem(1L)
            defaultCartRepository.addItem(2L)

            val newCart = defaultCartRepository.getCart()
            assertThat(newCart.items.size).isEqualTo(2)
        }

    @Test
    fun `장바구니에 등록된 상품을 추가한다`() =
        runTest {
            defaultCartRepository.addItem(1L)

            val cart = defaultCartRepository.getCart()

            assertThat(cart.items.any { it.product.id == 1L }).isTrue
        }

    @Test
    fun `장바구니에 등록된 상품을 삭제한다`() =
        runTest {
            defaultCartRepository.addItem(1L)
            val cart = defaultCartRepository.getCart()
            assertThat(cart.items.any { it.product.id == 1L }).isTrue
            assertThat(cart.items.size).isEqualTo(1)

            defaultCartRepository.deleteItem(1L)

            val newCart = defaultCartRepository.getCart()
            assertThat(newCart.items.size).isEqualTo(0)

            assertThat(newCart.items.any { it.product.id == 1L }).isFalse
        }

    @Test
    fun `장바구니에 등록된 상품의 수량을 변경한다`() =
        runTest {
            defaultCartRepository.addItem(1L, 5)

            defaultCartRepository.changeCartItem(1L, 10)

            val newCart = defaultCartRepository.getCart()
            val item = newCart.items.find { it.product.id == 1L }
            assertThat(item!!.quantity).isEqualTo(10)
        }
}
