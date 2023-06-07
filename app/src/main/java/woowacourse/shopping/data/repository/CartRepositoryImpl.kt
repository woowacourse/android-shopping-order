package woowacourse.shopping.data.repository

import com.example.domain.model.BaseResponse
import com.example.domain.model.CartProduct
import com.example.domain.repository.CartRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dataSource.remote.CartProductService
import woowacourse.shopping.data.model.dto.request.AddCartProductDto
import woowacourse.shopping.data.model.dto.request.ChangeCartProductCountDto
import woowacourse.shopping.data.model.dto.response.CartProductDto
import woowacourse.shopping.data.util.responseParseCustomError
import java.net.URI

class CartRepositoryImpl(
    private val cartProductService: CartProductService,
) : CartRepository {
    override fun fetchAll(
        callBack: (BaseResponse<List<CartProduct>>) -> Unit,
    ) {
        cartProductService.getAllCartProduct().enqueue(object : Callback<List<CartProductDto>> {
            override fun onResponse(
                call: Call<List<CartProductDto>>,
                response: Response<List<CartProductDto>>
            ) {
                if (response.isSuccessful.not())
                    return responseParseCustomError(response.errorBody(), callBack)

                val cartProductDtos = response.body() ?: emptyList()
                val cartProducts = cartProductDtos.map(CartProductDto::toDomain)
                callBack(BaseResponse.SUCCESS(cartProducts))
            }

            override fun onFailure(call: Call<List<CartProductDto>>, t: Throwable) {
                callBack(BaseResponse.NETWORK_ERROR())
            }
        })
    }

    override fun fetchSize(callBack: (cartCount: BaseResponse<Int>) -> Unit) {
        cartProductService.getAllCartProduct().enqueue(object : Callback<List<CartProductDto>> {
            override fun onResponse(
                call: Call<List<CartProductDto>>,
                response: Response<List<CartProductDto>>
            ) {
                if (response.isSuccessful.not())
                    return responseParseCustomError(response.errorBody(), callBack)

                val cartProductDtos = response.body() ?: emptyList()
                val cartProducts = cartProductDtos.map(CartProductDto::toDomain)
                callBack(BaseResponse.SUCCESS(cartProducts.sumOf { it.count }))
            }

            override fun onFailure(call: Call<List<CartProductDto>>, t: Throwable) {
                callBack(BaseResponse.NETWORK_ERROR())
            }
        })
    }

    override fun addCartProduct(
        productId: Long,
        callBack: (cartId: BaseResponse<Long>) -> Unit,
    ) {
        cartProductService.addCartProduct(AddCartProductDto(productId))
            .enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful.not())
                        return responseParseCustomError(response.errorBody(), callBack)

                    val responseHeader = response.headers()["Location"]
                        ?: return responseParseCustomError(response.errorBody(), callBack)

                    val cartId = URI(responseHeader).path.substringAfterLast("/").toLong()
                    callBack(BaseResponse.SUCCESS(cartId))
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callBack(BaseResponse.NETWORK_ERROR())
                }
            })
    }

    override fun changeCartProductCount(
        cartId: Long,
        count: Int,
        callBack: (cartId: BaseResponse<Long>) -> Unit,
    ) {
        cartProductService.changeCartProduct(cartId, ChangeCartProductCountDto(count)).enqueue(
            object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    if (response.isSuccessful.not())
                        return responseParseCustomError(response.errorBody(), callBack)

                    callBack(BaseResponse.SUCCESS(cartId))
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    callBack(BaseResponse.NETWORK_ERROR())
                }
            }
        )
    }

    override fun deleteCartProduct(
        cartId: Long,
        callBack: (cartId: BaseResponse<Long>) -> Unit,
    ) {
        cartProductService.deleteCartProduct(cartId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                if (response.isSuccessful.not())
                    return responseParseCustomError(response.errorBody(), callBack)

                callBack(BaseResponse.SUCCESS(cartId))
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                callBack(BaseResponse.NETWORK_ERROR())
            }
        })
    }
}
