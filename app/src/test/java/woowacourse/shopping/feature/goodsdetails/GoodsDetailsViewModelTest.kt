package woowacourse.shopping.feature.goodsdetails

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.R
import woowacourse.shopping.data.carts.CartUpdateError
import woowacourse.shopping.data.carts.dto.CartQuantity
import woowacourse.shopping.data.carts.repository.CartRepository
import woowacourse.shopping.data.goods.repository.GoodsRepository
import woowacourse.shopping.domain.model.Goods
import woowacourse.shopping.feature.GoodsUiModel
import woowacourse.shopping.util.InstantTaskExecutorExtension

@ExtendWith(InstantTaskExecutorExtension::class)
class GoodsDetailsViewModelTest {
    @MockK
    private lateinit var cartRepository: CartRepository

    @MockK
    private lateinit var goodsRepository: GoodsRepository

    private lateinit var viewModel: GoodsDetailsViewModel

    private val sampleGoodsUiModel =
        GoodsUiModel(
            name = "상품1",
            price = 10000,
            thumbnailUrl = "url1",
            id = 1,
        )

    private val sampleGoods = Goods("상품1", 10000, "url1", 1, "카테고리1")
    private val recentlyViewedGoods = Goods("최근상품", 5000, "url2", 2, "카테고리2")
    private val initialCartId = 100

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        setupRepositoryMocks()
        viewModel =
            GoodsDetailsViewModel(
                goodsUiModel = sampleGoodsUiModel,
                cartRepository = cartRepository,
                goodsRepository = goodsRepository,
                cartId = initialCartId,
            )
        setupLiveDataObservers()
    }

    private fun setupLiveDataObservers() {
        viewModel.cartItem.observeForever { }
        viewModel.mostRecentlyViewedGoods.observeForever { }
    }

    private fun setupRepositoryMocks() {
        every { goodsRepository.fetchMostRecentGoods(any()) } answers {
            firstArg<(Goods?) -> Unit>()(null)
        }
        every { goodsRepository.loggingRecentGoods(any(), any()) } answers {
            secondArg<() -> Unit>()()
        }
        every { cartRepository.updateQuantity(any(), any(), any(), any()) } answers {
            thirdArg<() -> Unit>()()
        }
        every { cartRepository.addCartItem(any(), any(), any(), any()) } answers {
            thirdArg<(Int, Int) -> Unit>()(200, 1)
        }
    }

    @Test
    fun `초기 상태에서 CartItem이 올바르게 설정된다`() {
        assertThat(
            viewModel.cartItem.value
                ?.goods
                ?.id,
        ).isEqualTo(1)
        assertThat(
            viewModel.cartItem.value
                ?.goods
                ?.name,
        ).isEqualTo("상품1")
        assertThat(viewModel.cartItem.value?.quantity).isEqualTo(1)
    }

    @Test
    fun `초기 상태에서 최근 본 상품이 null로 설정된다`() {
        assertThat(viewModel.mostRecentlyViewedGoods.value).isNull()
    }

    @Test
    fun `increaseSelectorQuantity 호출 시 수량이 증가한다`() {
        viewModel.increaseSelectorQuantity()

        assertThat(viewModel.cartItem.value?.quantity).isEqualTo(2)
    }

    @Test
    fun `increaseSelectorQuantity 여러 번 호출 시 수량이 계속 증가한다`() {
        viewModel.increaseSelectorQuantity()
        viewModel.increaseSelectorQuantity()

        assertThat(viewModel.cartItem.value?.quantity).isEqualTo(3)
    }

    @Test
    fun `decreaseSelectorQuantity 호출 시 수량이 감소한다`() {
        viewModel.increaseSelectorQuantity()
        viewModel.increaseSelectorQuantity() // 수량을 3

        viewModel.decreaseSelectorQuantity()

        assertThat(viewModel.cartItem.value?.quantity).isEqualTo(2)
    }

    @Test
    fun `수량이 1일 때 decreaseSelectorQuantity 호출 시 수량이 변경되지 않는다`() {
        viewModel.decreaseSelectorQuantity()

        assertThat(viewModel.cartItem.value?.quantity).isEqualTo(1)
    }

    @Test
    fun `addOrIncreaseToCart 호출 시 repository updateQuantity가 호출된다`() {
        viewModel.increaseSelectorQuantity() // 수량 2

        viewModel.addOrUpdateQuantityToCart()

        verify {
            cartRepository.updateQuantity(
                eq(initialCartId),
                any<CartQuantity>(),
                any(),
                any(),
            )
        }
    }

    @Test
    fun `updateQuantity 성공 시 완료 알림 이벤트가 발생한다`() {
        every { cartRepository.updateQuantity(any(), any(), any(), any()) } answers {
            thirdArg<() -> Unit>()()
        }
        viewModel.increaseSelectorQuantity() // 수량 2

        viewModel.addOrUpdateQuantityToCart()

        assertThat(viewModel.alertEvent.getValue()?.resourceId ?: -99).isEqualTo(R.string.goods_detail_cart_update_complete_toast_message)
    }

    @Test
    fun `updateQuantity NotFound 에러 시 addCartItem이 호출된다`() {
        every { cartRepository.updateQuantity(any(), any(), any(), any()) } answers {
            val onError = args[3] as (CartUpdateError) -> Unit
            onError(CartUpdateError.NotFound)
        }

        viewModel.addOrUpdateQuantityToCart()

        verify { cartRepository.addCartItem(any(), eq(1), any(), any()) }
    }

    @Test
    fun `addCartItem 성공 시 추가 완료 알림 이벤트가 발생한다`() {
        val newCartId = 999
        every { cartRepository.updateQuantity(any(), any(), any(), any()) } answers {
            val onError = args[3] as (CartUpdateError) -> Unit
            onError(CartUpdateError.NotFound)
        }
        every { cartRepository.addCartItem(any(), any(), any(), any()) } answers {
            thirdArg<(Int, Int) -> Unit>()(200, newCartId)
        }

        viewModel.addOrUpdateQuantityToCart()

        assertThat(viewModel.alertEvent.getValue()?.resourceId ?: -99).isEqualTo(R.string.goods_detail_cart_insert_complete_toast_message)
    }

    @Test
    fun `최근 본 상품이 현재 상품과 다를 때 mostRecentlyViewedGoods가 설정된다`() {
        every { goodsRepository.fetchMostRecentGoods(any()) } answers {
            firstArg<(Goods?) -> Unit>()(recentlyViewedGoods)
        }

        viewModel.initMostRecentlyViewedGoods()

        verify { goodsRepository.fetchMostRecentGoods(any()) }

        assertThat(viewModel.mostRecentlyViewedGoods.value).isEqualTo(recentlyViewedGoods)
    }

    @Test
    fun `최근 본 상품이 현재 상품과 같을 때 mostRecentlyViewedGoods가 설정되지 않는다`() {
        every { goodsRepository.fetchMostRecentGoods(any()) } answers {
            firstArg<(Goods?) -> Unit>()(sampleGoods) // 현재 상품과 동일한 ID
        }

        viewModel.initMostRecentlyViewedGoods()

        assertThat(viewModel.mostRecentlyViewedGoods.value).isNull()
    }

    @Test
    fun `initMostRecentlyViewedGoods 호출 시 현재 상품이 최근 본 상품 목록에 로깅된다`() {
        viewModel.initMostRecentlyViewedGoods()

        verify { goodsRepository.loggingRecentGoods(any(), any()) }
    }

    @Test
    fun `onClickMostRecentlyGoodsSection 호출 시 클릭 이벤트가 발생한다`() {
        every { goodsRepository.fetchMostRecentGoods(any()) } answers {
            firstArg<(Goods?) -> Unit>()(recentlyViewedGoods)
        }
        viewModel.initMostRecentlyViewedGoods()

        viewModel.onClickMostRecentlyGoodsSection()

        assertThat(viewModel.clickMostRecentlyGoodsEvent.getValue()).isEqualTo(recentlyViewedGoods)
    }

    @Test
    fun `최근 본 상품이 없을 때 onClickMostRecentlyGoodsSection 호출 시 이벤트가 발생하지 않는다`() {
        viewModel.onClickMostRecentlyGoodsSection()

        assertThat(viewModel.clickMostRecentlyGoodsEvent.getValue()).isNull()
    }

    @Test
    fun `loggingRecentViewedGoods 호출 시 repository loggingRecentGoods가 호출된다`() {
        val testGoods = Goods("테스트상품", 1000, "url", 3, "카테고리")

        viewModel.loggingRecentViewedGoods(testGoods)

        verify { goodsRepository.loggingRecentGoods(eq(testGoods), any()) }
    }

    @Test
    fun `여러 번 수량 변경 후 장바구니 추가 시 올바른 수량으로 처리된다`() {
        viewModel.increaseSelectorQuantity()
        viewModel.increaseSelectorQuantity()
        viewModel.increaseSelectorQuantity() // 수량 4
        viewModel.decreaseSelectorQuantity() // 수량 3

        viewModel.addOrUpdateQuantityToCart()

        verify {
            cartRepository.updateQuantity(
                any(),
                match { it.quantity == 3 },
                any(),
                any(),
            )
        }
    }

    @Test
    fun `UiModel로 전달받은 CartItem 상품 정보가 올바르게 설정된다`() {
        val cartItem = viewModel.cartItem.value

        assertThat(cartItem?.goods?.id).isEqualTo(sampleGoodsUiModel.id)
        assertThat(cartItem?.goods?.name).isEqualTo(sampleGoodsUiModel.name)
        assertThat(cartItem?.goods?.price).isEqualTo(sampleGoodsUiModel.price)
        assertThat(cartItem?.goods?.thumbnailUrl).isEqualTo(sampleGoodsUiModel.thumbnailUrl)
    }

    @Test
    fun `cartRepository updateQuantity 호출시 올바른 매개변수가 전달된다`() {
        val expectedQuantity = 3
        viewModel.increaseSelectorQuantity()
        viewModel.increaseSelectorQuantity()

        viewModel.addOrUpdateQuantityToCart()

        verify {
            cartRepository.updateQuantity(
                eq(initialCartId),
                match { it.quantity == expectedQuantity },
                any(),
                any(),
            )
        }
    }

    @Test
    fun `initMostRecentlyViewedGoods호출시 현재 상품을 최근 본 상품에 로깅한다`() {
        every { goodsRepository.fetchMostRecentGoods(any()) } answers {
            firstArg<(Goods?) -> Unit>()(recentlyViewedGoods)
        }

        viewModel.initMostRecentlyViewedGoods()

        verify { goodsRepository.loggingRecentGoods(any(), any()) }
    }
}
