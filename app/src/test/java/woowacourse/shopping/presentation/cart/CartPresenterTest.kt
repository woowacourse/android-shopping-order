package woowacourse.shopping.presentation.cart

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.cart.CartRepository
import woowacourse.shopping.data.recentproduct.RecentProductRepository
import woowacourse.shopping.model.CartProduct
import woowacourse.shopping.presentation.fixture.CartProductFixture

class CartPresenterTest {
    private lateinit var presenter: CartContract.Presenter
    private lateinit var view: CartContract.View
    private lateinit var cartRepository: CartRepository
    private lateinit var recentProductRepository: RecentProductRepository

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        recentProductRepository = mockk(relaxed = true)
        presenter = CartPresenter(view, cartRepository)

        every {
            cartRepository.getCartProducts(
                callback = any(),
            )
        } answers {
            val callback = args[0] as (List<CartProduct>) -> Unit
            callback(
                CartProductFixture.getCartProducts(
                    quantity = 2,
                    1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
                ),
            )
        }
    }

    @Test
    fun `장바구니 목록을 불러온다`() {
        // given : 장바구니 목록을 불러올 수 있는 상태다.
        every {
            view.showCartProductModels(
                CartProductFixture.getCheckableCartProductModels(quantity = 2, 1, 2, 3, 4, 5),
            )
        } just runs

        // when : 장바구니 목록 불러오기 요청을 보낸다.
        presenter.loadCarts()

        // then : 장바구니 목록이 노출된다.
        verify {
            view.showCartProductModels(
                CartProductFixture.getCheckableCartProductModels(quantity = 2, 1, 2, 3, 4, 5),
            )
        }
    }

    @Test
    fun `장바구니 아이템을 삭제한다`() {
        // given : 장바구니 아이템을 삭제할 수 있는 상태다.
        every {
            view.showCartProductModels(
                CartProductFixture.getCheckableCartProductModels(quantity = 2, 3, 4, 5, 6),
            )
        } just runs

        every {
            cartRepository.deleteCartProduct(1, any())
        } answers {
            val callback = args[1] as () -> Unit
            callback()
        }

        presenter.loadCarts()

        // when : 장바구니 아이템 삭제 요청을 보낸다.
        presenter.deleteCartProductModel(
            cartProductModel = CartProductFixture.getCheckableCartProductModel(1),
        )

        // then : 삭제되고 남은 아이템이 노출된다.
        verify {
            view.showCartProductModels(
                CartProductFixture.getCheckableCartProductModels(quantity = 2, 2, 3, 4, 5, 6),
            )
        }
    }

    @Test
    fun `장바구니 아이템의 상품 개수를 증가시킨다`() {
        // given : 장바구니 아이템의 상품 개수를 증가시킬 수 있는 상태다.

        val expected =
            CartProductFixture.getCheckableCartProductModels(quantity = 2, 1, 2, 3, 4) +
                CartProductFixture.getCheckableCartProductModel(5, 3)

        every { view.showCartProductModels(expected) } just runs

        every {
            cartRepository.updateCartProductCount(any(), any(), any())
        } answers {
            val callback = args[2] as () -> Unit
            callback()
        }
        presenter.loadCarts()

        // when : 장바구니 아이템 상품 개수 증가 요청을 보낸다.
        presenter.addProductCartCount(
            cartProductModel = CartProductFixture.getCheckableCartProductModel(5),
        )

        // then : 장바구니 아이템 상품 개수가 증가되어 화면에 노출된다.
        verify { view.showCartProductModels(expected) }
    }

    @Test
    fun `장바구니 아이템의 상품 개수를 감소시킨다`() {
        // given : 장바구니 아이템의 상품 개수를 감소 시킬 수 있는 상태다.

        val expected = CartProductFixture.getCheckableCartProductModels(quantity = 2, 1, 2, 3, 4) +
            CartProductFixture.getCheckableCartProductModel(5, 1)

        every { view.showCartProductModels(expected) } just runs

        every {
            cartRepository.updateCartProductCount(any(), any(), any())
        } answers {
            val callback = args[2] as () -> Unit
            callback()
        }
        presenter.loadCarts()

        // when : 장바구니 아이템 상품 개수 감소 요청을 보낸다.
        presenter.subProductCartCount(
            cartProductModel = CartProductFixture.getCheckableCartProductModel(5, 2),
        )

        // then : 장바구니 아이템 상품 개수가 감소되어 화면에 노출된다.
        verify { view.showCartProductModels(expected) }
    }

    @Test
    fun `장바구니 아이템의 선택을 해제한다`() {
        // given : 장바구니 아이템의 선택 상태를 변경할 수 있는 상태이다.
        val selectedExpectedProduct =
            CartProductFixture.getCartProducts(2, 1, 2, 3, 4, 6, 7, 8, 9, 10)
        val expectedCount = selectedExpectedProduct.sumOf { it.quantity }
        val expectedPrice = selectedExpectedProduct.sumOf { it.getTotalPrice() }

        every { view.showAllCheckBoxIsChecked(false) } just runs
        every { view.showTotalCount(expectedCount) } just runs
        every { view.showTotalPrice(expectedPrice) } just runs

        presenter.loadCarts()

        // when : 장바구니 아이템 선택 상태 변경 요청을 보낸다.
        presenter.changeProductSelected(
            productModel = CartProductFixture.getProductModel(5),
        )

        // then : 장바구니 아이템 전체 선택을 해제한다.
        verify { view.showAllCheckBoxIsChecked(false) }

        // and : 선택된 상품 전체 가격을 업데이트 한다.
        verify { view.showTotalCount(expectedCount) }

        // and : 선택된 상품 전체 개수를 업데이트 한다.
        verify { view.showTotalPrice(expectedPrice) }
    }

    @Test
    fun `현재 장바구니 모든 아이템의 선택을 해제한다`() {
        // given : 장바구니 목록 전체를 선택할 수 있는 상태이다.
        val selectedExpectedProduct =
            CartProductFixture.getCartProducts(2, 6, 7, 8, 9, 10)
        val expectedCount = selectedExpectedProduct.sumOf { it.quantity }
        val expectedPrice = selectedExpectedProduct.sumOf { it.getTotalPrice() }

        every { view.showAllCheckBoxIsChecked(false) } just runs
        every { view.showTotalCount(expectedCount) } just runs
        every { view.showTotalPrice(expectedPrice) } just runs

        presenter.loadCarts()

        // when : 장바구니 목록 전체 선택 해제 요청을 보낸다.
        presenter.selectAllProduct(false)

        // then : 장바구니 아이템 전체 선택을 해제한다.
        verify { view.showAllCheckBoxIsChecked(false) }

        // and : 선택된 상품 전체 가격을 업데이트 한다.
        verify { view.showTotalCount(expectedCount) }

        // and : 선택된 상품 전체 개수를 업데이트 한다.
        verify { view.showTotalPrice(expectedPrice) }
    }

    @Test
    fun `다음 페이지로 넘긴다`() {
        // given : 다음 페이지로 넘길 수 있는 상태다.
        every {
            view.showCartProductModels(
                CartProductFixture.getCheckableCartProductModels(quantity = 2, 6, 7, 8, 9, 10),
            )
        } just runs
        presenter.loadCarts()

        // when : 다음 페이지로 넘기기 요청을 보낸다.
        presenter.plusPage()

        // then : 다음 페이지 장바구니 상품들이 노출된다.
        verify {
            view.showCartProductModels(
                CartProductFixture.getCheckableCartProductModels(quantity = 2, 6, 7, 8, 9, 10),
            )
        }
    }

    @Test
    fun `이전 페이지로 넘긴다`() {
        // given : 이전 페이지로 돌아갈 수 있는 상태다.
        every {
            view.showCartProductModels(
                CartProductFixture.getCheckableCartProductModels(quantity = 2, 1, 2, 3, 4, 5),
            )
        } just runs
        presenter.loadCarts()

        // when : 이전 페이지로 돌아가기 요청을 보낸다.
        presenter.plusPage()

        // then : 이전 페이지 장바구니 상품들이 노출된다.
        verify {
            view.showCartProductModels(
                CartProductFixture.getCheckableCartProductModels(quantity = 2, 1, 2, 3, 4, 5),
            )
        }
    }
}
