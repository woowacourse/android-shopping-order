package woowacourse.shopping.ui.shopping

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.repository.CartItemRepository
import woowacourse.shopping.repository.ProductRepository
import woowacourse.shopping.repository.RecentlyViewedProductRepository
import woowacourse.shopping.repository.UserRepository
import java.util.concurrent.CompletableFuture

class ShoppingPresenterTest {
    private lateinit var view: ShoppingContract.View
    private lateinit var recentlyViewedProductRepository: RecentlyViewedProductRepository
    private lateinit var productRepository: ProductRepository
    private lateinit var cartItemRepository: CartItemRepository
    private lateinit var userRepository: UserRepository
    private val pageSize = 20

    @Before
    fun setUp() {
        view = mockk()
        recentlyViewedProductRepository = mockk()
        productRepository = mockk()
        cartItemRepository = mockk()
        userRepository = mockk()
    }

    @Test
    fun loadProductsNextPage() {
        // given
        val presenter = ShoppingPresenter(
            view,
            recentlyViewedProductRepository,
            productRepository,
            cartItemRepository,
            userRepository,
            pageSize = pageSize
        )
        every {
            productRepository.findAll(any(), any())
        } returns CompletableFuture.completedFuture(Result.success(emptyList()))
        every {
            cartItemRepository.findAll(any())
        } returns CompletableFuture.completedFuture(Result.success(emptyList()))
        every {
            view.addProducts(any())
        } just runs

        // when
        presenter.loadProductsNextPage()

        // then
        verify { productRepository.findAll(pageSize, 0) }
        verify { cartItemRepository.findAll(any()) }
        verify { view.addProducts(any()) }
    }
}
