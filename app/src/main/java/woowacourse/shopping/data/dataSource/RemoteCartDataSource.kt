package woowacourse.shopping.data.dataSource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.CartProductDto
import woowacourse.shopping.data.service.RetrofitCartService
import woowacourse.shopping.data.service.RetrofitClient
import woowacourse.shopping.data.utils.createResponseCallback

class RemoteCartDataSource(
    private val service: RetrofitCartService = RetrofitClient.getInstance().retrofitCartService,
) : CartDataSource {

    override fun getAll(
        onSuccess: (List<CartProductDto>) -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        service.getCarts().enqueue(
            createResponseCallback<List<CartProductDto>>(
                onSuccess = { onSuccess(it) },
                onFailure = { onFailure(it) },
            ),
        )
    }

    override fun postItem(itemId: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        service.postCart(itemId).enqueue(
            createResponseCallback(
                onSuccess = { onSuccess() },
                onFailure = { onFailure(it) },
            ),
        )
    }

    override fun patchItemQuantity(
        itemId: Int,
        quantity: Int,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit,
    ) {
        service.patchCart(itemId, quantity).enqueue(
            createResponseCallback(
                onSuccess = { onSuccess() },
                onFailure = { onFailure(it) },
            ),
        )
    }

    override fun deleteItem(itemId: Int, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        service.deleteCart(itemId).enqueue(
            object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure(IllegalArgumentException("삭제에 실패했습니다."))
                    }
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    onFailure(Exception(t.message))
                }
            },
        )
    }
}
