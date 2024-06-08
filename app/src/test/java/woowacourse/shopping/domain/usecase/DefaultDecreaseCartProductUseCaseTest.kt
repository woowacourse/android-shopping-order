package woowacourse.shopping.domain.usecase

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.booleans.shouldBeTrue
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
import woowacourse.shopping.domain.entity.fakeProduct
import woowacourse.shopping.domain.repository.CartRepository
import woowacourse.shopping.domain.repository.ProductRepository
import woowacourse.shopping.domain.usecase.cart.DefaultDecreaseCartProductUseCase

@ExtendWith(MockKExtension::class)
class DefaultDecreaseCartProductUseCaseTest {
    @RelaxedMockK
    private lateinit var productRepository: ProductRepository

    @RelaxedMockK
    private lateinit var cartRepository: CartRepository

    @InjectMockKs
    private lateinit var defaultDecreaseCartProductUseCase: DefaultDecreaseCartProductUseCase

    @Test
    fun `product id 가 유효하지 않으면 예외 발생`() =
        runTest {
            // given
            val productId = 1L
            val amount = 1
            coEvery { productRepository.findProductById(productId) } returns
                Result.failure(
                    IllegalArgumentException(),
                )
            // when
            val actual = defaultDecreaseCartProductUseCase(productId, amount)
            // then
            assertSoftly {
                actual.isFailure.shouldBeTrue()
                actual.exceptionOrNull().shouldBeTypeOf<IllegalArgumentException>()
            }
        }

    @Test
    fun `product id에 해당하는 상품이 쇼핑 카트에 없으면 예외 발생`() =
        runTest {
            // given
            val productId = 1L
            val amount = 1
            coEvery { productRepository.findProductById(productId) } returns Result.success(fakeProduct())
            coEvery { cartRepository.findCartProduct(productId) } returns
                Result.failure(
                    IllegalArgumentException(),
                )
            // when
            val actual = defaultDecreaseCartProductUseCase(productId, amount)
            // then
            assertSoftly {
                actual.isFailure.shouldBeTrue()
                actual.exceptionOrNull().shouldBeTypeOf<IllegalArgumentException>()
            }
        }

    @Test
    fun `장바구니에 상품이 있고, 수량이 1개 이하면 상품을 삭제한다`() =
        runTest {
            // given
            val productId = 1L
            val amount = 1
            val product = fakeProduct(id = productId)
            val cartProduct = fakeCartProduct(productId = productId, count = 1)
            val cart = Cart(cartProduct)
            coEvery { productRepository.findProductById(productId) } returns Result.success(product)
            coEvery { cartRepository.findCartProduct(productId) } returns Result.success(cartProduct)
            coEvery { cartRepository.deleteCartProduct(productId) } returns Result.success(cart)
            // when
            val actual = defaultDecreaseCartProductUseCase(productId, amount)
            // then
            assertSoftly {
                actual.isSuccess.shouldBeTrue()
                actual.getOrNull() shouldBe cart
            }
        }

    @Test
    fun `장바구니에 상품이 있고, 수량이 1개 이상이면 수량을 감소시킨다`() =
        runTest {
            // given
            val productId = 1L
            val amount = 1
            val product = fakeProduct(id = productId)
            val cartProduct = fakeCartProduct(productId = productId, count = 2)
            val decreasedCount = 1
            val cart = Cart(fakeCartProduct(productId = productId, count = decreasedCount))
            coEvery { productRepository.findProductById(productId) } returns Result.success(product)
            coEvery { cartRepository.findCartProduct(productId) } returns Result.success(cartProduct)
            coEvery {
                cartRepository.updateCartProduct(product, decreasedCount)
            } returns Result.success(cart)
            // when
            val actual = defaultDecreaseCartProductUseCase(productId, amount)
            // then
            assertSoftly {
                actual.isSuccess.shouldBeTrue()
                actual.getOrNull() shouldBe cart
            }
        }
}
