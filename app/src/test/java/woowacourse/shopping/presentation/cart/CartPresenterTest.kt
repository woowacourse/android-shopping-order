package woowacourse.shopping.presentation.cart

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.CartProductInfo
import woowacourse.shopping.presentation.mapper.toDomain
import woowacourse.shopping.presentation.model.CartProductInfoListModel
import woowacourse.shopping.presentation.model.CartProductInfoModel
import woowacourse.shopping.presentation.model.OrderProductModel
import woowacourse.shopping.presentation.model.OrderProductsModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.repository.CartRepository

class CartPresenterTest {
    private lateinit var view: CartContract.View
    private lateinit var presenter: CartContract.Presenter
    private lateinit var cartRepository: CartRepository
    private val notOrderedCartList = CartProductInfoListModel(
        listOf(
            CartProductInfoModel(0, makeTestProductModel(0), 1, false),
            CartProductInfoModel(1, makeTestProductModel(1), 1, false),
            CartProductInfoModel(2, makeTestProductModel(2), 1, false),
            CartProductInfoModel(3, makeTestProductModel(3), 1, false),
            CartProductInfoModel(4, makeTestProductModel(4), 1, false),
        )
    )

    private val orderedCartList = CartProductInfoListModel(
        listOf(
            CartProductInfoModel(0, makeTestProductModel(0), 1, true),
            CartProductInfoModel(1, makeTestProductModel(1), 1, true),
            CartProductInfoModel(2, makeTestProductModel(2), 1, true),
            CartProductInfoModel(3, makeTestProductModel(3), 1, true),
            CartProductInfoModel(4, makeTestProductModel(4), 1, true),
        )
    )

