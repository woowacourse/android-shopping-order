package woowacourse.shopping.feature.goods

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.repository.GoodsRepository
import woowacourse.shopping.domain.model.Authorization
import woowacourse.shopping.domain.model.CartItem
import woowacourse.shopping.domain.model.Goods
import woowacourse.shopping.util.InstantTaskExecutorExtension

@ExtendWith(InstantTaskExecutorExtension::class)
class GoodsViewModelTest {
    @MockK
    private lateinit var cartRepository: CartRepository

    @MockK
    private lateinit var goodsRepository: GoodsRepository

    private lateinit var viewModel: GoodsViewModel

    private val sampleGoods1 = Goods("상품1", 10000, "url1", 1, "카테고리1")
    private val sampleGoods2 = Goods("상품2", 20000, "url2", 2, "카테고리2")
    private val sampleCartItem1 = CartItem(sampleGoods1, 1, 1, true)
    private val sampleCartItem2 = CartItem(sampleGoods2, 2, 2, true)

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        viewModel = GoodsViewModel(cartRepository, goodsRepository)
        setupRepositoryMocks()
        setupLiveDataObservers()
    }

    private fun setupLiveDataObservers() {
        viewModel.goodsWithCartQuantity.observeForever { }
        viewModel.isFullLoaded.observeForever { }
        viewModel.totalCartItemSize.observeForever { }
        viewModel.recentlyViewedGoods.observeForever { }
    }

    private fun setupRepositoryMocks() {
        every { goodsRepository.fetchPageGoods(any(), any(), any(), any()) } answers { }
        every { goodsRepository.fetchRecentGoods(any()) } answers {
            firstArg<(List<Goods>) -> Unit>()(emptyList())
        }
        every { cartRepository.fetchAllCartItems(any(), any()) } answers { }
    }

    @Test
    fun `초기 상태에서 모든 LiveData가 기본값으로 설정된다`() {
        assertThat(viewModel.goodsWithCartQuantity.value).isEmpty()
        assertThat(viewModel.isFullLoaded.value).isFalse()
        assertThat(viewModel.totalCartItemSize.value).isEqualTo("0")
        assertThat(viewModel.recentlyViewedGoods.value).isEmpty()
    }

    @Test
    fun `상품 설정 시 CartItem으로 변환되어 표시된다`() {
        viewModel.setTestGoods(listOf(sampleGoods1, sampleGoods2))

        assertThat(viewModel.goodsWithCartQuantity.value).hasSize(2)
        assertThat(
            viewModel.goodsWithCartQuantity.value
                ?.first()
                ?.goods,
        ).isEqualTo(sampleGoods1)
        assertThat(
            viewModel.goodsWithCartQuantity.value
                ?.first()
                ?.quantity,
        ).isEqualTo(0)
    }

    @Test
    fun `장바구니 캐시 설정 시 상품과 수량이 매핑된다`() {
        viewModel.setTestGoods(listOf(sampleGoods1, sampleGoods2))
        viewModel.setTestCartCache(listOf(sampleCartItem1))

        assertThat(viewModel.goodsWithCartQuantity.value).hasSize(2)
        assertThat(
            viewModel.goodsWithCartQuantity.value
                ?.first()
                ?.quantity,
        ).isEqualTo(1)
        assertThat(
            viewModel.goodsWithCartQuantity.value
                ?.last()
                ?.quantity,
        ).isEqualTo(0)
    }

    @Test
    fun `장바구니 캐시 설정 시 장바구니 총 수량이 올바르게 계산된다`() {
        viewModel.setTestCartCache(listOf(sampleCartItem1, sampleCartItem2))

        assertThat(viewModel.totalCartItemSize.value).isEqualTo("3")
    }

    @Test
    fun `장바구니 수량이 100개 이상일 때 99+로 표시된다`() {
        val cartItems = listOf(CartItem(sampleGoods1, 100, 1, true))
        viewModel.setTestCartCache(cartItems)

        assertThat(viewModel.totalCartItemSize.value).isEqualTo("99+")
    }

    @Test
    fun `findCart 호출 시 캐시된 장바구니에서 올바른 ID를 반환한다`() {
        viewModel.setTestCartCache(listOf(sampleCartItem1))

        val cartId = viewModel.findCart(sampleGoods1)

        assertThat(cartId).isEqualTo(1)
    }

    @Test
    fun `최근 본 상품과 장바구니 캐시가 설정되었을 때 mostRecentlyViewedCartId가 올바른 ID를 반환한다`() {
        viewModel.setTestRecentlyViewedGoods(listOf(sampleGoods1))
        viewModel.setTestCartCache(listOf(sampleCartItem1))

        val cartId = viewModel.mostRecentlyViewedCartId()

        assertThat(cartId).isEqualTo(1)
    }

    @Test
    fun `로그인 상태에서 장바구니 클릭 시 navigateToCart 이벤트가 발생한다`() {
        Authorization.setLoginStatus(true)

        viewModel.onCartClicked()

        assertThat(viewModel.navigateToCart.getValue()).isNotNull
    }

    @Test
    fun `비로그인 상태에서 장바구니 클릭 시 navigateToLogin 이벤트가 발생한다`() {
        Authorization.setLoginStatus(false)

        viewModel.onCartClicked()

        assertThat(viewModel.navigateToLogin.getValue()).isNotNull
    }

    @Test
    fun `비로그인 상태에서 아이템 추가 시 navigateToLogin 이벤트가 발생한다`() {
        Authorization.setLoginStatus(false)

        viewModel.addCartItemOrIncreaseQuantity(sampleCartItem1)

        assertThat(viewModel.navigateToLogin.getValue()).isNotNull
    }

    @Test
    fun `로그인 성공 시 Authorization 상태가 true로 설정된다`() {
        every { cartRepository.checkValidLocalSavedBasicKey(any(), any()) } answers {
            firstArg<(Int) -> Unit>()(200)
        }

        viewModel.login()

        assertThat(Authorization.isLogin).isTrue()
        verify { cartRepository.checkValidLocalSavedBasicKey(any(), any()) }
    }

    @Test
    fun `로그인 실패 시 Authorization 상태가 false로 설정된다`() {
        every { cartRepository.checkValidLocalSavedBasicKey(any(), any()) } answers {
            firstArg<(Int) -> Unit>()(401)
        }

        viewModel.login()

        assertThat(Authorization.isLogin).isFalse()
    }

    @Test
    fun `isFullLoaded 상태 설정이 올바르게 동작한다`() {
        viewModel.setTestIsFullLoaded(true)

        assertThat(viewModel.isFullLoaded.value).isTrue()
    }

    @Test
    fun `최근 본 상품 설정이 올바르게 동작한다`() {
        val recentGoods = listOf(sampleGoods1, sampleGoods2)
        viewModel.setTestRecentlyViewedGoods(recentGoods)

        assertThat(viewModel.recentlyViewedGoods.value).isEqualTo(recentGoods)
    }

    @Test
    fun `로그인 상태에서 새 아이템 추가 시 repository addCartItem이 호출된다`() {
        Authorization.setLoginStatus(true)
        viewModel.setTestCartCache(emptyList())
        every { cartRepository.addCartItem(any(), any(), any(), any()) } answers {
            thirdArg<(Int, Int) -> Unit>()(200, 1)
        }

        viewModel.addCartItemOrIncreaseQuantity(sampleCartItem1)

        verify { cartRepository.addCartItem(sampleGoods1, 1, any(), any()) }
    }

    @Test
    fun `로그인 상태에서 기존 아이템 수량 증가 시 repository updateQuantity가 호출된다`() {
        Authorization.setLoginStatus(true)
        viewModel.setTestCartCache(listOf(sampleCartItem1))
        every { cartRepository.updateQuantity(any(), any(), any(), any()) } answers {
            thirdArg<() -> Unit>()()
        }

        viewModel.addCartItemOrIncreaseQuantity(sampleCartItem1)

        verify { cartRepository.updateQuantity(eq(1), any(), any(), any()) }
    }

    @Test
    fun `updateRecentlyViewedGoods 호출 시 repository fetchRecentGoods가 호출된다`() {
        val recentGoods = listOf(sampleGoods1, sampleGoods2)
        every { goodsRepository.fetchRecentGoods(any()) } answers {
            firstArg<(List<Goods>) -> Unit>()(recentGoods)
        }

        viewModel.updateRecentlyViewedGoods()

        verify { goodsRepository.fetchRecentGoods(any()) }
        assertThat(viewModel.recentlyViewedGoods.value).isEqualTo(recentGoods)
    }

    @Test
    fun `fetchAndSetCartCache repository 호출 검증`() {
        viewModel.fetchAndSetCartCache()

        verify { cartRepository.fetchAllCartItems(any(), any()) }
    }
}
