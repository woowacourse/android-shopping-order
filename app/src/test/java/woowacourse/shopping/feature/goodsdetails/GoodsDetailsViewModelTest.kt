package woowacourse.shopping.feature.goodsdetails

import io.kotest.assertions.failure
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.R
import woowacourse.shopping.data.carts.AddItemResult
import woowacourse.shopping.data.carts.CartFetchResult
import woowacourse.shopping.data.carts.CartUpdateResult
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

    private val testDispatcher = UnconfinedTestDispatcher()

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
    fun setUp() =
        runTest {
            Dispatchers.setMain(testDispatcher)
            MockKAnnotations.init(this@GoodsDetailsViewModelTest)
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

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun setupLiveDataObservers() {
        viewModel.cartItem.observeForever { }
        viewModel.mostRecentlyViewedGoods.observeForever { }
    }

    private fun setupRepositoryMocks() {
        coEvery { goodsRepository.fetchMostRecentGoods() } returns null
        coEvery { goodsRepository.loggingRecentGoods(any()) } returns Unit
        coEvery {
            cartRepository.updateQuantity(
                any(),
                any(),
            )
        } returns CartUpdateResult.Success(200)
        coEvery { cartRepository.addCartItem(any(), any()) } returns
            CartFetchResult.Success(
                AddItemResult(200, 1),
            )
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

        coVerify {
            cartRepository.updateQuantity(
                eq(initialCartId),
                any<CartQuantity>(),
            )
        }
    }

    @Test
    fun `updateQuantity 성공 시 완료 알림 이벤트가 발생한다`() {
        coEvery {
            cartRepository.updateQuantity(
                any(),
                any(),
            )
        } returns CartUpdateResult.Success(200)
        viewModel.increaseSelectorQuantity() // 수량 2

        viewModel.addOrUpdateQuantityToCart()

        val goodsDetailsAlertMessage = viewModel.alertEvent.getValue()
        when (goodsDetailsAlertMessage) {
            is GoodsDetailsAlertMessage.ResourceId ->
                assertThat(
                    goodsDetailsAlertMessage.resourceId,
                ).isEqualTo(R.string.goods_detail_cart_update_complete_toast_message)
            else -> failure("알림 이벤트 발생 안함")
        }
    }

    @Test
    fun `addCartItem 성공 시 추가 완료 알림 이벤트가 발생한다`() =
        runTest {
            val newCartId = 999
            coEvery { cartRepository.addCartItem(any(), any()) } returns
                CartFetchResult.Success(
                    AddItemResult(200, newCartId),
                )

            val viewModelWithNullCartId =
                GoodsDetailsViewModel(
                    goodsUiModel = sampleGoodsUiModel,
                    cartRepository = cartRepository,
                    goodsRepository = goodsRepository,
                    cartId = GoodsDetailsViewModel.NULL_CART_ID, // -1로 설정
                )

            viewModelWithNullCartId.addOrUpdateQuantityToCart()
            val goodsDetailsAlertMessage = viewModelWithNullCartId.alertEvent.getValue()
            when (goodsDetailsAlertMessage) {
                is GoodsDetailsAlertMessage.ResourceId ->
                    assertThat(
                        goodsDetailsAlertMessage.resourceId,
                    ).isEqualTo(R.string.goods_detail_cart_insert_complete_toast_message)
                else -> failure("알림 이벤트 발생 안함")
            }
        }

    @Test
    fun `최근 본 상품이 현재 상품과 다를 때 mostRecentlyViewedGoods가 설정된다`() {
        coEvery { goodsRepository.fetchMostRecentGoods() } returns recentlyViewedGoods
        viewModel.initMostRecentlyViewedGoods()

        coVerify { goodsRepository.fetchMostRecentGoods() }

        assertThat(viewModel.mostRecentlyViewedGoods.value).isEqualTo(recentlyViewedGoods)
    }

    @Test
    fun `최근 본 상품이 현재 상품과 같을 때 mostRecentlyViewedGoods가 설정되지 않는다`() =
        runTest {
            coEvery { goodsRepository.fetchMostRecentGoods() } returns sampleGoods

            viewModel.initMostRecentlyViewedGoods()

            assertThat(viewModel.mostRecentlyViewedGoods.value).isNull()
        }

    @Test
    fun `initMostRecentlyViewedGoods 호출 시 현재 상품이 최근 본 상품 목록에 로깅된다`() =
        runTest {
            viewModel.initMostRecentlyViewedGoods()

            coVerify { goodsRepository.loggingRecentGoods(any()) }
        }

    @Test
    fun `onClickMostRecentlyGoodsSection 호출 시 클릭 이벤트가 발생한다`() =
        runTest {
            coEvery { goodsRepository.fetchMostRecentGoods() } returns recentlyViewedGoods
            viewModel.initMostRecentlyViewedGoods()

            viewModel.onClickMostRecentlyGoodsSection()

            assertThat(viewModel.clickMostRecentlyGoodsEvent.getValue()).isEqualTo(
                recentlyViewedGoods,
            )
        }

    @Test
    fun `최근 본 상품이 없을 때 onClickMostRecentlyGoodsSection 호출 시 이벤트가 발생하지 않는다`() {
        viewModel.onClickMostRecentlyGoodsSection()

        assertThat(viewModel.clickMostRecentlyGoodsEvent.getValue()).isNull()
    }

    @Test
    fun `loggingRecentViewedGoods 호출 시 repository loggingRecentGoods가 호출된다`() =
        runTest {
            val testGoods = Goods("테스트상품", 1000, "url", 3, "카테고리")

            viewModel.loggingRecentViewedGoods(testGoods)

            coVerify { goodsRepository.loggingRecentGoods(eq(testGoods)) }
        }

    @Test
    fun `여러 번 수량 변경 후 장바구니 추가 시 올바른 수량으로 처리된다`() =
        runTest {
            viewModel.increaseSelectorQuantity()
            viewModel.increaseSelectorQuantity()
            viewModel.increaseSelectorQuantity() // 수량 4
            viewModel.decreaseSelectorQuantity() // 수량 3

            viewModel.addOrUpdateQuantityToCart()

            coVerify {
                cartRepository.updateQuantity(
                    any(),
                    match { it.quantity == 3 },
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
    fun `cartRepository updateQuantity 호출시 올바른 매개변수가 전달된다`() =
        runTest {
            val expectedQuantity = 3
            viewModel.increaseSelectorQuantity()
            viewModel.increaseSelectorQuantity()

            viewModel.addOrUpdateQuantityToCart()

            coVerify {
                cartRepository.updateQuantity(
                    eq(initialCartId),
                    match { it.quantity == expectedQuantity },
                )
            }
        }

    @Test
    fun `initMostRecentlyViewedGoods 호출시 현재 상품을 최근 본 상품에 로깅한다`() =
        runTest {
            coEvery { goodsRepository.fetchMostRecentGoods() } returns recentlyViewedGoods

            viewModel.initMostRecentlyViewedGoods()

            coVerify { goodsRepository.loggingRecentGoods(any()) }
        }
}
