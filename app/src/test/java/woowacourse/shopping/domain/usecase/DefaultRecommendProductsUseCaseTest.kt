package woowacourse.shopping.domain.usecase

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.fakeCartProduct
import woowacourse.shopping.domain.entity.fakeProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.usecase.product.DefaultRecommendProductsUseCase

@ExtendWith(MockKExtension::class)
class DefaultRecommendProductsUseCaseTest {
    @RelaxedMockK
    private lateinit var cartRepository: CartRepository

    @RelaxedMockK
    private lateinit var productRepository: ProductRepository

    @InjectMockKs
    private lateinit var defaultRecommendProductsUseCase: DefaultRecommendProductsUseCase

    @Test
    fun `최근 상품이 없으면, 카트에 없는 상품을 추천한다`() = runTest {
        // given
        val recommendSize = 3
        val totalProducts = listOf(
            fakeProduct(id = 1), fakeProduct(id = 2), fakeProduct(id = 3), fakeProduct(id = 4)
        )
        val cart = Cart(
            fakeCartProduct(productId = 1),
        )
        coEvery { productRepository.loadRecentProducts(1) } returns Result.success(emptyList())
        coEvery { cartRepository.loadCart() } returns Result.success(cart)
        coEvery { productRepository.loadProducts(0, any()) } returns Result.success(totalProducts)
        // when
        val actual = defaultRecommendProductsUseCase(recommendSize)
        // then
        val expect = listOf(fakeProduct(id = 2), fakeProduct(id = 3), fakeProduct(id = 4))
        actual shouldBe expect
    }

    @Test
    fun `최근 상품이 있으면, 카트에 없고 최근 상품의 카테고리에 해당하는 추천 상품을 가져온다`() = runTest {
        // given
        val recommendSize = 3
        val category = "식품"
        val recentProduct = listOf(fakeProduct(id = 1, category = "식품"))
        val totalProducts = listOf(
            fakeProduct(id = 1, category = category), fakeProduct(id = 2, category = category),
            fakeProduct(id = 3, category = category), fakeProduct(id = 4, category = category),
        )
        val cart = Cart(fakeCartProduct(productId = 2))
        coEvery { productRepository.loadRecentProducts(1) } returns Result.success(recentProduct)
        coEvery { cartRepository.loadCart() } returns Result.success(cart)
        coEvery { productRepository.loadProducts(category, 0, any()) } returns Result.success(
            totalProducts
        )
        // when
        val actual = defaultRecommendProductsUseCase(recommendSize)
        // then
        val expect = listOf(
            fakeProduct(id = 1, category = category),
            fakeProduct(id = 3, category = category),
            fakeProduct(id = 4, category = category),
        )
        actual shouldBe expect
    }

    @Test
    fun `추천할 상품 개수보다 적을 경우, 그 개수만큼만 추천한다`() = runTest {
        // given
        val recommendSize = 3
        val category = "식품"
        val recentProduct = listOf(fakeProduct(id = 1, category = "식품"))
        val totalProducts = listOf(
            fakeProduct(id = 1, category = category), fakeProduct(id = 2, category = category),
        )
        val cart = Cart(fakeCartProduct(productId = 2))
        coEvery { productRepository.loadRecentProducts(1) } returns Result.success(recentProduct)
        coEvery { cartRepository.loadCart() } returns Result.success(cart)
        coEvery { productRepository.loadProducts(category, 0, any()) } returns Result.success(
            totalProducts
        )
        // when
        val actual = defaultRecommendProductsUseCase(recommendSize)
        // then
        val expect = listOf(
            fakeProduct(id = 1, category = category),
        )
        assertSoftly {
            actual shouldHaveSize 1
            actual shouldBe expect
        }
    }
}