    private fun makeTestProductModel(id: Int): ProductModel {
        return ProductModel(id, "", "", 1000)
    }

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
    }

    @Test
    fun 카트_목록을_불러와서_상품_목록을_보여준다() {
        // given
        presenter = CartPresenter(view = view, cartRepository = cartRepository)
        val slot = slot<(List<CartProductInfo>) -> Unit>()
        every { cartRepository.getAllCartItems(onSuccess = capture(slot)) } answers {
            slot.captured.invoke(notOrderedCartList.items.map { it.toDomain() })
        }
        // when
        presenter.loadCartItems()
        // then
        verify { view.setCartItems(notOrderedCartList.items) }
    }

    @Test
    fun 페이지의_0번째_상품을_삭제할_수_있다() {
        // given
        presenter = CartPresenter(view, cartRepository, initCartProducts = notOrderedCartList)
        val deleteItem = notOrderedCartList.items[0]
        val slot = slot<() -> Unit>()
        every { cartRepository.deleteCartItem(deleteItem.id, onSuccess = capture(slot)) } answers {
            slot.captured.invoke()
        }
        // when
        presenter.deleteProductItem(deleteItem)
        // then
        val expected = CartProductInfoListModel(
            listOf(
                CartProductInfoModel(1, makeTestProductModel(1), 1, false),
                CartProductInfoModel(2, makeTestProductModel(2), 1, false),
                CartProductInfoModel(3, makeTestProductModel(3), 1, false),
                CartProductInfoModel(4, makeTestProductModel(4), 1, false),
            )
        ).items
        verify { view.setCartItems(expected) }
    }

    @Test
    fun 특정_상품의_count를_업데이트_할_수_있다() {
        // given
        presenter = CartPresenter(view, cartRepository, initCartProducts = notOrderedCartList)
        val updateItem = notOrderedCartList.items[0]
        val slot = slot<() -> Unit>()
        val cartItemsSlot = slot<List<CartProductInfoModel>>()
        every { cartRepository.updateCartItemQuantity(0, 3, onSuccess = capture(slot)) } answers {
            slot.captured.invoke()
        }
        // when
        presenter.updateProductCount(updateItem, 3)
        // then
        val expected = CartProductInfoListModel(
            listOf(
                CartProductInfoModel(0, makeTestProductModel(0), 3, false),
                CartProductInfoModel(1, makeTestProductModel(1), 1, false),
                CartProductInfoModel(2, makeTestProductModel(2), 1, false),
                CartProductInfoModel(3, makeTestProductModel(3), 1, false),
                CartProductInfoModel(4, makeTestProductModel(4), 1, false),
            )
        ).items
        verify { view.setCartItems(expected) }
    }

    @Test
    fun 전체_체크박스를_체크하면_페이지에_있는_모든_상품을_주문목록에_추가한다() {
        // given
        presenter = CartPresenter(view, cartRepository, initCartProducts = notOrderedCartList)
        // when
        presenter.changeCurrentPageProductsOrder(true)
        // then
        val expected = orderedCartList.items
        verify { view.setCartItems(expected) }
    }

    @Test
    fun 전체_체크박스를_해제하면_페이지에_있는_모든_상품을_주문목록에서_해제한다() {
        // given
        presenter = CartPresenter(view, cartRepository, initCartProducts = orderedCartList)
        // when
        presenter.changeCurrentPageProductsOrder(false)
        // then
        val expected = notOrderedCartList.items
        verify { view.setCartItems(expected) }
    }

    @Test
    fun `상품의_체크박스를_체크하면_주문목록에_추가되고,주문 관련 뷰가 업데이트 된다`() {
        // given
        presenter = CartPresenter(
            view = view,
            cartRepository = cartRepository,
            initCartProducts = notOrderedCartList,
            initPage = 1,
        )
        val notOrderItem = CartProductInfoModel(0, makeTestProductModel(1), 1)
        // when
        presenter.addProductInOrder(notOrderItem)
        // then
        verify { view.setOrderCount(1) }
        verify { view.setOrderPrice(1000) }
        verify { view.setAllIsOrderCheck(false) }
    }

    @Test
    fun `상품의_체크박스를_해제하면_주문목록에서_제거되고, 주문 관련 뷰가 업데이트된다`() {
        // given
        presenter = CartPresenter(
            view = view,
            cartRepository = cartRepository,
            initCartProducts = orderedCartList,
            initPage = 1,
        )
        val orderItem = CartProductInfoModel(0, makeTestProductModel(1), 1)
        // when
        presenter.deleteProductInOrder(orderItem)
        // then
        verify { view.setOrderCount(4) }
        verify { view.setOrderPrice(4000) }
        verify { view.setAllIsOrderCheck(false) }
    }

    @Test
    fun 다음_페이지에_상품이_있으면_오른쪽페이지_버튼상태를_true로_한다() {
        // given
        presenter = CartPresenter(
            view = view,
            cartRepository = cartRepository,
            initCartProducts = CartProductInfoListModel(orderedCartList.items + orderedCartList.items),
            initPage = 1,
        )
        // when
        presenter.checkPlusPageAble()
        // then
        verify { view.setUpPlusPageState(true) }
    }

    @Test
    fun 다음_페이지에_상품이_없으면_오른쪽페이지_버튼상태를_false로_한다() {
        // given
        presenter = CartPresenter(
            view = view,
            cartRepository = cartRepository,
            initCartProducts = orderedCartList,
            initPage = 1,
        )
        // when
        presenter.checkPlusPageAble()
        // then
        verify { view.setUpPlusPageState(false) }
    }

    @Test
    fun 현재_페이지가_1이라면_왼쪽버튼상태를_false로_한다() {
        // given
        presenter = CartPresenter(
            view = view,
            cartRepository = cartRepository,
            initPage = 1,
        )
        // when
        presenter.checkMinusPageAble()
        // then
        verify { view.setUpMinusPageState(false) }
    }

    @Test
    fun 현재_페이지가_1이_아니라면_왼쪽버튼상태를_true로_한다() {
        // given
        presenter = CartPresenter(
            view = view,
            cartRepository = cartRepository,
            initPage = 2,
        )
        // when
        presenter.checkMinusPageAble()
        // then
        verify { view.setUpMinusPageState(true) }
    }

    @Test
    fun 오른쪽_페이지_버튼을_누르면_페이지를_1_증가시킨다() {
        // given
        presenter = CartPresenter(
            view = view,
            cartRepository = cartRepository,
            initPage = 1,
        )
        // when
        presenter.plusPage()
        // then
        verify { view.setPage("2") }
    }

    @Test
    fun 왼쪽_버튼을_누르면_페이지를_1_감소시킨다() {
        // given
        presenter = CartPresenter(
            view = view,
            cartRepository = cartRepository,
            initPage = 2,
        )
        // when
        presenter.minusPage()
        // then
        verify { view.setPage("1") }
    }

    @Test
    fun 결제_버튼을_누르면_주문_상태인_상품과_함께_결제_화면으로_넘어간다() {
        // given
        presenter = CartPresenter(
            view = view,
            cartRepository = cartRepository,
            initCartProducts = orderedCartList,
        )
        // when
        presenter.order()
        // then
        val expected = OrderProductsModel(
            listOf(
                OrderProductModel(makeTestProductModel(0), 1),
                OrderProductModel(makeTestProductModel(1), 1),
                OrderProductModel(makeTestProductModel(2), 1),
                OrderProductModel(makeTestProductModel(3), 1),
                OrderProductModel(makeTestProductModel(4), 1),
            )
        )
        view.showOrderView(expected)
    }
}
