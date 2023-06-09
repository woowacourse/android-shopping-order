package woowacourse.shopping.ui.order.contract.presenter

internal class OrderPresenterTest {
/*
    private lateinit var view: OrderContract.View
    private lateinit var presenter: OrderContract.Presenter
    private lateinit var cartItems: CartItems
    private lateinit var couponRepository: CouponRepository
    private lateinit var orderRepository: OrderRepository

    private val fakeProduct = Product(
        1L,
        "상품",
        50000,
        "",
    )

    private val fakeCartProduct = CartProduct(
        1,
        1,
        fakeProduct,
    )

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        couponRepository = mockk(relaxed = true)
        orderRepository = mockk(relaxed = true)
        cartItems = CartItems(listOf(fakeCartProduct))
        every { couponRepository.getCoupons() } returns Result.success(listOf())
        presenter = OrderPresenter(
            cartItems.toUIModel(),
            view,
            couponRepository,
            orderRepository,
        )
    }

    @Test
    fun `주문 내역을 가져올 수 있다`() {
        val slot = slot<List<CartProductUIModel>>()
        every { view.setUpOrder(capture(slot)) } answers { nothing }
        presenter.getOrder()

        assertEquals(slot.captured, cartItems.toUIModel().cartProducts)
        verify { view.setUpOrder(slot.captured) }
    }

    @Test
    fun `original 가격을 가져올 수 있다`() {
        val slot = slot<Int>()
        every { view.setPrice(capture(slot)) } answers { nothing }
        presenter.getOriginalPrice()

        assertEquals(slot.captured, cartItems.getPrice())
        verify { view.setPrice(slot.captured) }
    }

    @Test
    fun `전체 쿠폰을 가져올 수 있다`() {
        val slot = slot<List<String>>()
        every { view.setCoupons(capture(slot)) } answers { nothing }
        presenter.getCoupons()

        verify { view.setCoupons(slot.captured) }
    }

    @Test
    fun `총 가격을 보여준다`() {
        every { couponRepository.getCoupons() } returns Result.success(listOf())
        every { couponRepository.getPriceWithCoupon(any(), any()) } returns Result.success(
            CouponDiscountPrice(5000, 40000),
        )

        presenter.getTotalPrice("")

        verify { view.setPrice(50000) }
    }

    @Test
    fun `주문 버튼을 누르면 주문 상세보기 페이지로 이동한다`() {
        every { couponRepository.getCoupons() } returns Result.success(listOf())
        every { orderRepository.insertOrderWithCoupon(any(), any()) } returns Result.success(
            Order(
                1L,
                listOf(),
                "",
                "",
                0,
                0,
                0,
            ),
        )
        every { orderRepository.insertOrderWithoutCoupon(any()) } returns Result.success(
            Order(
                1L,
                listOf(),
                "",
                "",
                0,
                0,
                0,
            ),
        )

        presenter.navigateToOrderDetail()

        verify { view.navigateToOrderDetail(any()) }
    }*/
}
