package woowacourse.shopping.ui.detail

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.fixture.CoroutinesTestExtension
import woowacourse.shopping.fixture.InstantTaskExecutorExtension
import woowacourse.shopping.fixture.fake.FakeCartRepository
import woowacourse.shopping.fixture.fake.FakeProductRepository
import woowacourse.shopping.fixture.fake.FakeRecentRepository
import woowacourse.shopping.fixture.getOrAwaitValue
import woowacourse.shopping.ui.detail.viewmodel.ProductDetailViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class, CoroutinesTestExtension::class)
class ProductDetailViewModelTest {
    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var productRepository: FakeProductRepository
    private lateinit var recentProductRepository: FakeRecentRepository
    private lateinit var cartRepository: FakeCartRepository

    @BeforeEach
    fun setUp() {
        recentProductRepository = FakeRecentRepository()
        cartRepository = FakeCartRepository()
        productRepository = FakeProductRepository(cartRepository)
        viewModel =
            ProductDetailViewModel(
                PRODUCT_ID,
                productRepository,
                recentProductRepository,
                cartRepository,
            )
    }

    @Test
    fun `뷰모델을 생성하면, 초기 데이터가 불러와진다`() =
        runTest {
            // given
            val actual = viewModel.productWithQuantity.getOrAwaitValue()

            // then
            assertThat(actual.product).isEqualTo(FakeProductRepository.productStubs.first { it.id == PRODUCT_ID })
        }

    @Test
    fun `상품의 수량이 0일 때, 증가 시키면 상품의 수량이 1이 된다`() =
        runTest {
            // when
            val before = viewModel.productWithQuantity.getOrAwaitValue().quantity.value
            assertThat(before).isEqualTo(0)

            // given
            viewModel.plusCount(PRODUCT_ID)

            // then
            val actual = viewModel.productWithQuantity.getOrAwaitValue().quantity.value
            assertThat(actual).isEqualTo(1)
        }

    @Test
    fun `상품의 수량이 2일 때, 감소시키면 상품의 수량이 1이 된다`() =
        runTest {
            // when
            viewModel.plusCount(PRODUCT_ID)
            viewModel.plusCount(PRODUCT_ID)
            val before = viewModel.productWithQuantity.getOrAwaitValue().quantity.value
            assertThat(before).isEqualTo(2)

            // given
            viewModel.minusCount(PRODUCT_ID)

            // then
            val actual = viewModel.productWithQuantity.getOrAwaitValue().quantity.value
            assertThat(actual).isEqualTo(1)
        }

    companion object {
        private const val PRODUCT_ID = 1L
    }
}
