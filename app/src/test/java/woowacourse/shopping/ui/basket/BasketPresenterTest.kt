import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.data.repository.OrderRepository
import woowacourse.shopping.data.repository.UserRepository
import woowacourse.shopping.domain.BasketProduct
import woowacourse.shopping.domain.Count
import woowacourse.shopping.domain.repository.BasketRepository
import woowacourse.shopping.ui.BasketFixture
import woowacourse.shopping.ui.basket.BasketContract
import woowacourse.shopping.ui.basket.BasketPresenter
import woowacourse.shopping.ui.mapper.toBasketProductUiModel

class BasketPresenterTest {
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
            basketRepository = basketRepository,
        )
        presenter.basket = BasketFixture.createBasket()
    }

    @Test
    fun `전체 선택 토글 버튼을 눌러 전체 체크가 활성화되면 그에 반영하여 체크 값과 총가격,선택개수 가 업데이트된다(true 로 설정)`() {
        // given

        // when
        presenter.fetchTotalCheckToCurrentPage(true)

        // then
        verify { view.updateTotalPrice(any()) }
        verify { view.updateCheckedProductsCount(any()) }
        verify { view.updateBasketProducts(any()) }
    }

    @Test
    fun `장바구니 항목중 checkState를 업데이트 한다면 그에 반영하여 화면의 체크 상태,총가격,선택개수 가 업데이트 된다`() {
        // given
        val basketProduct = presenter.basket
            .products
            .first()
        val updatedBasketProduct = BasketProduct(
            id = basketProduct.id,
            count = basketProduct.count,
            product = basketProduct.product,
            checked = false
        )

        // when
        presenter.updateBasketProductCheckState(updatedBasketProduct)

        // then
        verify { view.updateTotalPrice(any()) }
        verify { view.updateCheckedProductsCount(any()) }
        verify { view.updateTotalCheckBox(any()) }
    }

    @Test
    fun `장바구니 상품의 개수를 증가시키면 화면(장바구니 물품, paging navigation 상태)정보와 장바구니 db 값 업데이트가 일어난다`() {
        // given
        val basketProduct = presenter.basket
            .products
            .first()

        // when: 장바구니에 존재하는 상품의 개수를 증가시킨다.
        presenter.plusBasketProductCount(basketProduct.product)

        // then: 증가된 상품이 반영된 장바구니를 통해서 화면을 갱신한다
        val expected = BasketFixture.createBasket()
            .plus(
                BasketProduct(
                    count = Count(1),
                    product = basketProduct.product
                )
            )
            .products
            .map { it.toBasketProductUiModel() }

        verify { basketRepository.update(basketProduct.plusCount()) }
        verify { view.updateBasketProducts(expected) }
    }

    @Test
    fun `장바구니 상품의 개수를 감소시키면 화면(장바구니 물품, paging navigation 상태)정보와 장바구니 db 값 업데이트가 일어난다`() {
        // given
        val basketProduct = presenter.basket
            .products
            .first()

        // when: 장바구니에 존재하는 상품의 개수를 감소시킨다.
        presenter.minusBasketProductCount(basketProduct.product)

        // then: 증가된 상품이 반영된 장바구니를 통해서 화면을 갱신한다.
        val expected = BasketFixture.createBasket()
            .minus(
                BasketProduct(
                    count = Count(1),
                    product = basketProduct.product
                )
            )
            .products
            .map { it.toBasketProductUiModel() }

        verify { basketRepository.update(basketProduct.minusCount()) }
        verify { view.updateBasketProducts(expected) }
    }

    @Test
    fun `프리젠터의 현재상태(페이징 네비게이터 상태,현재화면 장바구니아이템 데이터,선택된 총가격,체크된 항목 갯수)를 화면에 업데이트 한다`() {
        // given

        // when
        presenter.updateBasketProducts()

        // then
        verify { view.updateNavigatorEnabled(any(), any()) }
        verify { view.updateBasketProducts(any()) }
        verify { view.updateTotalPrice(any()) }
        verify { view.updateCheckedProductsCount(any()) }
    }

    @Test
    fun `이전화면 버튼을 누르면 startId가 변경되며 그에 대응하는 현재화면 BasketProduct data와 페이징 네비게이션 상태 데이터가 업데이트된다`() {
        // given

        // when: 이전 화면 버튼을 누른다.
        presenter.updatePreviousPage()

        // then: 그에 대응하는 현재화면 BasketProduct data와 페이징 네비게이션 상태 데이터가 업데이트된다
        verify { view.updateCurrentPage(any()) }
        verify { view.updateNavigatorEnabled(any(), any()) }
        verify { view.updateBasketProducts(any()) }
    }

    @Test
    fun `다음화면 버튼을 누르면 startId가 변경되며 그에 대응하는 현재화면 BasketProduct data와 페이징 네비게이션 상태 데이터가 업데이트된다`() {
        // given

        // when: 다음 화면 버튼을 누른다.
        presenter.updateNextPage()

        // then: 그에 대응하는 현재화면 BasketProduct data와 페이징 네비게이션 상태 데이터가 업데이트된다
        verify { view.updateCurrentPage(any()) }
        verify { view.updateNavigatorEnabled(any(), any()) }
        verify { view.updateBasketProducts(any()) }
    }

    @Test
    fun `결제를 시작하면서 선택된 상품을 뷰에게 전달한다`() {
        // given
        val checkedProducts = BasketFixture.createBasket()
            .products
            .filter { it.checked }
            .map { it.toBasketProductUiModel() }

        // when: 포인트를 사용한다
        presenter.startPayment()

        // then: repository로부터 user정보를 받아 dialog를 띄운다.
        verify { view.showPaymentView(checkedProducts) }
    }

    @Test
    fun `장바구니의 아이템을 삭제하면 관련 화면 데이터(현재화면 BasketProduct data와 페이징 네비게이션 상태 데이터)가 업데이트되며 db에서도 아이템을 삭제한다 `() {
        // given
        val basketProduct = presenter.basket
            .products
            .first()

        // when
        presenter.deleteBasketProduct(basketProduct.toBasketProductUiModel())

        // then
        val expected = presenter.basket
            .remove(basketProduct)
            .products
            .map { it.toBasketProductUiModel() }

        verify { basketRepository.remove(basketProduct) }
        verify { view.updateNavigatorEnabled(any(), any()) }
        verify { view.updateBasketProducts(expected) }
    }
}
