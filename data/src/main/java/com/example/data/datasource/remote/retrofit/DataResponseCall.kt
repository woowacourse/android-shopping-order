package com.example.data.datasource.remote.retrofit

import com.example.domain.datasource.DataResponse
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class DataResponseCall<T : Any>(
    private val delegate: Call<T>,
) : Call<DataResponse<T>> {
    override fun clone(): Call<DataResponse<T>> = DataResponseCall(delegate)

    override fun execute(): Response<DataResponse<T>> {
        val response = delegate.execute()
        return if (response.isSuccessful) {
            Response.success(DataResponse.Success(response.body()))
        } else {
            Response.success(
                DataResponse.Failure(
                    response.code(),
                    response.errorBody()?.string(),
                ),
            )
        }
    }

    override fun enqueue(callback: Callback<DataResponse<T>>) {
        delegate.enqueue(
            object : Callback<T> {
                override fun onResponse(
                    call: Call<T>,
                    response: Response<T>,
                ) {
                    if (response.isSuccessful) {
                        callback.onResponse(
                            this@DataResponseCall,
                            Response.success(DataResponse.Success(response.body())),
                        )
                    } else {
                        callback.onResponse(
                            this@DataResponseCall,
                            Response.success(
                                DataResponse.Failure(
                                    response.code(),
                                    response.errorBody()?.string(),
                                ),
                            ),
                        )
                    }
                }

                override fun onFailure(
                    call: Call<T>,
                    t: Throwable,
                ) {
                    val response =
                        when (t) {
                            is IOException -> DataResponse.Error(t)
                            else -> DataResponse.Unexpected(t)
                        }
                    callback.onResponse(
                        this@DataResponseCall,
                        Response.success(response),
                    )
                }
            },
        )
    }

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}
