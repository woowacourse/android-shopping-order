package woowacourse.shopping.data

import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiCallbackHandlerTest {
    private lateinit var mockWebServer: MockWebServer
    private lateinit var api: DummyApi
    private lateinit var handler: ApiCallbackHandler

    @BeforeEach
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit =
            Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        api = retrofit.create(DummyApi::class.java)
        handler = ApiCallbackHandler()
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `enqueueWithResult - 정상적인 응답이 오면 Result_success를 반환한다`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{ "name": "world" }"""),
        )

        var result: Result<DummyResponse>? = null

        handler.enqueueWithResult(api.getSuccess()) {
            result = it
        }

        Thread.sleep(100)

        assertTrue(result!!.isSuccess)
        assertEquals("world", result.getOrNull()?.name)
    }

    @Test
    fun `enqueueWithResult - API의 반환 타입이 Unit이고 바디가 null 이면 Unit을 반환한다`() {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(""),
        )

        // When
        var result: Result<Unit>? = null
        handler.enqueueWithResult(api.getUnit()) {
            result = it
        }

        // Then
        Thread.sleep(100)
        assertTrue(result!!.isSuccess)
        assertEquals(Unit, result.getOrNull())
    }

    @Test
    fun `enqueueWithResult - HTTP 오류가 발생하면 HttpException을 반환한다`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Server error"),
        )

        var result: Result<DummyResponse>? = null

        handler.enqueueWithResult(api.getServerError()) {
            result = it
        }

        Thread.sleep(100)

        assertTrue(result!!.isFailure)
        assertTrue(result.exceptionOrNull() is HttpException)
    }

    @Test
    fun `enqueueWithDomainTransform - 응답이 성공하면 toDomain으로 변환된 값을 Result_success로 반환한다`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{ "domainValue": "expected" }"""),
        )
        var result: Result<DummyDomain>? = null

        handler.enqueueWithDomainTransform(api.getDomainSuccess()) {
            result = it
        }

        Thread.sleep(100)
        assertTrue(result!!.isSuccess)
        assertEquals("expected", result.getOrNull()?.value)
    }

    @Test
    fun `enqueueWithDomainTransform - HTTP 오류 응답이면 HttpException를 반환한다`() {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("{ \"error\": \"Not found\" }"),
        )
        var result: Result<DummyDomain>? = null

        // When
        handler.enqueueWithDomainTransform(api.getDomainSuccess()) {
            result = it
        }

        Thread.sleep(100)

        // Then
        assertTrue(result!!.isFailure)
        assertTrue(result.exceptionOrNull() is HttpException)
        val exception = result.exceptionOrNull() as HttpException
        assertEquals(404, exception.code())
    }

    @Test
    fun `enqueueWithDomainTransform - 응답이 성공하지만 JSON 파싱 실패시 Result_failure 반환한다`() {
        // Given
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("{ invalid json }"),
        )
        var result: Result<DummyDomain>? = null

        // When
        handler.enqueueWithDomainTransform(api.getDomainSuccess()) {
            result = it
        }

        Thread.sleep(100)

        // Then
        assertTrue(result!!.isFailure)
        assertTrue(result.exceptionOrNull() is Exception)
    }

    @Test
    fun `enqueueWithDomainTransform - 네트워크 실패 시 IOException을 반환한다`() {
        // Given
        mockWebServer.shutdown()
        var result: Result<DummyDomain>? = null

        // When
        handler.enqueueWithDomainTransform(api.getDomainSuccess()) {
            result = it
        }

        Thread.sleep(100)

        // Then
        assertTrue(result!!.isFailure)
        assertTrue(result.exceptionOrNull() is java.io.IOException)
    }
}
