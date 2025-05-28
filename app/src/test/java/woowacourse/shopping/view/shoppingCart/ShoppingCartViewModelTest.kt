package woowacourse.shopping.view.shoppingCart

import org.junit.jupiter.api.BeforeEach
import woowacourse.shopping.data.shoppingCart.repository.CartRepository

class ShoppingCartViewModelTest {
    private lateinit var repository: CartRepository
    private lateinit var viewModel: ShoppingCartViewModel

    @BeforeEach
    fun setUp() {
        repository = FakeShoppingCartRepository()
        viewModel = ShoppingCartViewModel(repository)
    }
}
