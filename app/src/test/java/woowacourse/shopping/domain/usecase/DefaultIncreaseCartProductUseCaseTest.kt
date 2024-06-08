package woowacourse.shopping.domain.usecase

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeTypeOf
import io.mockk.coEvery
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import woowacourse.shopping.domain.entity.Cart
import woowacourse.shopping.domain.entity.fakeCartProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository

@ExtendWith(MockKExtension::class)
class DefaultIncreaseCartProductUseCaseTest {

    @RelaxedMockK
    private lateinit var cartRepository: CartRepository

    @RelaxedMockK
    private lateinit var productRepository: ProductRepository

    @InjectMockKs
    private lateinit var defaultIncreaseCartProductUseCase: DefaultIncreaseCartProductUseCase


    @Test
    fun `product id가 유효하지 않으면, 예외 발생`() = runTest {
        // given
        val productId = 1L
        val amount = 1
        coEvery { productRepository.findProductById(productId) } returns Result.failure(
            IllegalArgumentException()
        )
        // when
        val actual = defaultIncreaseCartProductUseCase(productId, amount)
        // then
        assertSoftly {
            actual.isFailure shouldBe true
            actual.exceptionOrNull().shouldBeTypeOf<IllegalArgumentException>()
        }
    }

    @Test
    fun `cart 에 상품이 없으면 새롭게 생성한다`() = runTest {
        // given
        val productId = 1L
        val amount = 1
        val cartProduct = fakeCartProduct(productId = productId, count = amount)
        val cart = Cart(cartProduct)
        coEvery { productRepository.findProductById(productId) } returns Result.success(cartProduct.product)
        coEvery { cartRepository.findCartProduct(productId) } returns Result.failure(
            NoSuchElementException()
        )
        coEvery {
            cartRepository.createCartProduct(
                cartProduct.product,
                amount
            )
        } returns Result.success(cart)
        // when
        val actual = defaultIncreaseCartProductUseCase(productId, amount)
        // then
        assertSoftly {
            actual.isSuccess shouldBe true
            actual.getOrThrow() shouldBe cart
        }
    }

    @Test
    fun `cart 에 상품이 있으면, 수량을 증가시킨다`() = runTest {
        // given
        val productId = 1L
        val amount = 1
        val cartProduct = fakeCartProduct(productId = productId, count = 1)
        val newCartProduct = fakeCartProduct(productId = productId, count = 2)
        val cart = Cart(newCartProduct)
        coEvery { productRepository.findProductById(productId) } returns Result.success(cartProduct.product)
        coEvery { cartRepository.findCartProduct(productId) } returns Result.success(cartProduct)
        coEvery {
            cartRepository.updateCartProduct(
                cartProduct.product,
                newCartProduct.count
            )
        } returns Result.success(cart)
        // when
        val actual = defaultIncreaseCartProductUseCase(productId, amount)
        // then
        assertSoftly {
            actual.isSuccess shouldBe true
            actual.getOrThrow() shouldBe cart
        }
    }
}