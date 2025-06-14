package woowacourse.shopping.view.shoppingCart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.view.common.CoroutinesTestExtension
import woowacourse.shopping.view.common.InstantTaskExecutorExtension

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
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

    @Test
    fun `장바구니 업데이트 실패 시 단발성 이벤트로 UPDATE_SHOPPING_CART_FAILURE 값을 가진다`() {
        // given:
        coEvery { shoppingCartRepository.load(any(), any()) } returns
            Result.failure(
                RuntimeException(),
            )

        // when:
        shoppingCartViewModel.updateShoppingCart()

        // then:
        val event = shoppingCartViewModel.event.getValue()
        assertThat(event).isEqualTo(ShoppingCartEvent.UPDATE_SHOPPING_CART_FAILURE)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }
}
