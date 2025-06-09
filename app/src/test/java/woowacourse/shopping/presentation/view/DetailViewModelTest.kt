package woowacourse.shopping.presentation.view

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.model.product.toDomain
import woowacourse.shopping.fixture.FakeCartRepository
import woowacourse.shopping.fixture.FakeProductRepository
import woowacourse.shopping.fixture.productsFixture
import woowacourse.shopping.presentation.common.model.toProduct
import woowacourse.shopping.presentation.view.detail.DetailViewModel
import woowacourse.shopping.presentation.view.util.CoroutinesTestExtension
import woowacourse.shopping.presentation.view.util.InstantTaskExecutorExtension
import woowacourse.shopping.presentation.view.util.getOrAwaitValue

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class DetailViewModelTest {
    private lateinit var viewModel: DetailViewModel

    @BeforeEach
    fun setUp() {
        val fakeProductRepository = FakeProductRepository()
        val fakeCartRepository = FakeCartRepository()
        viewModel =
            DetailViewModel(
                1,
                fakeProductRepository,
                fakeCartRepository,
            )
    }

    @Test
    fun `상품 ID에 해당하는 상품을 조회한다`() {
        // When
        val result = viewModel.product.getOrAwaitValue().toProduct()
        val expected = productsFixture.find { it.id == 1L }?.toDomain()

        // Then
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `최근에 본 상품을 조회한다`() {
        // When
        val result = viewModel.recentProduct.getOrAwaitValue().toProduct()

        val expected = productsFixture.find { it.id == 1L }?.toDomain()

        // Than
        assertThat(result).isEqualTo(expected)
    }

    @Test
    fun `상품을 장바구니에 추가하면 저장 상태가 변경된다`() {
        // When
        viewModel.addProductToCart()
        val result = viewModel.addToCartSuccessEvent.getValue()

        // Then
        assertThat(result).isNotNull
    }

    @Test
    fun `장바구니에 담을 상품 초기 개수는 1이다`() {
        // When
        val quantity = viewModel.quantity.getOrAwaitValue()

        // Then
        assertThat(quantity).isEqualTo(1)
    }

    @Test
    fun `장바구니에 담을 상품 개수를 증가시킬 수 있다`() {
        // Give
        val beforeQuantity = viewModel.quantity.getOrAwaitValue()

        // When
        viewModel.increaseQuantity()
        val afterQuantity = viewModel.quantity.getOrAwaitValue()

        // Then
        assertThat(afterQuantity).isGreaterThan(beforeQuantity)
    }

    @Test
    fun `장바구니에 담을 상품 개수를 감소시킬 수 있다`() {
        // Give
        viewModel.increaseQuantity()
        viewModel.increaseQuantity()
        val beforeQuantity = viewModel.quantity.getOrAwaitValue()

        // When
        viewModel.decreaseQuantity()
        val afterQuantity = viewModel.quantity.getOrAwaitValue()

        // Then
        assertThat(afterQuantity).isLessThan(beforeQuantity)
    }

    @Test
    fun `장바구니에 담을 상품 개수는 1개 미만으로 감소시킬 수 없다`() {
        // Give
        val initialQuantity = 1

        val beforeQuantity = viewModel.quantity.getOrAwaitValue()
        assertThat(beforeQuantity).isEqualTo(initialQuantity)

        // When
        viewModel.decreaseQuantity()
        val afterQuantity = viewModel.quantity.getOrAwaitValue()

        // Then
        assertThat(afterQuantity).isEqualTo(initialQuantity)
    }
}
