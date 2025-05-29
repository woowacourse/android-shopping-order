package woowacourse.shopping.view.shoppingCart

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.invoke
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.shoppingCart.repository.ShoppingCartRepository
import woowacourse.shopping.domain.product.Product
import woowacourse.shopping.view.common.InstantTaskExecutorExtension

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
        every {
            shoppingCartRepository.load(any(), any(), captureLambda())
        } answers {
            lambda<(Result<List<Product>>) -> Unit>().invoke(Result.failure(RuntimeException()))
        }

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
