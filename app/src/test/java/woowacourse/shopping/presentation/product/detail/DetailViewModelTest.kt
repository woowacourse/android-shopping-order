package woowacourse.shopping.presentation.product.detail

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.fixture.FakeCartItemRepository
import woowacourse.shopping.fixture.FakeCatalogItemRepository
import woowacourse.shopping.fixture.FakeViewedItemRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.util.InstantTaskExecutorExtension
import kotlin.test.Test

@ExtendWith(InstantTaskExecutorExtension::class)
class DetailViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: DetailViewModel

    @Test
    fun `최근 본 상품이 존재하면 loadLastViewedItem 호출 시 lastViewed가 설정된다`() {
        viewModel =
            DetailViewModel(
                FakeCatalogItemRepository(20),
                FakeCartItemRepository(0),
                FakeViewedItemRepository(1),
                productId = 100,
            )

        viewModel.setProduct()
        viewModel.loadLastViewedItem()

        val lastViewed = viewModel.lastViewed.value
        assertThat(lastViewed?.name).isEqualTo("1 아이스 카페 아메리카노")
    }

    @Test
    fun `최근 본 상품이 현재 상품과 같으면 lastViewed는 null이 된다`() {
        viewModel =
            DetailViewModel(
                FakeCatalogItemRepository(30),
                FakeCartItemRepository(0),
                FakeViewedItemRepository(1),
                productId = 100,
            )

        viewModel.setProduct()
        viewModel.loadLastViewedItem()

        val lastViewed = viewModel.lastViewed.value
        assertThat(lastViewed).isEqualTo(lastViewed)
    }

    @Test
    fun `상품을 장바구니에 담을 수 있다`() {
        val cartRepository = FakeCartItemRepository(0)

        viewModel = DetailViewModel(
            FakeCatalogItemRepository(30),
            cartRepository,
            FakeViewedItemRepository(1),
            productId = 10L
        )

        viewModel.setProduct()

        cartRepository.addCartItem(id= 10L, quantity = 2) {  }
        val cartItems = mutableListOf<ProductUiModel>()
        cartRepository.getCartItems(page = 0, size = 5) { result ->
            result.onSuccess { pagingData ->
                cartItems.addAll(pagingData.products)
                println(pagingData.toString())
            }
        }

        assertThat(cartItems).hasSize(1)
        assertThat(cartItems[0].quantity).isEqualTo(2)
        assertThat(cartItems[0].id).isEqualTo(10L)
    }


    @Test
    fun `상품을 원하는 갯수만큼 장바구니에 담을 수 있다`() {
        val cartRepository = FakeCartItemRepository(0)

        viewModel = DetailViewModel(
            FakeCatalogItemRepository(30),
            cartRepository,
            FakeViewedItemRepository(1),
            productId = 10L
        )

        viewModel.setProduct()
        cartRepository.addCartItem(id= 10L, quantity = 3) {  }

        val cartItems = mutableListOf<ProductUiModel>()
        cartRepository.getCartItems(page = 0, size = 5) { result ->
            result.onSuccess { pagingData ->
                cartItems.addAll(pagingData.products)
            }
        }

        assertThat(cartItems).hasSize(1)
        assertThat(cartItems[0].quantity).isEqualTo(3)
        assertThat(cartItems[0].id).isEqualTo(10L)
    }
}
