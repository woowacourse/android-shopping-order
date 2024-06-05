package woowacourse.shopping.data.datasource.impl

import android.os.Handler
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.response.ResponseProductIdGetDto
import woowacourse.shopping.data.dto.response.ResponseProductsGetDto
import woowacourse.shopping.data.service.ApiResult
import woowacourse.shopping.data.service.ShoppingRetrofit

class HandlerProductDataSource {
    fun getProductsByOffset(page: Int, size: Int, handler: Handler) {
        val msg = handler.obtainMessage()

        ShoppingRetrofit.productService.getProductsByOffset(page = page, size = size)
            .enqueue(object : Callback<ResponseProductsGetDto> {
                override fun onResponse(
                    call: Call<ResponseProductsGetDto>,
                    response: Response<ResponseProductsGetDto>
                ) {
                    if (response.isSuccessful) {
                        msg.obj = ApiResult.Success(response.body())
                        handler.sendMessage(msg)
                    } else {
                        msg.obj = ApiResult.Fail
                        handler.sendMessage(msg)
                    }
                }

                override fun onFailure(call: Call<ResponseProductsGetDto>, e: Throwable) {
                    msg.obj = ApiResult.Error(e)
                    handler.sendMessage(msg)
                }

            })
    }

    fun getProductsById(id: Long, handler: Handler) {
        val msg = handler.obtainMessage()

        ShoppingRetrofit.productService.getProductsById(id = id)
            .enqueue(object : Callback<ResponseProductIdGetDto> {
                override fun onResponse(
                    call: Call<ResponseProductIdGetDto>,
                    response: Response<ResponseProductIdGetDto>
                ) {
                    if (response.isSuccessful) {
                        msg.obj = ApiResult.Success(response.body())
                        handler.sendMessage(msg)
                    } else {
                        msg.obj = ApiResult.Fail
                        handler.sendMessage(msg)
                    }
                }

                override fun onFailure(call: Call<ResponseProductIdGetDto>, e: Throwable) {
                    msg.obj = ApiResult.Error(e)
                    handler.sendMessage(msg)
                }

            })
    }


}
