package woowacourse.shopping.domain.usecase

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.fakeProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

@ExtendWith(MockKExtension::class)
class DefaultAddCartProductUseCaseTest {
    @RelaxedMockK
    private lateinit var cartRepository: CartRepository

    @RelaxedMockK
    private lateinit var productRepository: ProductRepository

    @InjectMockKs
    private lateinit var defaultAddCartProductUseCase: DefaultAddCartProductUseCase

    @Test
    fun `Id 로 상품을 찾을 수 없고, 장바구니에도 해당 상품이 있으면 장바구니에서 해당 상품을 삭제한다`() = runTest {
        // given
        val productId = 1L
        val count = 1
        coEvery { productRepository.findProductById(productId) } returns Result.failure(
            IllegalArgumentException()
        )
        coEvery { cartRepository.existsCartProduct(productId) } returns true
        coEvery { cartRepository.deleteCartProduct(productId) } returns Result.success(Cart())
        // when
        val actual = defaultAddCartProductUseCase(productId, count)
        // then
        coVerify(exactly = 1) { productRepository.findProductById(productId) }
        coVerify(exactly = 1) { cartRepository.deleteCartProduct(productId) }
        coVerify(exactly = 1) { cartRepository.existsCartProduct(productId) }
        assertSoftly {
            actual.isFailure.shouldBeTrue()
            actual.exceptionOrNull().shouldBeTypeOf<IllegalArgumentException>()
        }
    }

    @Test
    fun `장바구니에 상품이 존재하면, 장바구니 상품을 업데이트한다`() = runTest {
        // given
        val productId = 1L
        val count = 1
        val product = fakeProduct()
        val cart = Cart()
        coEvery { productRepository.findProductById(productId) } returns Result.success(product)
        coEvery { cartRepository.existsCartProduct(productId) } returns true
        coEvery { cartRepository.updateCartProduct(any(), count) } returns Result.success(cart)
        // when
        val actual = defaultAddCartProductUseCase(productId, count)
        // then
        coVerify(exactly = 1) { productRepository.findProductById(productId) }
        coVerify(exactly = 1) { cartRepository.existsCartProduct(productId) }
        coVerify(exactly = 1) { cartRepository.updateCartProduct(any(), count) }
        assertSoftly {
            actual.isSuccess.shouldBeTrue()
            actual.getOrThrow() shouldBe cart
        }
    }

    @Test
    fun `장바구니에 상품이 없으면, 장바구니 상품을 생성한다`() = runTest {
        // given
        val productId = 1L
        val count = 1
        val product = fakeProduct()
        val cart = Cart()
        coEvery { productRepository.findProductById(productId) } returns Result.success(product)
        coEvery { cartRepository.existsCartProduct(productId) } returns false
        coEvery { cartRepository.createCartProduct(any(), count) } returns Result.success(cart)
        // when
        val actual = defaultAddCartProductUseCase(productId, count)
        // then
        coVerify(exactly = 1) { productRepository.findProductById(productId) }
        coVerify(exactly = 1) { cartRepository.existsCartProduct(productId) }
        coVerify(exactly = 1) { cartRepository.createCartProduct(any(), count) }
        assertSoftly {
            actual.isSuccess.shouldBeTrue()
            actual.getOrThrow() shouldBe cart
        }
    }
}