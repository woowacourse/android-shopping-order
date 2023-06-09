package woowacourse.shopping.data.cart

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatIllegalArgumentException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import woowacourse.shopping.data.cart.source.NetworkCartItem
import woowacourse.shopping.data.cart.source.NetworkCartItemDataSource
import woowacourse.shopping.data.product.Product
import woowacourse.shopping.data.product.source.NetworkProduct

internal class DefaultCartItemRepositoryTest {

    private lateinit var networkCartItemDataSource: NetworkCartItemDataSource
    private lateinit var sut: DefaultCartItemRepository

    @BeforeEach
    fun setUp() {
        networkCartItemDataSource = mockk()
        sut = DefaultCartItemRepository(networkCartItemDataSource)
    }

    @Test
    fun `장바구니 아이템들을 조회할 때 데이터 소스에서 장바구니 아이템을 불러오는 데 실패하면 실패한 Result를 가진 CompletableFuture 객체를 반환한다`() {
        every { networkCartItemDataSource.loadCartItems() } returns Result.failure(Throwable())

        val completableFuture = sut.getCartItems()

        completableFuture.thenAccept { assertThat(it.isFailure).isTrue }
    }

    @Test
    fun `장바구니 아이템을 조회할 때 데이터 소스에서 장바구니 아이템을 불러오는 데 성공하면 장바구니 아이템의 리스트를 가진 성공한 Result를 가진 CompletableFuture 객체를 반환한다`() {
        val dummyNetworkCartItems = listOf(
            NetworkCartItem(
                1,
                10,
                NetworkProduct(1, "치킨", 10000, "http://example.com/chicken.jpg")
            )
        )
        every { networkCartItemDataSource.loadCartItems() } returns Result.success(
            dummyNetworkCartItems
        )

        val completableFuture = sut.getCartItems()

        completableFuture.thenAccept {
            assertAll(
                { assertThat(it.isSuccess).isTrue },
                { assertThat(it.getOrNull()).isEqualTo(dummyNetworkCartItems.toExternal()) }
            )
        }
    }

    @Test
    fun `장바구니 아이템을 생성할 때 수량이 1 미만이라면 에러가 발생한다`() {
        assertThatIllegalArgumentException().isThrownBy {
            sut.createCartItem(Product(1, "", "", 10), 0)
        }.withMessage("장바구니 아이템의 수량은 1 이상이어야 합니다.")
    }

    @Test
    fun `장바구니 아이템을 생성할 때 데이터 소스에 저장이 실패하면 실패한 Result 객체를 가진 CompletableFuture 객체를 받는다`() {
        every { networkCartItemDataSource.saveCartItem(any()) } returns Result.failure(Throwable())

        sut.createCartItem(Product(1, "", "", 10), 10).thenAccept {
            assertThat(it.isFailure).isTrue
        }
    }

    @Test
    fun `장바구니 아이템을 생성할 때 데이터 소스에 저장이 성공하면 장바구니 아이템의 아이디를 가진 성공한 Result 객체를 가진 CompletableFuture 객체를 받는다`() {
        every { networkCartItemDataSource.saveCartItem(any()) } returns Result.success(1L)

        sut.createCartItem(Product(1, "", "", 10), 10).thenAccept {
            assertAll(
                { assertThat(it.isSuccess).isTrue },
                { assertThat(it.getOrThrow()).isEqualTo(1L) }
            )
        }
    }

    @Test
    fun `장바구니 아이템의 수량을 변경할 때 변경할 수량이 1 미만이라면 에러가 발생한다`() {
        assertThatIllegalArgumentException().isThrownBy {
            sut.updateCartItemQuantity(1L, 0)
        }.withMessage("장바구니 아이템의 수량은 1 이상이어야 합니다.")
    }

    @Test
    fun `장바구니 아이템의 수량을 변경할 때 데이터 소스에 갱신이 실패하면 실패한 Result 객체를 가진 CompletableFuture 객체를 받는다`() {
        every {
            networkCartItemDataSource.updateCartItemQuantity(any(), any())
        } returns Result.failure(Throwable())

        sut.updateCartItemQuantity(1L, 10).thenAccept {
            assertThat(it.isFailure).isTrue
        }
    }

    @Test
    fun `장바구니 아이템의 수량을 변경할 때 데이터 소스에 갱신이 성공하면 성공한 Result 객체를 가진 CompletableFuture 객체를 받는다`() {
        every {
            networkCartItemDataSource.updateCartItemQuantity(any(), any())
        } returns Result.success(Unit)

        sut.updateCartItemQuantity(1L, 10).thenAccept {
            assertThat(it.isSuccess).isTrue
        }
    }

    @Test
    fun `장바구니 아이템을 삭제할 때 데이터 소스에 삭제가 실패하면 실패한 Result 객체를 가진 CompletableFuture 객체를 받는다`() {
        every { networkCartItemDataSource.deleteCartItem(any()) } returns Result.failure(Throwable())

        sut.deleteCartItem(1L).thenAccept {
            assertThat(it.isFailure).isTrue
        }
    }

    @Test
    fun `장바구니 아이템을 삭제할 때 데이터 소스에 삭제가 성공하면 성공한 Result 객체를 가진 CompletableFuture 객체를 받는다`() {
        every { networkCartItemDataSource.deleteCartItem(any()) } returns Result.success(Unit)

        sut.deleteCartItem(1L).thenAccept {
            assertThat(it.isSuccess).isTrue
        }
    }
}
