package woowacourse.shopping.view.cart

import org.junit.jupiter.api.BeforeEach
import woowacourse.shopping.data.cart.repository.CartRepository

class ShoppingCartViewModelTest {
    private lateinit var repository: CartRepository
    private lateinit var viewModel: CartViewModel

    @BeforeEach
    fun setUp() {
        repository = FakeShoppingCartRepository()
        viewModel = CartViewModel(repository)
    }
}
