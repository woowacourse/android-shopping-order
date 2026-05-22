package woowacourse.shopping.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.RegisterExtension
import woowacourse.shopping.domain.Product
import woowacourse.shopping.domain.PurchaseProduct
import woowacourse.shopping.ui.recommendation.RecommendationViewModel
import woowacourse.shopping.viewmodel.fakes.FakeCartRepository
import woowacourse.shopping.viewmodel.fakes.FakeProductRepository
import woowacourse.shopping.viewmodel.fakes.FakeRecentlyViewedProductRepository

@OptIn(ExperimentalCoroutinesApi::class)
class RecommendationViewModelTest {
    @JvmField
    @RegisterExtension
    val mainDispatcherExtension = MainDispatcherExtension()

    private lateinit var cartRepository: FakeCartRepository
    private lateinit var productRepository: FakeProductRepository
    private lateinit var recentlyViewedProductRepository: FakeRecentlyViewedProductRepository
    private lateinit var viewModel: RecommendationViewModel

    @BeforeEach
    fun initViewModel() {
        cartRepository = FakeCartRepository()
        productRepository = FakeProductRepository()
        recentlyViewedProductRepository = FakeRecentlyViewedProductRepository()
    }

    @Test
    fun `최근 본 상품의 카테고리의 상품 추천 목록을 불러온다`() =
        runTest {
            // given: 상품 목록이 주어진다
            val snack1 =
                Product(
                    category = "과자",
                    id = 1L,
                    imageUri = "uri",
                    name = "통키",
                    price = 1000,
                )

            val drink =
                Product(
                    category = "음료",
                    id = 2L,
                    imageUri = "uri",
                    name = "환타",
                    price = 1200,
                )
            val snack2 =
                Product(
                    category = "과자",
                    id = 3L,
                    imageUri = "uri",
                    name = "썬칩",
                    price = 1400,
                )
            productRepository.setProducts(listOf(snack1, drink, snack2))
            recentlyViewedProductRepository.updateList(snack1)

            // when: viewModel이 초기화될 때
            viewModel =
                RecommendationViewModel(
                    cartRepository = cartRepository,
                    productRepository = productRepository,
                    recentlyViewedProductRepository = recentlyViewedProductRepository,
                    initPrice = 0,
                    initCheckedItemIds = emptyList(),
                )

            // then: 카테고리 상품 추천 목록을 불러온다
            assertEquals(
                snack2.id,
                viewModel.uiState.value.recommendedProducts.products[1]
                    .id,
            )
        }

    @Test
    fun `상품을 장바구니에 추가하면 결제할 금액에 반영된다`() =
        runTest {
            // given: 추천 상품을 장바구니에 추가한다
            val purchaseProduct =
                PurchaseProduct(
                    id = 1L,
                    product =
                        Product(
                            category = "과자",
                            id = 1L,
                            imageUri = "uri",
                            name = "통키",
                            price = 1000,
                        ),
                    count = 1,
                )
            cartRepository.insert(purchaseProduct)
            viewModel =
                RecommendationViewModel(
                    cartRepository = cartRepository,
                    productRepository = productRepository,
                    recentlyViewedProductRepository = recentlyViewedProductRepository,
                    initPrice = 1000,
                    initCheckedItemIds = emptyList(),
                )

            // when: 상품의 개수를 변경하면
            viewModel.updateCountWithID(1L, 2)

            // then: 결제할 가격에 반영된다
            assertEquals(2000, viewModel.uiState.value.totalPrice)
        }
}
