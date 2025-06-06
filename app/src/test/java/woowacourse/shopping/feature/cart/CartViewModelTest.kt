package woowacourse.shopping.feature.cart

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.slot
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.repository.GoodsRepository
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Goods
import woowacourse.shopping.util.InstantTaskExecutorExtension

@ExtendWith(InstantTaskExecutorExtension::class)
class CartViewModelTest {
    @MockK
    private lateinit var cartRepository: CartRepository

    @MockK
    private lateinit var goodsRepository: GoodsRepository

    private lateinit var viewModel: CartViewModel

    private val sampleGoods1 = Goods("상품1", 10000, "url1", 1, "카테고리1")
    private val sampleGoods2 = Goods("상품2", 20000, "url2", 2, "카테고리2")
    private val sampleCartItem1 = CartItem(sampleGoods1, 2, 1, true)
    private val sampleCartItem2 = CartItem(sampleGoods2, 1, 2, false)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        every { cartRepository.fetchAllCartItems(any(), any()) } answers { }
        viewModel = CartViewModel(cartRepository, goodsRepository)
        setupLiveDataObservers()
    }

    private fun setupLiveDataObservers() {
        viewModel.cartsList.observeForever { }
        viewModel.isMultiplePages.observeForever { }
        viewModel.isRightPageEnable.observeForever { }
        viewModel.isLeftPageEnable.observeForever { }
        viewModel.totalPrice.observeForever { }
        viewModel.selectedItemCount.observeForever { }
        viewModel.isAllSelected.observeForever { }
        viewModel.currentPageCarts.observeForever { }
    }

    @Test
    fun `초기 페이지는 1이어야 한다`() {
        assertThat(viewModel.page.value).isEqualTo(1)
    }

    @Test
    fun `plusPage 호출 시 페이지가 증가한다`() {
        viewModel.plusPage()

        assertThat(viewModel.page.value).isEqualTo(2)
    }

    @Test
    fun `minusPage 호출 시 페이지가 감소한다`() {
        viewModel.setTestPage(2)

        viewModel.minusPage()

        assertThat(viewModel.page.value).isEqualTo(1)
    }

    @Test
    fun `6개 아이템이 있을 때 다중 페이지가 활성화된다`() {
        val items =
            (1..6).map {
                CartItem(Goods("상품$it", 1000, "url$it", it, "카테고리"), 1, it, true)
            }

        viewModel.setTestCarts(items)

        assertThat(viewModel.isMultiplePages.value).isTrue()
        assertThat(viewModel.isRightPageEnable.value).isTrue()
    }

    @Test
    fun `페이지가 2일 때 왼쪽 페이지 버튼이 활성화된다`() {
        val items =
            (1..6).map {
                CartItem(Goods("상품$it", 1000, "url$it", it, "카테고리"), 1, it, true)
            }
        viewModel.setTestCarts(items)

        viewModel.setTestPage(2)

        assertThat(viewModel.isLeftPageEnable.value).isTrue()
    }

    @Test
    fun `아이템 체크 상태가 변경된다`() {
        viewModel.setTestCarts(listOf(sampleCartItem1))

        viewModel.toggleCartItemCheck(sampleCartItem1)

        assertThat(
            viewModel.cartsList.value
                ?.first()
                ?.isSelected,
        ).isFalse()
    }

    @Test
    fun `선택된 아이템들의 총 가격이 계산된다`() {
        val selectedItems =
            listOf(
                CartItem(sampleGoods1, 2, 1, true),
                CartItem(sampleGoods2, 1, 2, true),
            )

        viewModel.setTestCarts(selectedItems)

        assertThat(viewModel.totalPrice.value).isEqualTo(40000)
    }

    @Test
    fun `선택된 아이템들의 총 수량이 계산된다`() {
        val selectedItems =
            listOf(
                CartItem(sampleGoods1, 2, 1, true),
                CartItem(sampleGoods2, 1, 2, true),
            )

        viewModel.setTestCarts(selectedItems)

        assertThat(viewModel.selectedItemCount.value).isEqualTo(3)
    }

    @Test
    fun `모든 아이템이 선택되었을 때 isAllSelected가 true가 된다`() {
        val allSelectedItems =
            listOf(
                CartItem(sampleGoods1, 2, 1, true),
                CartItem(sampleGoods2, 1, 2, true),
            )

        viewModel.setTestCarts(allSelectedItems)

        assertThat(viewModel.isAllSelected.value).isTrue()
    }

    @Test
    fun `첫 번째 페이지에서는 5개 아이템이 표시된다`() {
        val items =
            (1..7).map {
                CartItem(Goods("상품$it", 1000, "url$it", it, "카테고리"), 1, it, true)
            }

        viewModel.setTestCarts(items)

        assertThat(viewModel.currentPageCarts.value?.size).isEqualTo(5)
    }

    @Test
    fun `두 번째 페이지에서는 나머지 아이템들이 표시된다`() {
        val items =
            (1..7).map {
                CartItem(Goods("상품$it", 1000, "url$it", it, "카테고리"), 1, it, true)
            }
        viewModel.setTestCarts(items)

        viewModel.setTestPage(2)

        assertThat(viewModel.currentPageCarts.value?.size).isEqualTo(2)
    }

    @Test
    fun `selectAllItems 호출 시 모든 아이템이 선택된다`() {
        val unselectedItems =
            listOf(
                CartItem(sampleGoods1, 2, 1, false),
                CartItem(sampleGoods2, 1, 2, false),
            )
        viewModel.setTestCarts(unselectedItems)

        viewModel.selectAllItems()

        assertThat(viewModel.isAllSelected.value).isTrue()
    }

    @Test
    fun `increaseQuantity 호출 시 repository updateQuantity가 호출된다`() {
        val cartQuantitySlot = slot<CartQuantity>()
        every { cartRepository.updateQuantity(any(), capture(cartQuantitySlot), any(), any()) } answers {
            thirdArg<() -> Unit>()()
        }
        viewModel.setTestCarts(listOf(sampleCartItem1))

        viewModel.increaseCartItemQuantity(sampleCartItem1)

        verify { cartRepository.updateQuantity(eq(1), any(), any(), any()) }
    }

    @Test
    fun `delete 호출 시 repository delete가 호출된다`() {
        every { cartRepository.delete(any(), any(), any()) } answers {
            secondArg<(Int) -> Unit>()(200)
        }
        viewModel.setTestCarts(listOf(sampleCartItem1))

        viewModel.delete(sampleCartItem1)

        verify { cartRepository.delete(eq(1), any(), any()) }
    }

    @Test
    fun `addCartItemOrIncreaseQuantityFromRecommend 호출 시 repository addCartItem이 호출된다`() {
        val goodsSlot = slot<Goods>()
        every { cartRepository.addCartItem(capture(goodsSlot), any(), any(), any()) } answers {}

        viewModel.addCartItemOrIncreaseQuantityFromRecommend(sampleCartItem1)

        verify { cartRepository.addCartItem(any(), eq(1), any(), any()) }
        assertThat(goodsSlot.captured).isEqualTo(sampleCartItem1.goods)
    }

    @Test
    fun `getPosition 호출 시 올바른 위치를 반환한다`() {
        viewModel.setTestCarts(listOf(sampleCartItem1, sampleCartItem2))

        assertThat(viewModel.getPosition(sampleCartItem1)).isEqualTo(0)
        assertThat(viewModel.getPosition(sampleCartItem2)).isEqualTo(1)
    }
}
