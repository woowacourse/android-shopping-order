package woowacourse.shopping.viewmodel.cart.recommendation

import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.comparables.shouldBeLessThan
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.invoke
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.model.PagedResult
import woowacourse.shopping.domain.model.CartProduct
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.domain.model.RecentProduct
import woowacourse.shopping.domain.repository.CartProductRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.repository.RecentProductRepository
import woowacourse.shopping.fixture.CoroutinesTestExtension
import woowacourse.shopping.view.cart.recommendation.CartProductRecommendationViewModel
import woowacourse.shopping.viewmodel.InstantTaskExecutorExtension
import woowacourse.shopping.viewmodel.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(CoroutinesTestExtension::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class CartProductRecommendationViewModelTest {
    private val mockProduct =
        Product(
            id = 1,
            imageUrl = "",
            name = "hwannow",
            price = 20000,
            category = "woowahan",
        )
    private lateinit var viewModel: CartProductRecommendationViewModel
    private val productRepository: ProductRepository = mockk(relaxed = true)
    private val cartProductRepository: CartProductRepository = mockk(relaxed = true)
    private val recentProductRepository: RecentProductRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        coEvery { cartProductRepository.getPagedProducts(any(), any()) } returns
            Result.success(
                PagedResult(
                    items =
                        listOf(
                            CartProduct(
                                id = 10101,
                                product =
                                    Product(
                                        id = 1,
                                        imageUrl = "",
                                        name = "hwannow",
                                        price = 20000,
                                        category = "woowahan",
                                    ),
                            ),
                        ),
                    hasNext = false,
                ),
            )

        every { recentProductRepository.getLastViewedProduct(captureLambda()) } answers {
            lambda<(Result<RecentProduct?>) -> Unit>().invoke(
                Result.success(
                    RecentProduct(
                        product =
                            Product(
                                id = 1,
                                imageUrl = "",
                                name = "hwannow",
                                price = 20000,
                                category = "woowahan",
                            ),
                    ),
                ),
            )
        }

        every { productRepository.getPagedProducts(any(), any(), captureLambda()) } answers {
            lambda<(Result<PagedResult<Product>>) -> Unit>().invoke(
                Result.success(
                    PagedResult(
                        items =
                            List(20) {
                                Product(
                                    id = it + 1,
                                    imageUrl = "",
                                    name = "hwannow${it + 1}",
                                    price = 20000,
                                    category = "woowahan",
                                )
                            },
                        hasNext = false,
                    ),
                ),
            )
        }
        viewModel =
            CartProductRecommendationViewModel(
                productRepository,
                cartProductRepository,
                recentProductRepository,
            )

        viewModel.initShoppingCartInfo(emptySet(), 0, 0)
    }

    @Test
    fun `최근 본 상품의 카테고리를 기반으로 추천 상품 목록이 생성된다`() {
        // when
        val recommendedProducts = viewModel.recommendedProducts.getOrAwaitValue()

        // then
        recommendedProducts.all { it.product.category == "woowahan" }.shouldBeTrue()
    }

    @Test
    fun `장바구니에 담긴 상품은 추천 상품에 뜨지 않는다`() {
        // when
        val recommendedProducts = viewModel.recommendedProducts.getOrAwaitValue()

        // then
        recommendedProducts shouldNotContain mockProduct
    }

    @Test
    fun `장바구니에 상품을 추가한다`() =
        runTest {
            // given
            val id = slot<Int>()
            coEvery { cartProductRepository.insert(capture(id), any()) } returns Result.success(10102)

            val beforePrice = viewModel.totalPrice.getOrAwaitValue()
            val beforeCount = viewModel.totalCount.getOrAwaitValue()

            // when
            viewModel.onAddClick(mockProduct.copy(id = 2))
            val actualPrice = viewModel.totalPrice.getOrAwaitValue()
            val actualCount = viewModel.totalCount.getOrAwaitValue()

            // then
            coVerify { cartProductRepository.insert(any(), any()) }

            id.captured shouldBe 2
            actualPrice shouldBeGreaterThan beforePrice
            actualCount shouldBeGreaterThan beforeCount
        }

    @Test
    fun `장바구니에 추가한 상품의 개수를 증가한다`() =
        runTest {
            // given
            coEvery { cartProductRepository.insert(any(), any()) } returns Result.success(10102)
            viewModel.onAddClick(mockProduct.copy(id = 2))

            coEvery { cartProductRepository.updateQuantity(any(), any()) } returns Result.success(Unit)

            val beforePrice = viewModel.totalPrice.getOrAwaitValue()
            val beforeCount = viewModel.totalCount.getOrAwaitValue()

            // when
            viewModel.onQuantityIncreaseClick(mockProduct.copy(id = 2))
            val actual =
                viewModel.recommendedProducts.getOrAwaitValue().first { it.product.id == 2 }.quantity
            val actualPrice = viewModel.totalPrice.getOrAwaitValue()
            val actualCount = viewModel.totalCount.getOrAwaitValue()

            // then
            actual shouldBe 2
            actualPrice shouldBeGreaterThan beforePrice
            actualCount shouldBeGreaterThan beforeCount
        }

    @Test
    fun `장바구니에 추가한 상품의 개수를 감소한다`() =
        runTest {
            // given
            coEvery { cartProductRepository.insert(any(), any()) } returns Result.success(10102)
            viewModel.onAddClick(mockProduct.copy(id = 2))

            coEvery { cartProductRepository.updateQuantity(any(), any()) } returns Result.success(Unit)
            viewModel.onQuantityIncreaseClick(mockProduct.copy(id = 2))

            val beforePrice = viewModel.totalPrice.getOrAwaitValue()
            val beforeCount = viewModel.totalCount.getOrAwaitValue()

            // when
            viewModel.onQuantityDecreaseClick(mockProduct.copy(id = 2))
            val actual =
                viewModel.recommendedProducts.getOrAwaitValue().first { it.product.id == 2 }.quantity
            val actualPrice = viewModel.totalPrice.getOrAwaitValue()
            val actualCount = viewModel.totalCount.getOrAwaitValue()

            // then
            actual shouldBe 1
            actualPrice shouldBeLessThan beforePrice
            actualCount shouldBeLessThan beforeCount
        }
}
