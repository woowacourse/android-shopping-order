package woowacourse.shopping.presentation.recommend

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.fixture.FakeCartItemRepository
import woowacourse.shopping.fixture.FakeCatalogItemRepository
import woowacourse.shopping.presentation.product.catalog.ProductUiModel
import woowacourse.shopping.util.CoroutinesTestExtension
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.getOrAwaitValue
import kotlin.test.Test

@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class RecommendViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: RecommendViewModel

    @Test
    fun `초기 상태에서 추천 상품이 로드된다`() =
        runTest {
            viewModel =
                RecommendViewModel(
                    productsRepository = FakeCatalogItemRepository(20),
                    cartItemRepository = FakeCartItemRepository(0),
                    initialCheckedItems =
                        listOf(
                            ProductUiModel(
                                id = 1L,
                                name = "아이스 카페 아메리카노",
                                imageUrl = "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[110563]_20210426095937947.jpg",
                                price = 10000,
                                quantity = 2,
                                isChecked = true,
                            ),
                        ),
                )

            val items = viewModel.items.getOrAwaitValue()

            assertThat(items).isNotEmpty
        }

    @Test
    fun `상품 수량이 정상적으로 증가한다`() =
        runTest {
            val product =
                ProductUiModel(
                    id = 2L,
                    name = "아이스 카페 아메리카노",
                    imageUrl = "",
                    price = 1000,
                    quantity = 0,
                )

            viewModel =
                RecommendViewModel(
                    productsRepository = FakeCatalogItemRepository(20),
                    cartItemRepository = FakeCartItemRepository(0),
                    initialCheckedItems =
                        listOf(
                            ProductUiModel(
                                id = 1L,
                                name = "아이스 카페 아메리카노",
                                imageUrl = "https://image.istarbucks.co.kr/upload/store/skuimg/2021/04/[110563]_20210426095937947.jpg",
                                price = 10000,
                                quantity = 2,
                                isChecked = true,
                            ),
                        ),
                )

            viewModel.toggleQuantity(product)

            val updated = viewModel.items.getOrAwaitValue().find { it.id == product.id }
            assertThat(updated?.quantity).isEqualTo(1)

            val checked = viewModel.checkedItems.getOrAwaitValue()
            assertThat(checked).anyMatch { it.id == product.id && it.quantity == 1 }
        }

    @Test
    fun `선택한 상품들이 변경되면 총 금액과 총 수량이 반영된다`() =
        runTest {
            val checkedProducts =
                listOf(
                    ProductUiModel(
                        id = 1L,
                        name = "아이스 아메리카노",
                        imageUrl = "",
                        price = 1000,
                        quantity = 2,
                    ),
                    ProductUiModel(
                        id = 2L,
                        name = "베르가못 콜드브루",
                        imageUrl = "",
                        price = 2000,
                        quantity = 1,
                    ),
                )

            viewModel =
                RecommendViewModel(
                    productsRepository = FakeCatalogItemRepository(20),
                    cartItemRepository = FakeCartItemRepository(0),
                    initialCheckedItems = checkedProducts,
                )

            val totalPrice = viewModel.totalOrderPrice.getOrAwaitValue()
            val totalCount = viewModel.totalOrderCount.getOrAwaitValue()

            assertThat(totalPrice).isEqualTo(1000 * 2 + 2000 * 1)
            assertThat(totalCount).isEqualTo(3)
        }

    @Test
    fun `주문 요청 성공 시 결제 화면 이동 이벤트가 발생한다`() =
        runTest {
            val checkedProducts =
                listOf(
                    ProductUiModel(
                        id = 1L,
                        name = "아이스 아메리카노",
                        imageUrl = "",
                        price = 1000,
                        quantity = 1,
                    ),
                )

            viewModel =
                RecommendViewModel(
                    productsRepository = FakeCatalogItemRepository(20),
                    cartItemRepository = FakeCartItemRepository(0),
                    initialCheckedItems = checkedProducts,
                )

            viewModel.onOrderClick()

            val event = viewModel.navigateToPaymentEvent.getOrAwaitValue()
            assertThat(event.checkedItems).containsExactlyElementsOf(checkedProducts)
        }

    @Test
    fun `RecommendActivity에서 돌아와도 이전의 데이터가 동일하게 유지된다`() =
        runTest {
            viewModel =
                RecommendViewModel(
                    productsRepository = FakeCatalogItemRepository(20),
                    cartItemRepository = FakeCartItemRepository(0),
                    initialCheckedItems = emptyList(),
                )

            val toRestore =
                listOf(
                    ProductUiModel(
                        id = 2L,
                        name = "아이스 아메리카노",
                        imageUrl = "",
                        price = 1000,
                        quantity = 2,
                    ),
                )

            viewModel.restoreCheckedProducts(toRestore)

            val checked = viewModel.checkedItems.getOrAwaitValue()
            val items = viewModel.items.getOrAwaitValue()

            assertThat(checked).containsExactlyElementsOf(toRestore)

            assertThat(items.find { it.id == 2L }?.quantity).isEqualTo(2)
            assertThat(items.find { it.id == 2L }?.isChecked).isTrue()
        }
}
