package woowacourse.shopping

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import io.mockk.Called
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.model.toUiModel
import woowacourse.shopping.view.cart.CartContract
import woowacourse.shopping.view.cart.CartPresenter

class CartPresenterTest {
    private lateinit var presenter: CartContract.Presenter
    private lateinit var view: CartContract.View
    private lateinit var cartRepository: CartRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartRepository = object : CartRepository {
            override fun getAll(callback: (List<CartProduct>) -> Unit) {
                callback(CartProductsFixture.cartProducts)
            }

            override fun insert(productId: Int, quantity: Int, callback: (Int) -> Unit) {
                callback(1)
            }

            override fun update(cartId: Int, quantity: Int, callback: (Boolean) -> Unit) {
                callback(true)
            }

            override fun remove(cartId: Int, callback: (Boolean) -> Unit) {
                callback(true)
            }
        }

        presenter = CartPresenter(view, cartRepository)
    }

    @Test
    fun 장바구니의_상품을_띄울_수_있다() {
        // given

        // when
        presenter.fetchProducts()

        // then
        verify(exactly = 1) { view.showProducts(any()) }
    }

    @Test
    fun 다음_페이지를_띄울_수_있다() {
        // given
        presenter.fetchProducts()

        // when
        presenter.fetchNextPage()

        // then
        verify(exactly = 1) { view.showChangedItems() }
    }

    @Test
    fun 이전_페이지를_띄울_수_있다() {
        // given
        presenter.fetchProducts()
        presenter.fetchNextPage()

        // when
        presenter.fetchPrevPage()

        // then
        verify(exactly = 2) { view.showChangedItems() }
    }

    @Test
    fun 장바구니_상품을_삭제할_수_있다() {
        // given
        presenter.fetchProducts()

        // when
        presenter.removeProduct(1)

        // then
        verify(exactly = 1) { view.showChangedItems() }
    }

    @Test
    fun 업데이트_하려는_상품의_개수가_1이상이면_업데이트할_수_있다() {
        // given
        presenter.fetchProducts()

        // when
        presenter.updateCartProductCount(1, 3)

        // then
        verify(exactly = 1) { view.showChangedItem(any()) }
    }

    @Test
    fun 업데이트_하려는_상품의_개수가_1미만이면_업데이트하지_않는다() {
        // given
        presenter.fetchProducts()

        // when
        presenter.updateCartProductCount(1, 0)

        // then
        verify(exactly = 0) { view.showChangedItem(any()) }
    }

    @Test
    fun 장바구니_상품을_선택하면_가격과_주문개수를_수정한다() {
        // given
        presenter.fetchProducts()

        // when
        presenter.checkProduct(CartProductsFixture.cartProducts[0].toUiModel(false))

        // then
        val expectedTotalPrice = 10000
        val expectedTotalCount = 1
        val expectedTotalCheckAll = false

        val actualTotalPrice = presenter.cartSystemResult.value?.totalPrice
        val actualTotalCount = presenter.cartSystemResult.value?.totalCount
        val actualTotalCheckAll = presenter.isCheckedAll.value

        assertEquals(expectedTotalPrice, actualTotalPrice)
        assertEquals(expectedTotalCount, actualTotalCount)
        assertEquals(expectedTotalCheckAll, actualTotalCheckAll)
    }

    @Test
    fun 장바구니_상품을_한번에_선택할_수_있다() {
        presenter.fetchProducts()
        presenter.checkProductsAll()

        val expectedIndexes = listOf(0, 1, 2, 3, 4)
        val expectedTotalPrice = 50000
        val expectedTotalCount = 5
        val expectedTotalCheckAll = true

        val actualTotalPrice = presenter.cartSystemResult.value?.totalPrice
        val actualTotalCount = presenter.cartSystemResult.value?.totalCount
        val actualTotalCheckAll = presenter.isCheckedAll.value

        expectedIndexes.forEach {
            verify { view.showChangedItem(it) }
        }
        assertEquals(expectedTotalPrice, actualTotalPrice)
        assertEquals(expectedTotalCount, actualTotalCount)
        assertEquals(expectedTotalCheckAll, actualTotalCheckAll)
    }
}
