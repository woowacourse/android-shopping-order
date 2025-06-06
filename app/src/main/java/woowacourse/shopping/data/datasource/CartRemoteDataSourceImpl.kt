package woowacourse.shopping.data.datasource

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import woowacourse.shopping.data.dto.cartitem.ProductResponse
import woowacourse.shopping.data.dto.cartitem.Quantity
import woowacourse.shopping.data.dto.cartitem.UpdateCartItemRequest
import woowacourse.shopping.data.service.CartItemService

class CartRemoteDataSourceImpl(
    private val cartItemService: CartItemService,
) : CartRemoteDataSource {
    override fun insertProduct(
        productId: Long,
        quantity: Int,
        onSuccess: (Int) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        cartItemService
            .postCartItem(
                UpdateCartItemRequest(
                    productId = productId,
                    quantity = quantity,
                ),
            ).enqueue(
                object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void?>,
                        response: Response<Void?>,
                    ) {
                        if (response.isSuccessful) {
                            val locationHeader = response.headers()["location"]
                            val id = locationHeader?.substringAfterLast("/")?.toIntOrNull()
                            if (id == null) {
                                onFailure(Throwable("CartItemId가 없습니다."))
                            } else {
                                onSuccess(id)
                            }
                        } else {
                            onFailure(Throwable("삭제 실패: ${response.code()}"))
                        }
                    }

                    override fun onFailure(
                        call: Call<Void?>,
                        t: Throwable,
                    ) {
                        onFailure(t)
                    }
                },
            )
    }

    override fun deleteProduct(
        productId: Long,
        onSuccess: (Unit) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        cartItemService
            .deleteCartItem(productId)
            .enqueue(
                object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void?>,
                        response: Response<Void?>,
                    ) {
                        if (response.isSuccessful) {
                            onSuccess(Unit)
                        } else {
                            onFailure(Throwable("삭제 실패: ${response.code()}"))
                        }
                    }

                    override fun onFailure(
                        call: Call<Void?>,
                        t: Throwable,
                    ) {
                        onFailure(t)
                    }
                },
            )
    }

    override fun fetchProducts(
        page: Int,
        size: Int,
        onSuccess: (ProductResponse) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        cartItemService
            .requestCartItems(
                page = page,
                size = size,
            ).enqueue(
                object : Callback<ProductResponse> {
                    override fun onResponse(
                        call: Call<ProductResponse?>,
                        response: Response<ProductResponse?>,
                    ) {
                        response.body()?.let { onSuccess(it) }
                            ?: onFailure(Throwable("상품을 가져오기를 실패했습니다."))
                    }

                    override fun onFailure(
                        call: Call<ProductResponse?>,
                        t: Throwable,
                    ) {
                        onFailure(t)
                    }
                },
            )
    }

    override fun updateProduct(
        cartItemId: Long,
        quantity: Int,
        onSuccess: (Unit) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        cartItemService
            .patchCartItemQuantity(
                cartItemId = cartItemId,
                quantity = Quantity(quantity),
            ).enqueue(
                object : Callback<Void> {
                    override fun onResponse(
                        call: Call<Void?>,
                        response: Response<Void?>,
                    ) {
                        if (response.isSuccessful) {
                            onSuccess(Unit)
                        } else {
                            onFailure(Throwable("수정 실패: ${response.code()}"))
                        }
                    }

                    override fun onFailure(
                        call: Call<Void?>,
                        t: Throwable,
                    ) {
                        onFailure(t)
                    }
                },
            )
    }

    override fun fetchCartTotalElements(
        onSuccess: (Long) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        cartItemService
            .requestCartItems(
                page = null,
                size = null,
            ).enqueue(
                object : Callback<ProductResponse> {
                    override fun onResponse(
                        call: Call<ProductResponse?>,
                        response: Response<ProductResponse?>,
                    ) {
                        response.body()?.let { onSuccess(it.totalElements) }
                            ?: onFailure(Throwable("totalElements가 없습니다."))
                    }

                    override fun onFailure(
                        call: Call<ProductResponse?>,
                        t: Throwable,
                    ) {
                        onFailure(t)
                    }
                },
            )
    }

    override fun fetchCartItemsCount(
        onSuccess: (Int) -> Unit,
        onFailure: (Throwable) -> Unit,
    ) {
        cartItemService
            .getCartItemsCount()
            .enqueue(
                object : Callback<Quantity> {
                    override fun onResponse(
                        call: Call<Quantity?>,
                        response: Response<Quantity?>,
                    ) {
                        response.body()?.let { onSuccess(it.value) }
                            ?: onFailure(Throwable("장바구니 수량 가져오는데 실패했습니다."))
                    }

                    override fun onFailure(
                        call: Call<Quantity?>,
                        t: Throwable,
                    ) {
                        onFailure(t)
                    }
                },
            )
    }
}
