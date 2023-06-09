package woowacourse.shopping.data.cart.source

import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import woowacourse.shopping.data.product.source.NetworkProduct

internal class CartItemRemoteServiceTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var url: HttpUrl
    private lateinit var sut: CartItemRemoteService
    private lateinit var okHttpClient: OkHttpClient

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        url = mockWebServer.url("/")

        sut = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CartItemRemoteService::class.java)

        okHttpClient = OkHttpClient()
    }

    @Test
    fun `장바구니 아이템을 저장하기를 요청했을 때 네트워크 통신이 원활하다면 201번의 응답 코드와 Location 헤더에 장바구니 아이템의 아이디가 포함된 응답을 받는다`() {
        val mockResponse = MockResponse()
            .setResponseCode(201)
            .addHeader("Location", "/cart-items/1")
        mockWebServer.enqueue(mockResponse)

        val anyProductId = 1L
        val response = sut.requestToSave(CartItemSaveRequestBody(anyProductId)).execute()

        assertAll(
            { assertThat(response.code()).isEqualTo(201) },
            { assertThat(response.headers()["Location"]).startsWith("/cart-items/") }
        )
    }

    @Test
    fun `장바구니 아이템을 조회하기를 요청했을 때 네트워크 통신이 원활하다면 200번의 응답 코드와 Content-Type 헤더의 값이 application json인 헤더와 NetworkCartItem으로 파싱될 수 있는 Json 문자열이 바디에 포함된 응답을 받는다`() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
            .setBody(
                """
                    [
                        {
                            "id": 1,
                            "quantity": 5,
                            "product": {
                                "id": 1,
                                "price": 10000,
                                "name": "치킨",
                                "imageUrl": "http://example.com/chicken.jpg"
                            }
                        },
                        {
                            "id": 2,
                            "quantity": 1,
                            "product": {
                                "id": 2,
                                "price": 20000,
                                "name": "피자",
                                "imageUrl": "http://example.com/pizza.jpg"
                            }
                        }
                    ]
                """.trimIndent()
            )
        mockWebServer.enqueue(mockResponse)

        val response = sut.requestCartItems().execute()

        assertAll(
            { assertThat(response.code()).isEqualTo(200) },
            { assertThat(response.headers()["Content-Type"]).isEqualTo("application/json") },
            {
                assertThat(response.body()).isEqualTo(
                    listOf(
                        NetworkCartItem(
                            1,
                            5,
                            NetworkProduct(1, "치킨", 10000, "http://example.com/chicken.jpg")
                        ),
                        NetworkCartItem(
                            2,
                            1,
                            NetworkProduct(2, "피자", 20000, "http://example.com/pizza.jpg")
                        )
                    )
                )
            }
        )
    }

    @Test
    fun `특정 장바구니 아이템의 수량을 변경하기를 요청했을 때 네트워크 통신이 원활하다면 응답 코드가 200인 응답을 받는다`() {
        val mockResponse = MockResponse()
            .setResponseCode(200)
        mockWebServer.enqueue(mockResponse)

        val anyCartItemId = 1L
        val anyQuantity = 10
        val response = sut.requestToUpdateQuantity(
            anyCartItemId,
            CartItemQuantityUpdateRequestBody(anyQuantity)
        ).execute()

        assertThat(response.code()).isEqualTo(200)
    }

    @Test
    fun `특정 장바구니 아이템을 삭제하기를 요청했을 때 네트워크 통신이 원활하다면 응답 코드가 204인 응답을 받는다`() {
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        val anyCartItemId = 1L
        val response = sut.requestToDelete(anyCartItemId).execute()

        assertThat(response.code()).isEqualTo(204)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }
}
