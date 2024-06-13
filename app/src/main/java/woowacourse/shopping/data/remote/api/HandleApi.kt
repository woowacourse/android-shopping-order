package woowacourse.shopping.data.remote.api

import retrofit2.HttpException
import retrofit2.Response
import woowacourse.shopping.data.remote.dto.response.CouponDto
import woowacourse.shopping.domain.result.DataError
import woowacourse.shopping.domain.result.Result

suspend inline fun <reified T> handleApi(crossinline execute: suspend () -> Response<T>): Result<T, DataError> =
    try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            Result.Success(body)
        } else if (response.isSuccessful && body == null && T::class == Unit::class) {
            Result.Success(Unit as T)
        } else if (response.code() == 401 || response.code() == 403) {
            Result.Error(DataError.Network.NO_INTERNET)
        } else if (response.code() == 404) {
            Result.Error(DataError.NotFound)
        } else if (response.code() == 500) {
            Result.Error(DataError.Network.SERVER)
        } else {
           Result.Error(DataError.UNKNOWN)
        }
    } catch (e: HttpException) {
        Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: Throwable) {
        throw e
    }


suspend inline fun <reified T> handleCouponApi(crossinline execute: suspend () -> Response<T>): Result<T, DataError> =
    try {
        val response = execute()
        val body = response.body()
        if (response.isSuccessful && body != null) {
            Result.Success(body)
        } else if (response.isSuccessful && body == null && T::class == Unit::class) {
            Result.Success(Unit as T)
        } else if (response.code() == 401 || response.code() == 403) {
            Result.Error(DataError.Network.NO_INTERNET)
        } else if (response.code() == 404) {
            Result.Error(DataError.NotFound)
        } else if (response.code() == 500) {
            Result.Error(DataError.Network.SERVER)
        } else {
            Result.Error(DataError.UNKNOWN)
        }
    } catch (e: HttpException) {
        Result.Error(DataError.Network.NO_INTERNET)
    } catch (e: Throwable) {
        throw e
    }

