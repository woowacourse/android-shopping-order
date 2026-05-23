package woowacourse.shopping.data.remote.server.repository

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import mockwebserver3.MockResponse
import mockwebserver3.MockWebServer
import okhttp3.MediaType.Companion.toMediaType
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import woowacourse.shopping.data.remote.server.service.ProductService
import woowacourse.shopping.domain.repository.ProductRepository

class ProductRepositoryImplTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var productService: ProductService
    private lateinit var productRepository: ProductRepository

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()

        productService = retrofit.create(ProductService::class.java)
        productRepository = ProductRepositoryImpl(productService)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.close()
    }

    @Test
    fun `상품 목록 조회 시 응답 body를 상품 목록으로 변환한다`() = runBlocking {
        // given
        mockWebServer.enqueue(
            MockResponse.Builder()
                .code(200)
                .body(productsResponseBody(category = "음료"))
                .addHeader("Content-Type", "application/json")
                .build()
        )

        // when
        val products = productRepository.getProducts(page = 0, pageSize = 10)

        // then
        assertEquals(1, products.size)
        assertEquals(1L, products[0].id)
        assertEquals("상품", products[0].name)
        assertEquals("https://via.placeholder.com/300", products[0].imageUri)
        val request = mockWebServer.takeRequest()
        assertEquals("/products?page=0&size=10", request.target)
    }

    @Test
    fun `상품 상세 조회 시 응답 body를 상품으로 변환한다`() = runBlocking {
        // given
        mockWebServer.enqueue(
            MockResponse.Builder()
                .code(200)
                .body(productResponseBody())
                .addHeader("Content-Type", "application/json")
                .build()
        )

        // when
        val product = productRepository.getProduct(1L)

        // then
        assertEquals(1L, product.id)
        assertEquals("상품", product.name)
        val request = mockWebServer.takeRequest()
        assertEquals("/products/1", request.target)
    }

    @Test
    fun `카테고리 상품 조회 시 category를 쿼리로 전달하고 응답 body를 상품 목록으로 변환한다`() = runBlocking {
        // given
        mockWebServer.enqueue(
            MockResponse.Builder()
                .code(200)
                .body(productsResponseBody(category = "음료"))
                .addHeader("Content-Type", "application/json")
                .build()
        )

        // when
        val products = productRepository.getCategoryProducts(category = "음료")

        // then
        assertEquals(1, products.size)
        assertEquals("음료", products[0].category)
        val request = mockWebServer.takeRequest()
        assertEquals("/products?category=%EC%9D%8C%EB%A3%8C&page=0&size=10", request.target)
    }

    private fun productResponseBody(): String =
        """
        {
          "category": "음료",
          "id": 1,
          "imageUrl": "url",
          "name": "상품",
          "price": 1000
        }
        """.trimIndent()

    private fun productsResponseBody(category: String): String =
        """
        {
          "content": [
            {
              "category": "$category",
              "id": 1,
              "imageUrl": "",
              "name": "상품",
              "price": 1000
            }
          ],
          "empty": false,
          "first": true,
          "last": true,
          "number": 0,
          "numberOfElements": 1,
          "pageable": {
            "offset": 0,
            "pageNumber": 0,
            "pageSize": 10,
            "paged": true,
            "sort": { "empty": true, "sorted": false, "unsorted": true },
            "unpaged": false
          },
          "size": 10,
          "sort": { "empty": true, "sorted": false, "unsorted": true },
          "totalElements": 1,
          "totalPages": 1
        }
        """.trimIndent()
}
