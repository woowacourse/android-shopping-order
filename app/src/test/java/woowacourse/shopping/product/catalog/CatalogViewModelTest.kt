package woowacourse.shopping.product.catalog

import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Rule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.data.mapper.toEntity
import woowacourse.shopping.data.repository.CartProductRepository
import woowacourse.shopping.data.repository.CatalogProductRepository
import woowacourse.shopping.data.repository.RecentlyViewedProductRepository
import woowacourse.shopping.util.CoroutinesTestExtension
import woowacourse.shopping.util.InstantTaskExecutorExtension
import woowacourse.shopping.util.getOrAwaitValue

@ExperimentalCoroutinesApi
@ExtendWith(InstantTaskExecutorExtension::class)
@ExtendWith(CoroutinesTestExtension::class)
class CatalogViewModelTest {
    @get:Rule
    private val catalogRepo: CatalogProductRepository = mockk()
    private val cartRepo: CartProductRepository = mockk()
    private val recentRepo: RecentlyViewedProductRepository = mockk()

    private lateinit var viewModel: CatalogViewModel

    private val dummyProduct =
        ProductUiModel(
            id = 1L,
            imageUrl = "https://example.com/image.jpg",
            name = "테스트 상품",
            price = 1000,
            quantity = 0,
            cartItemId = null,
            isChecked = true,
            category = "전자기기",
        )

    @BeforeEach
    fun setup() {
        viewModel = CatalogViewModel(catalogRepo, cartRepo, recentRepo)
    }

    @Test
    fun `increaseQuantity - 장바구니에 없는 상품 추가`() =
        runTest {
            coEvery { cartRepo.insertCartProduct(any(), any()) } returns 99L
            coEvery { cartRepo.getCartItemSize() } returns 1

            viewModel.increaseQuantity(dummyProduct)

            val updated = viewModel.updatedItem.getOrAwaitValue()
            assertThat(updated.quantity).isEqualTo(1)
            assertThat(updated.cartItemId).isEqualTo(99L)

            val size = viewModel.cartItemSize.getOrAwaitValue()
            assertThat(size).isEqualTo(1)
        }

    @Test
    fun `decreaseQuantity - 수량 1인 상품 삭제`() =
        runTest {
            val productInCart = dummyProduct.copy(quantity = 1, cartItemId = 99L)

            coEvery { cartRepo.deleteCartProduct(99L) } returns true
            coEvery { cartRepo.getCartItemSize() } returns 0

            viewModel.decreaseQuantity(productInCart)

            val updated = viewModel.updatedItem.getOrAwaitValue()
            assertThat(updated.quantity).isEqualTo(0)

            val size = viewModel.cartItemSize.getOrAwaitValue()
            assertThat(size).isEqualTo(0)
        }

    @Test
    fun `loadCatalog - 카탈로그와 장바구니 병합 후 로딩 완료`() =
        runTest {
            coEvery { catalogRepo.getProductsByPage(any(), any()) } returns listOf(dummyProduct)
            coEvery { cartRepo.getTotalElements() } returns 1L
            coEvery { cartRepo.getCartProducts(any()) } returns emptyList()
            coEvery { catalogRepo.getAllProductsSize() } returns 40L

            viewModel.loadCatalog(0, 20, 20, 40)

            val result = viewModel.loadedCatalogItems.getOrAwaitValue()
            val loadingState = viewModel.loadingState.getOrAwaitValue()

            assertThat(result).isNotEmpty()
            assertThat(result.first()).isInstanceOf(CatalogItem.ProductItem::class.java)
            assertThat((result.first() as CatalogItem.ProductItem).productItem.id).isEqualTo(1L)
            assertThat(loadingState.isLoading).isFalse()
        }

    @Test
    fun `loadRecentlyViewedProducts - 최근 본 상품 불러오기`() =
        runTest {
            coEvery { recentRepo.getRecentlyViewedProducts() } returns
                listOf(
                    dummyProduct.toEntity(),
                )

            viewModel.loadRecentlyViewedProducts()

            val result = viewModel.recentlyViewedProducts.getOrAwaitValue()
            assertThat(result).hasSize(1)
            assertThat(result[0].id).isEqualTo(dummyProduct.id)
        }
}
