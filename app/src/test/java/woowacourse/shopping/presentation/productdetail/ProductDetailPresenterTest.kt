package woowacourse.shopping.presentation.productdetail

import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.Price
import woowacourse.shopping.Product
import woowacourse.shopping.presentation.mapper.toDomain
import woowacourse.shopping.presentation.mapper.toPresentation
import woowacourse.shopping.presentation.model.CartProductInfoModel
import woowacourse.shopping.presentation.model.ProductModel
import woowacourse.shopping.repository.CartRepository
import woowacourse.shopping.repository.RecentProductRepository

class ProductDetailPresenterTest {
    private lateinit var view: ProductDetailContract.View
    private lateinit var presenter: ProductDetailContract.Presenter
    private lateinit var cartRepository: CartRepository
    private lateinit var recentProductRepository: RecentProductRepository
    private val initProductModel = ProductModel(1, "", "wooseok", 1000)

    @Before
    fun setUp() {
        view = mockk(relaxed = true)
        cartRepository = mockk(relaxed = true)
        recentProductRepository = mockk(relaxed = true)
        presenter = ProductDetailPresenter(
            view = view,
            recentProductRepository = recentProductRepository,
            cartRepository = cartRepository,
            productModel = initProductModel
        )
    }

    @Test
    fun 현재상품이_최근본_상품과_동일하다면_최근본_상품을_보여주지_않는다() {
        // given
        val slot = slot<(Product?) -> Unit>()
        every { recentProductRepository.getMostRecentProduct(onSuccess = capture(slot)) } answers {
            slot.captured.invoke(initProductModel.toDomain())
        }
        // when
        presenter.checkCurrentProductIsMostRecent()
        // then
        verify { view.setMostRecentProductVisible(false, initProductModel) }
    }

    @Test
    fun 현재상품이_최근본_상품과_동일하지_않다면_최근본_상품을_보여준다() {
        // given
        val mostRecentProduct = Product(2, "", "", Price(1000))
        val slot = slot<(Product?) -> Unit>()
        every { recentProductRepository.getMostRecentProduct(onSuccess = capture(slot)) } answers {
            slot.captured.invoke(mostRecentProduct)
        }
        // when
        presenter.checkCurrentProductIsMostRecent()
        // then
        verify { view.setMostRecentProductVisible(true, mostRecentProduct.toPresentation()) }
    }

    @Test
    fun `최근 본 상품을 누르면 최근 본 상품의 상세정보로 넘어간다`() {
        // given
        val mostRecentProduct = Product(2, "", "", Price(1000))
        val slot = slot<(Product?) -> Unit>()
        every { recentProductRepository.getMostRecentProduct(onSuccess = capture(slot)) } answers {
            slot.captured.invoke(mostRecentProduct)
        }
        // when
        presenter.checkCurrentProductIsMostRecent()
        presenter.showMostRecentProductDetail()
        // then
        verify { view.navigateToMostRecent(mostRecentProduct.toPresentation()) }
    }

    @Test
    fun 현재_상품을_최근본_상품목록에_저장한다() {
        // when
        presenter.saveRecentProduct()
        // then
        verify { recentProductRepository.deleteRecentProductId(1) }
        verify { recentProductRepository.addRecentProductId(1) }
    }

    @Test
    fun `장바구니 담기를 누르면 수량 선택 화면을 보여준다`() {
        // when
        presenter.showProductCart()
        // then
        val expected = CartProductInfoModel(0, initProductModel, 1, totalPrice = 1000)
        view.showProductCart(expected)
    }

    @Test
    fun `수량을 바꿀 때마다, 가격을 최신화 한다`() {
        // when
        presenter.updateTotalPrice(3)
        // then
        verify { view.setTotalPrice(3000) }
    }

    @Test
    fun `담기를 누르면 장바구니 수량을 업데이트 하고,완료메세지를 띄운다`() {
        // given
        val slotUpdateQuantity = slot<() -> Unit>()
        every {
            cartRepository.updateCartItemQuantityByProduct(
                product = initProductModel.toDomain(),
                count = 3,
                onSuccess = capture(slotUpdateQuantity)
            )
        } answers {
            slotUpdateQuantity.captured.invoke()
        }
        // when
        presenter.saveProductInRepository(3)
        // then
        verify {
            cartRepository.updateCartItemQuantityByProduct(
                initProductModel.toDomain(),
                3,
                slotUpdateQuantity.captured
            )
        }
        verify { view.showCompleteMessage(initProductModel.name) }
    }
}
