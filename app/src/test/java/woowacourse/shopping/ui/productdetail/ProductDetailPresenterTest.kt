package woowacourse.shopping.ui.productdetail

import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import woowacourse.shopping.domain.repository.BasketRepository
import woowacourse.shopping.ui.ProductFixture
import woowacourse.shopping.ui.mapper.toUiModel
import woowacourse.shopping.ui.model.ProductUiModel

class ProductDetailPresenterTest {

    private lateinit var view: ProductDetailContract.View
    private lateinit var basketRepository: BasketRepository
    private lateinit var presenter: ProductDetailPresenter

    @Before
    fun initProductDetailPresenter() {
        view = mockk(relaxed = true)
        basketRepository = mockk(relaxed = true)
        presenter = ProductDetailPresenter(
            view = view,
            basketRepository = basketRepository,
            currentProduct = ProductFixture.createProduct().toUiModel(),
            currentProductBasketId = 1,
            previousProduct = ProductFixture.createProduct2().toUiModel(),
            previousProductBasketId = 2
        )
    }

    @Test
    fun `현재 상품 데이터를 화면에 초기화 할수있다`() {
        // given
        val currentProduct = ProductFixture.createProduct().toUiModel()
        val previousProduct = ProductFixture.createProduct2().toUiModel()

        // when
        presenter.initProductData()

        // then
        verify {
            view.updateBindingData(
                product = currentProduct,
                previousProduct = previousProduct
            )
        }
    }

    @Test
    fun `장바구니 항목 선택 다이얼로그의 데이터 및 관련 데이터 수정 리스너들을 전달한다`() {
        // given

        // when
        presenter.setBasketDialog()

        // then
        verify(exactly = 1) { view.showBasketDialog(any(), any(), any(), any()) }
        verify(exactly = 1) { view.updateProductCount(any()) }
    }

    @Test
    fun `최근 본 상품을 클릭한다면 현재 상품이 최근본 상품으로 변경되고 현재 화면의 데이터가 변경된 현재상품(최근본상품)으로 업데이트된다`() {
        // given
        val slotProduct = slot<ProductUiModel>()

        every {
            view.updateBindingData(
                product = capture(slotProduct),
                previousProduct = any()
            )
        } just runs

        // when
        presenter.selectPreviousProduct()

        // then
        val expected = ProductFixture.createProduct2().toUiModel()

        assertEquals(expected, slotProduct.captured)
    }
}
