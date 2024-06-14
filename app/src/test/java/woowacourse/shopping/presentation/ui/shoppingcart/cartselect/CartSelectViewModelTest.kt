package woowacourse.shopping.presentation.ui.shoppingcart.cartselect

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.CoroutinesTestExtension
import woowacourse.shopping.InstantTaskExecutorExtension
import woowacourse.shopping.domain.repository.ShoppingCartRepository
import woowacourse.shopping.getOrAwaitValue
import woowacourse.shopping.remote.api.DummyData.CARTS_PULL
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@ExperimentalCoroutinesApi
@ExtendWith(MockKExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CartSelectViewModelTest {
    private lateinit var viewModel: CartSelectViewModel

    @MockK
    private lateinit var repository: ShoppingCartRepository

    @BeforeEach
    fun setUp() {
        coEvery {
            repository.getCartProductsPaged(
                0,
                5,
            )
        } returns
            Result.success(
                CARTS_PULL.copy(
                    content = CARTS_PULL.content.subList(0, 5),
                    pageable = CARTS_PULL.pageable.copy(pageNumber = 0),
                ),
            )
        coEvery {
            repository.getCartProductsPaged(
                1,
                5,
            )
        } returns
            Result.success(
                CARTS_PULL.copy(
                    content = CARTS_PULL.content.subList(5, 10),
                    pageable = CARTS_PULL.pageable.copy(pageNumber = 1),
                ),
            )
        coEvery { repository.getAllCarts() } returns Result.success(CARTS_PULL)

        viewModel = CartSelectViewModel(repository)
    }

    @Test
    fun `첫 번째 페이지에 장바구니를 불러온다`() {
        // then
        val actual = viewModel.uiState.getOrAwaitValue()
        assertThat(actual.pagingCartProduct.carts).isEqualTo(
            CARTS_PULL.content.subList(0, 5),
        )
    }

    @Test
    fun `첫 번째 페이지에서 다음 페이지로 넘어가면 다음 페이지 장바구니를 불러온다`() {
        // when
        viewModel.loadNextPage()

        // then
        val actual = viewModel.uiState.getOrAwaitValue()
        assertThat(actual.pagingCartProduct.carts).isEqualTo(
            CARTS_PULL.content.subList(5, 10),
        )
    }

    @Test
    fun `두 번째 페이지에서 이전 페이지로 넘어가면 이전 페이지 장바구니를 불러온다`() {
        // given
        viewModel.loadNextPage()
        viewModel.loadNextPage()
        // when
        viewModel.loadPreviousPage()

        // then
        val actual = viewModel.uiState.getOrAwaitValue()
        assertThat(actual.pagingCartProduct.carts).isEqualTo(
            CARTS_PULL.content.subList(0, 5),
        )
    }

    @Test
    fun `주문을 삭제하면 장바구니에 주문이 사라진다`() {
        // given
        val productIdSlot = slot<Int>()
        coEvery { repository.deleteCartProductById(capture(productIdSlot)) } returns
            Result.success(
                Unit,
            )
        coEvery {
            repository.getCartProductsPaged(
                0,
                5,
            )
        } returns
            Result.success(
                CARTS_PULL.copy(
                    content = CARTS_PULL.content.subList(1, 6),
                    pageable = CARTS_PULL.pageable.copy(pageNumber = 0),
                ),
            )

        // when
        viewModel.deleteCartProduct(CARTS_PULL.content.first().id)

        // then
        val latch = CountDownLatch(1)
        latch.await(1, TimeUnit.SECONDS)

        coVerify { repository.deleteCartProductById(CARTS_PULL.content.first().id) }
        assertThat(productIdSlot.captured).isEqualTo(CARTS_PULL.content.first().id)

        val actual = viewModel.uiState.getOrAwaitValue()
        assertThat(actual.pagingCartProduct.carts).isEqualTo(CARTS_PULL.content.subList(1, 6))
    }

    @Test
    fun `첫 번째 상품을 체크하면 주문 상품에 추가된다`() {
        // given & when
        viewModel.checkCartProduct(CARTS_PULL.content.first())

        // then
        val actual = viewModel.uiState.getOrAwaitValue()

        assertThat(listOf(actual.orderCarts.values.first().id)).isEqualTo(
            listOf(CARTS_PULL.content.first().id),
        )
    }

    @Test
    fun `전체 상품을 체크하면 전체 상품이 주문 상품에 추가된다`() {
        // given & when
        viewModel.checkAllCartProduct()

        // then
        val actual = viewModel.uiState.getOrAwaitValue()
        assertThat(actual.orderCarts.values.toList()).isEqualTo(CARTS_PULL.content)
    }
}
