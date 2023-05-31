import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.data.repository.UserRepository
import woowacourse.shopping.domain.repository.BasketRepository
import woowacourse.shopping.ui.basket.BasketContract
import woowacourse.shopping.ui.basket.BasketFixture
import woowacourse.shopping.ui.basket.BasketPresenter
import woowacourse.shopping.ui.basket.UserFixture
import woowacourse.shopping.ui.model.User

class BasketPresenterTest() {
    private lateinit var view: BasketContract.View
    private lateinit var basketRepository: BasketRepository
    private lateinit var userRepository: UserRepository
    private lateinit var orderRepository: OrderRepository
    private lateinit var presenter: BasketPresenter

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        basketRepository = mockk(relaxed = true)
        userRepository = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)

        presenter = BasketPresenter(
            view = view,
            userRepository = userRepository,
            basketRepository = basketRepository,
            orderRepository = orderRepository
        )
        presenter.basket = BasketFixture.createBasket()
    }

//    override fun usePoint() {
//        userRepository.getUser {
//            view.showUsingPointDialog(it)
//        }
//    }

    @Test
    fun `포인트를 사용하기 위해서 저장소로부터 유저 정보를 받아 다이얼로그를 띄운다`() {
        // given
        val slotShowUserPointDialog = slot<(User) -> Unit>()
        val user = UserFixture.createUser()

        every {
            userRepository.getUser(
                onReceived = capture(slotShowUserPointDialog)
            )
        } answers {
            slotShowUserPointDialog.captured.invoke(user)
        }

        // when: 포인트를 사용한다
        presenter.usePoint()

        // then: repository로부터 user정보를 받아 dialog를 띄운다.
        verify { view.showUsingPointDialog(user) }
    }

    @Test
    fun `주문을 저장소에 추가하면 주문 id를 받아서 주문 상세화면을 보여준다`() {
        // given
        val slotShowOrderDetail = slot<(orderId: Int) -> Unit>()
        val orderId = 10
        val basketProducts = BasketFixture.createBasket()
            .products
            .filter { it.checked }

        every {
            orderRepository.addOrder(
                basketIds = basketProducts.map { it.id },
                usingPoint = 1000,
                totalPrice = basketProducts.sumOf { it.product.price.value },
                onAdded = capture(slotShowOrderDetail)
            )
        } answers {
            slotShowOrderDetail.captured.invoke(orderId)
        }

        // when: 주문을 추가한다
        presenter.addOrder(1000)

        // then: 저장소에 주문이 추가되면 주문 상세 화면으로 이동한다
        verify { view.navigateToOrderDetail(orderId) }
    }
}
//
//    fun
//
// }
// //
// //    @Before
// //    fun initBasketPresenter() {
// //        view = mockk(relaxed = true)
// //        basketRepository = mockk(relaxed = true)
// //        presenter = BasketPresenter(view, basketRepository)
// //    }
// //
// //    @Test
// //    fun `전체 선택 토글 버튼을 눌러 전체 체크가 활성화되면 그에 반영하여 체크 값과 총가격,선택개수 가 업데이트된다(true 로 설정)`() {
// //        // given
// //        every { view.updateTotalPrice(any()) } just Runs
// //        every { view.updateCheckedProductsCount(any()) } just Runs
// //        every { view.updateBasketProducts(any()) } just Runs
// //
// //        // when
// //        presenter.fetchTotalCheckToCurrentPage(true)
// //
// //        // then
// //        verify(exactly = 1) { view.updateTotalPrice(any()) }
// //        verify(exactly = 1) { view.updateCheckedProductsCount(any()) }
// //        verify(exactly = 1) { view.updateBasketProducts(any()) }
// //    }
// //
// //    @Test
// //    fun `전체 선택 토글 버튼을 눌러 전체 체크가 활성화되면 그에 반영하여 체크 값과 총가격,선택개수 가 업데이트된다(false 로 설정)`() {
// //        // given
// //        every { view.updateTotalPrice(any()) } just Runs
// //        every { view.updateCheckedProductsCount(any()) } just Runs
// //        every { view.updateBasketProducts(any()) } just Runs
// //
// //        // when
// //        presenter.fetchTotalCheckToCurrentPage(false)
// //
// //        // then
// //        verify(exactly = 1) { view.updateTotalPrice(any()) }
// //        verify(exactly = 1) { view.updateCheckedProductsCount(any()) }
// //        verify(exactly = 1) { view.updateBasketProducts(any()) }
// //    }
// //
// //    @Test
// //    fun `장바구니 항목중 checkState를 업데이트 한다면 그에 반영하여 화면의 체크 상태,총가격,선택개수 가 업데이트 된다`() {
// //        // given
// //        every { view.updateTotalPrice(any()) } just Runs
// //        every { view.updateCheckedProductsCount(any()) } just Runs
// //        every { view.updateTotalCheckBox(any()) } just Runs
// //
// //        val basket = Basket(
// //            List(3) {
// //                BasketProduct(
// //                    id = it,
// //                    count = Count(3),
// //                    product = Product(it, "더미입니다만", Price(1000), "url")
// //                )
// //            }
// //        )
// //        presenter =
// //            BasketPresenter(view = view, basketRepository = basketRepository, basket = basket)
// //
// //        // when
// //        val updateProduct = BasketProduct(
// //            id = 1,
// //            count = Count(3),
// //            product = Product(1, "더미입니다만", Price(1000), "url"),
// //            checked = true
// //        )
// //        presenter.updateBasketProductCheckState(updateProduct)
// //
// //        // then
// //        verify(exactly = 1) { view.updateTotalPrice(any()) }
// //        verify(exactly = 1) { view.updateCheckedProductsCount(any()) }
// //        verify(exactly = 1) { view.updateTotalCheckBox(any()) }
// //    }
// //
// //    @Test
// //    fun `장바구니에 물품을 추가하면(물품 단일 추가버튼을 누르면) 화면(장바구니 물품 counter갯수,paging navigation 상태)정보와 장바구니 db 값 업데이트가 일어난다`() {
// //        // given
// //        every { basketRepository.add(any()) } just Runs
// //        every { view.updateNavigatorEnabled(any(), any()) } just Runs
// //        every { view.updateBasketProducts(any()) } just Runs
// //
// //        // when
// //        presenter.plusBasketProductCount(Product(1, "더미입니다만", Price(1000), "url"))
// //
// //        // then
// //        verify(exactly = 1) { basketRepository.add(any()) }
// //        verify(exactly = 1) { view.updateNavigatorEnabled(any(), any()) }
// //        verify(exactly = 1) { view.updateBasketProducts(any()) }
// //    }
// //
// //    @Test
// //    fun `장바구니에 물품을 제거하면(물품 단일 추가버튼을 누르면) 화면(장바구니 물품 counter갯수,paging navigation 상태)정보와 장바구니 db 값 업데이트가 일어난다`() {
// //        // given
// //        every { basketRepository.minus(any()) } just Runs
// //        every { view.updateNavigatorEnabled(any(), any()) } just Runs
// //        every { view.updateBasketProducts(any()) } just Runs
// //
// //        val basket = Basket(
// //            List(3) {
// //                BasketProduct(
// //                    id = it,
// //                    count = Count(3),
// //                    product = Product(it, "더미입니다만", Price(1000), "url")
// //                )
// //            }
// //        )
// //        presenter =
// //            BasketPresenter(view = view, basketRepository = basketRepository, basket = basket)
// //
// //        // when
// //        presenter.minusBasketProductCount(Product(1, "더미입니다만", Price(1000), "url"))
// //
// //        // then
// //        verify(exactly = 1) { basketRepository.minus(any()) }
// //        verify(exactly = 1) { view.updateNavigatorEnabled(any(), any()) }
// //        verify(exactly = 1) { view.updateBasketProducts(any()) }
// //    }
// //
// //    @Test
// //    fun `프리젠터의 현재상태(페이징 네비게이터 상태,현재화면 장바구니아이템 데이터,선택된 총가격,체크된 항목 갯수)를 화면에 업데이트 한다`() {
// //        // given
// //        every { view.updateNavigatorEnabled(any(), any()) } just Runs
// //        every { view.updateBasketProducts(any()) } just Runs
// //        every { view.updateTotalPrice(any()) } just Runs
// //        every { view.updateCheckedProductsCount(any()) } just Runs
// //        // when
// //        presenter.updateBasketProducts()
// //        // then
// //        verify(exactly = 1) { view.updateNavigatorEnabled(any(), any()) }
// //        verify(exactly = 1) { view.updateBasketProducts(any()) }
// //        verify(exactly = 1) { view.updateTotalPrice(any()) }
// //        verify(exactly = 1) { view.updateCheckedProductsCount(any()) }
// //    }
// //
// //    @Test
// //    fun `이전화면 버튼을 누르면 startId가 변경되며 그에 대응하는 현재화면 BasketProduct data와 페이징 네비게이션 상태 데이터가 업데이트된다`() {
// //        // given
// //        every { view.updateCurrentPage(any()) } just Runs
// //        every { view.updateNavigatorEnabled(any(), any()) } just Runs
// //        every { view.updateBasketProducts(any()) } just Runs
// //        // when
// //        presenter.updatePreviousPage()
// //        // then
// //        verify(exactly = 1) { view.updateCurrentPage(any()) }
// //        verify(exactly = 1) { view.updateNavigatorEnabled(any(), any()) }
// //        verify(exactly = 1) { view.updateBasketProducts(any()) }
// //    }
// //
// //    @Test
// //    fun `다음화면 버튼을 누르면 startId가 변경되며 그에 대응하는 현재화면 BasketProduct data와 페이징 네비게이션 상태 데이터가 업데이트된다`() {
// //        // given
// //        every { view.updateCurrentPage(any()) } just Runs
// //        every { view.updateNavigatorEnabled(any(), any()) } just Runs
// //        every { view.updateBasketProducts(any()) } just Runs
// //        // when
// //        presenter.updateNextPage()
// //        // then
// //        verify(exactly = 1) { view.updateCurrentPage(any()) }
// //        verify(exactly = 1) { view.updateNavigatorEnabled(any(), any()) }
// //        verify(exactly = 1) { view.updateBasketProducts(any()) }
// //    }
// //
// //    @Test
// //    fun `장바구니의 아이템을 삭제하면 관련 화면 데이터(현재화면 BasketProduct data와 페이징 네비게이션 상태 데이터)가 업데이트되며 db에서도 아이템을 삭제한다 `() {
// //        // given
// //        every { basketRepository.remove(any()) } just Runs
// //        every { view.updateNavigatorEnabled(any(), any()) } just Runs
// //        every { view.updateBasketProducts(any()) } just Runs
// //        // when
// //        val basket = Basket(
// //            List(3) {
// //                BasketProduct(
// //                    id = it,
// //                    count = Count(3),
// //                    product = Product(it, "더미입니다만", Price(1000), "url")
// //                )
// //            }
// //        )
// //        val deleteItem = BasketProduct(
// //            id = 1,
// //            count = Count(3),
// //            product = Product(1, "더미입니다만", Price(1000), "url")
// //        )
// //        presenter =
// //            BasketPresenter(view = view, basketRepository = basketRepository, basket = basket)
// //
// //        presenter.deleteBasketProduct(deleteItem.toUi())
// //
// //        // then
// //        verify(exactly = 1) { basketRepository.remove(any()) }
// //        verify(exactly = 1) { view.updateNavigatorEnabled(any(), any()) }
// //        verify(exactly = 1) { view.updateBasketProducts(any()) }
// //    }
// //}
