package woowacourse.shopping.viewModel.shoppingCart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.clearAllMocks
import io.mockk.mockk
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.view.shoppingCart.ShoppingCartViewModel
import woowacourse.shopping.viewModel.common.InstantTaskExecutorExtension

@ExtendWith(InstantTaskExecutorExtension::class)
class ShoppingCartViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var shoppingCartViewModel: ShoppingCartViewModel
    private lateinit var shoppingCartRepository: ShoppingCartRepository

    @BeforeEach
    fun setUp() {
        shoppingCartRepository = mockk()
        shoppingCartViewModel = ShoppingCartViewModel(shoppingCartRepository)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }
}
