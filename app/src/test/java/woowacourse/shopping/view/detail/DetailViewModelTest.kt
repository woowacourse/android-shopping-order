package woowacourse.shopping.view.detail

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.repository.FakeCartRepository
import woowacourse.shopping.data.repository.FakeProductRepository
import woowacourse.shopping.data.repository.FakeRecentProductRepository
import woowacourse.shopping.utils.CoroutinesTestExtension
import woowacourse.shopping.utils.InstantTaskExecutorExtension
import woowacourse.shopping.utils.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class DetailViewModelTest {
    private lateinit var viewModel: DetailViewModel

    @BeforeEach
    fun setUp() {
        val cartRepository = FakeCartRepository()
        val productRepository = FakeProductRepository()
        val recentProductRepository = FakeRecentProductRepository()
        viewModel =
            DetailViewModel(
                cartRepository = cartRepository,
                productRepository = productRepository,
                recentProductRepository = recentProductRepository,
                productId = 1,
            )
    }

    @Test
    fun `상품의 수량을 증가시킬 수 있다`() =
        runTest {
            // when
            viewModel.addQuantity(1)

            // then
            val actual = viewModel.productDetailUiState.getOrAwaitValue()
            assertThat(actual.quantity).isEqualTo(2)
        }

    @Test
    fun `상품의 수량을 감소시킬 수 있다`() =
        runTest {
            // when
            viewModel.addQuantity(2)
            viewModel.subtractQuantity(2)

            // then
            val actual = viewModel.productDetailUiState.getOrAwaitValue()
            assertThat(actual.quantity).isEqualTo(1)
        }

    @Test
    fun `상품의 수량을 변경한 후 장바구니에 담을 수 있다`() =
        runTest {
            // when
            viewModel.addQuantity(1)
            viewModel.addToCart()

            // then
            val actual = viewModel.detailUiEvent.getOrAwaitValue().getContentIfNotHandled()
            assertThat(actual).isEqualTo(DetailUiEvent.ProductAddedToCart)
        }

    @Test
    fun `상품의 수량을 특정 수 미만으로 감소시킬 수 없다`() =
        runTest {
            // when
            viewModel.addQuantity(2)
            viewModel.subtractQuantity(2)
            viewModel.subtractQuantity(2)

            // then
            val actualQuantity = viewModel.productDetailUiState.getOrAwaitValue().quantity
            val actualEvent = viewModel.detailUiEvent.getOrAwaitValue().getContentIfNotHandled()
            assertAll(
                { assertThat(actualQuantity).isEqualTo(1) },
                { assertThat(actualEvent).isEqualTo(DetailUiEvent.Error) },
            )
        }

    @Test
    fun `상품을 장바구니에 추가하면 상품 아이디 배열에 추가된다`() =
        runTest {
            // when
            viewModel.addToCart()

            // then
            val actual = viewModel.alteredProductIds
            assertThat(actual).isEqualTo(arrayOf(1))
        }

    @Test
    fun `이전에 상품 수량을 변경한 후 다시 변경시키면 변경이 일어난 상품 아이디 배열에 추가되지 않는다`() =
        runTest {
            // when
            viewModel.addQuantity(1)
            viewModel.addToCart()
            viewModel.subtractQuantity(1)
            viewModel.addToCart()

            // then
            val actual = viewModel.alteredProductIds
            assertThat(actual).isEqualTo(arrayOf(1))
        }
}
