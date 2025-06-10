package woowacourse.shopping.viewmodel.product

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.domain.usecase.cart.AddToCartUseCase
import woowacourse.shopping.domain.usecase.cart.GetCartProductByProductIdUseCase
import woowacourse.shopping.domain.usecase.cart.GetCartProductsUseCase
import woowacourse.shopping.domain.usecase.cart.RemoveFromCartUseCase
import woowacourse.shopping.domain.usecase.cart.UpdateCartQuantityUseCase
import woowacourse.shopping.domain.usecase.product.GetRecentProductsUseCase
import woowacourse.shopping.domain.usecase.product.SaveRecentlyViewedProductUseCase
import woowacourse.shopping.fixture.FakeCartProductRepository
import woowacourse.shopping.fixture.FakeRecentProductRepository
import woowacourse.shopping.view.product.detail.ProductDetailViewModel
import woowacourse.shopping.viewmodel.CoroutinesTestExtension
import woowacourse.shopping.viewmodel.InstantTaskExecutorExtension
import woowacourse.shopping.viewmodel.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class ProductDetailViewModelTest {
    private lateinit var viewModel: ProductDetailViewModel
    private lateinit var cartProductRepository: CartProductRepository
    private lateinit var recentProductRepository: RecentProductRepository

    private lateinit var getRecentProductsUseCase: GetRecentProductsUseCase
    private lateinit var saveRecentlyViewedProductUseCase: SaveRecentlyViewedProductUseCase
    private lateinit var getCartProductsUseCase: GetCartProductsUseCase
    private lateinit var getCartProductByProductIdUseCase: GetCartProductByProductIdUseCase
    private lateinit var addToCartUseCase: AddToCartUseCase
    private lateinit var removeFromCartUseCase: RemoveFromCartUseCase
    private lateinit var updateCartQuantityUseCase: UpdateCartQuantityUseCase
    private lateinit var product: Product

    @BeforeEach
    fun setup() {
        cartProductRepository = FakeCartProductRepository()
        recentProductRepository = FakeRecentProductRepository()

        getRecentProductsUseCase = GetRecentProductsUseCase(recentProductRepository)
        saveRecentlyViewedProductUseCase = SaveRecentlyViewedProductUseCase(recentProductRepository)
        getCartProductsUseCase = GetCartProductsUseCase(cartProductRepository)
        getCartProductByProductIdUseCase = GetCartProductByProductIdUseCase(cartProductRepository)
        addToCartUseCase = AddToCartUseCase(cartProductRepository)
        removeFromCartUseCase = RemoveFromCartUseCase(cartProductRepository)
        updateCartQuantityUseCase = UpdateCartQuantityUseCase(cartProductRepository, removeFromCartUseCase)
        product = Product(id = 0, imageUrl = "", name = "Product 0", price = 1000, category = "")

        viewModel =
            ProductDetailViewModel(
                product,
                getRecentProductsUseCase,
                saveRecentlyViewedProductUseCase,
                getCartProductByProductIdUseCase,
                addToCartUseCase,
                updateCartQuantityUseCase,
            )
    }

    @Test
    fun `수량 증가 버튼 클릭 시 수량이 증가한다`() {
        // when
        viewModel.onQuantityIncreaseClick(product)

        // then
        assertEquals(2, viewModel.quantity.getOrAwaitValue())
    }

    @Test
    fun `수량 감소 버튼 클릭 시 수량이 감소한다`() {
        // when
        viewModel.onQuantityIncreaseClick(product)
        viewModel.onQuantityDecreaseClick(product)

        // then
        assertEquals(1, viewModel.quantity.getOrAwaitValue())
    }

    @Test
    fun `수량이 1일 때 수량 감소 버튼 클릭 시 수량이 감소하지 않는다`() {
        viewModel.onQuantityDecreaseClick(product)
        assertEquals(1, viewModel.quantity.getOrAwaitValue())
    }

    @Test
    fun `장바구니 담기 클릭 시 이벤트가 발생한다`() {
        // when
        viewModel.onAddToCartClick()

        // then
        assertEquals(Unit, viewModel.addToCartEvent.getValue())
    }

    @Test
    fun `최근 본 상품 클릭 시 이벤트가 발생한다`() {
        // when
        viewModel.onLastViewedProductClick()

        // then
        assertEquals(Unit, viewModel.lastViewedProductClickEvent.getValue())
    }
}
