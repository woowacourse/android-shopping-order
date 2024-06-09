package woowacourse.shopping.domain.repository

import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import woowacourse.shopping.data.source.ProductDataSource
import woowacourse.shopping.data.source.ShoppingCartDataSource
import woowacourse.shopping.domain.model.Product
import woowacourse.shopping.productsTestFixture
import woowacourse.shopping.remote.model.response.CartItemResponse
import woowacourse.shopping.remote.model.response.ProductResponse
import woowacourse.shopping.source.FakeProductDataSource
import woowacourse.shopping.source.FakeShoppingCartDataSource

class DefaultShoppingProductRepositoryTest {
    private lateinit var productsSource: ProductDataSource
    private lateinit var cartSource: ShoppingCartDataSource

    private lateinit var repo: ShoppingProductsRepository

    @Test
    fun `페이징된 상품들을 가져온다 이 때 상품이ㅣ 장바구니에 있는 것이 있다면, 그 수량을 적용한다 `() =
        runTest {
            // given
            val page = 1
            productsSource =
                FakeProductDataSource(
                    allProducts = productsTestFixture(10).toMutableList(),
                )
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        cartItemDtosTestFixture(
                            10,
                            cartItemFixture = {
                                CartItemResponse(
                                    id = it.toLong(),
                                    quantity = 0,
                                    product = ProductResponse.DEFAULT,
                                )
                            },
                        ),
                )

            repo = DefaultShoppingProductRepository(productsSource, cartSource)

            // when
            val actual = repo.pagedProducts2(page).getOrThrow()

            // then
            assertThat(actual).isEqualTo(
                productsDomainTestFixture(10),
            )
        }

    @Test
    fun `페이징된 상품들을 가져온다 이 때 프로덕트 상품의 개수는 21 개, 그리고 장바구니에는 아무것도 없다`() =
        runTest {
            // given
            val page = 1
            productsSource =
                FakeProductDataSource(
                    allProducts = productsTestFixture(21).toMutableList(),
                )
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses = emptyList(),
                )
            repo = DefaultShoppingProductRepository(productsSource, cartSource)

            // when
            val actual = repo.pagedProducts2(page).getOrThrow()

            // then
            assertThat(actual).isEqualTo(
                productsDomainTestFixture(20),
            )
        }

    @Test
    fun `페이지 까지의 모든 상품들을 가져온다 이 때도 상품이 장바구니에 있는 것이 있따면 그 수량을 적용`() =
        runTest {
            // given
            val page = 2
            productsSource =
                FakeProductDataSource(
                    allProducts = productsTestFixture(21).toMutableList(),
                )
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses =
                        cartItemDtosTestFixture(
                            10,
                            cartItemFixture = {
                                CartItemResponse(
                                    id = it.toLong(),
                                    quantity = 0,
                                    product = ProductResponse.DEFAULT,
                                )
                            },
                        ),
                )
            repo = DefaultShoppingProductRepository(productsSource, cartSource)

            // when
            val actual = repo.allProductsUntilPage2(page).getOrThrow()

            // then
            assertThat(actual).isEqualTo(
                productsDomainTestFixture(21),
            )
        }

    @Test
    fun `상품을 로드한다 이 때 장바구니에 그 상품이 있다면, 장바구니에 담긴 개수도 적용되어야 한다`() =
        runTest {
            // given
            val productId = 1L
            productsSource =
                FakeProductDataSource(
                    allProducts = productsTestFixture(10).toMutableList(),
                )

            cartSource =
                FakeShoppingCartDataSource(
                    CartItemResponse(
                        id = 101,
                        quantity = 10,
                        product = productResponseTestFixture(productId = 1),
                    ),
                    CartItemResponse(
                        id = 102,
                        quantity = 3,
                        product = productResponseTestFixture(productId = 2),
                    ),
                )

            // when
            val repo = DefaultShoppingProductRepository(productsSource, cartSource)
            val actual = repo.loadProduct2(productId).getOrThrow()

            // then
            assertThat(actual).isEqualTo(
                productDomainTestFixture(productId, quantity = 10),
            )
        }

    @Test
    fun `총 상품 10개일 때 첫 페이지는 마지막 페이지이다`() =
        runTest {
            // given
            val page = 1
            productsSource =
                FakeProductDataSource(
                    allProducts = productsTestFixture(10).toMutableList(),
                )
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses = emptyList(),
                )
            val repo = DefaultShoppingProductRepository(productsSource, cartSource)

            // when
            val actual = repo.isFinalPage2(page).getOrThrow()

            // then
            assertThat(actual).isTrue()
        }

    @Test
    fun `총 상품 21 개일 때 첫 페이지는 마지막 페이지가 아니다`() =
        runTest {
            // given
            val page = 1
            productsSource =
                FakeProductDataSource(
                    allProducts = productsTestFixture(21).toMutableList(),
                )
            cartSource =
                FakeShoppingCartDataSource(
                    cartItemResponses = emptyList(),
                )
            val repo = DefaultShoppingProductRepository(productsSource, cartSource)

            // when
            val actual = repo.isFinalPage2(page).getOrThrow()

            // then
            assertThat(actual).isFalse()
        }
}

// domain layer 의 product 를 만드는 test fixture
fun productDomainTestFixture(
    id: Long,
    name: String = "$id name",
    price: Int = 1,
    imageUrl: String = "1",
    quantity: Int = 0,
    category: String = "",
) = Product(
    id = id,
    name = name,
    price = price,
    imgUrl = imageUrl,
    quantity = quantity,
    category = category,
)

fun productsDomainTestFixture(
    dataCount: Int,
    productDomainFixture: (Int) -> Product = { productDomainTestFixture(it.toLong()) },
): List<Product> =
    List(dataCount) {
        productDomainFixture(it)
    }

// ProductResponse 에 대한 test fixture
fun productResponseTestFixture(
    productId: Long = 1,
    name: String = "name",
    price: Int = 1000,
    imageUrl: String = "url",
    category: String = "category",
) = ProductResponse(
    id = productId,
    name = name,
    price = price,
    imageUrl = imageUrl,
    category = category,
)
