package woowacourse.shopping.data

import retrofit2.Call
import retrofit2.http.GET
import woowacourse.shopping.data.network.response.BaseResponse

interface DummyApi {
    @GET("/success")
    fun getSuccess(): Call<DummyResponse>

    @GET("/null-body")
    fun getNullBody(): Call<DummyResponse>

    @GET("/unit-response")
    fun getUnit(): Call<Unit>

    @GET("/server-error")
    fun getServerError(): Call<DummyResponse>

    @GET("/domain-success")
    fun getDomainSuccess(): Call<DummyBaseResponse>

    @GET("/domain-null")
    fun getDomainNull(): Call<DummyBaseResponse>

    @GET("/location-success")
    fun getLocationResponse(): Call<DummyResponse>

    @GET("/location-missing")
    fun getLocationMissing(): Call<DummyResponse>
}

data class DummyDomain(val value: String)

data class DummyResponse(val name: String)

data class DummyBaseResponse(val domainValue: String) : BaseResponse<DummyDomain> {
    override fun toDomain(): DummyDomain = DummyDomain(domainValue)
}
