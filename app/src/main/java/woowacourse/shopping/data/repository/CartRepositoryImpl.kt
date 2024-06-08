package woowacourse.shopping.data.repository

import woowacourse.shopping.data.datasource.RemoteCartDataSource
import woowacourse.shopping.data.model.CartItem
import woowacourse.shopping.data.model.CartItemRequestBody
import woowacourse.shopping.data.model.CartQuantity
import woowacourse.shopping.data.model.toCartData
import woowacourse.shopping.data.model.toCartDomain
import woowacourse.shopping.data.model.toCartItemDomain
import woowacourse.shopping.domain.model.CartData
import woowacourse.shopping.domain.model.CartDomain
import woowacourse.shopping.domain.model.CartItemDomain
import woowacourse.shopping.domain.repository.CartRepository

class CartRepositoryImpl(
    private val remoteCartDataSource: RemoteCartDataSource,
) : CartRepository {
    override suspend fun getCartItems(page: Int, size: Int, sort: String): Result<CartDomain> {
        return runCatching {
            remoteCartDataSource.getCartItems(page, size, sort).toCartDomain()
        }
    }

    override suspend fun getEntireCartData(): Result<List<CartData>> {
        return runCatching {
            val totalCartQuantity = remoteCartDataSource.getCartTotalQuantity().quantity
            remoteCartDataSource.getCartItems(
                PAGE_CART_ITEMS,
                totalCartQuantity,
                SORT_CART_ITEMS
            ).cartItems.map(CartItem::toCartData)
        }
    }

    override suspend fun getEntireCartItems(): Result<List<CartItemDomain>> {
        return runCatching {
            val totalCartQuantity = remoteCartDataSource.getCartTotalQuantity().quantity
            remoteCartDataSource.getCartItems(
                PAGE_CART_ITEMS,
                totalCartQuantity,
                SORT_CART_ITEMS
            ).cartItems.map(CartItem::toCartItemDomain)
        }
    }

    override suspend fun getEntireCartItemsForCart(): Result<CartDomain> {
        return runCatching {
            val totalCartQuantity = remoteCartDataSource.getCartTotalQuantity().quantity
            remoteCartDataSource.getCartItems(
                PAGE_CART_ITEMS,
                totalCartQuantity,
                SORT_CART_ITEMS
            ).toCartDomain()
        }
    }

    override suspend fun addCartItem(productId: Int, quantity: Int): Result<Unit> {
        return runCatching {
            val requestBody = CartItemRequestBody(productId, quantity)
            remoteCartDataSource.addCartItem(requestBody)
        }
    }

    override suspend fun deleteCartItem(cartItemId: Int): Result<Unit> {
        return runCatching {
            remoteCartDataSource.deleteCartItem(cartItemId)
        }
    }

    override suspend fun updateCartItem(cartItemId: Int, quantity: Int): Result<Unit> {
        return runCatching {
            remoteCartDataSource.updateCartItem(cartItemId, CartQuantity(quantity))
        }
    }

    override suspend fun getCartTotalQuantity(): Result<Int> {
        return runCatching {
            remoteCartDataSource.getCartTotalQuantity().quantity
        }
    }

    companion object {
        private const val PAGE_CART_ITEMS = 0
        private const val SORT_CART_ITEMS = "asc"
    }
    //    override suspend fun getCartItems(
//        page: Int,
//        size: Int,
//        sort: String,
////        onSuccess: (CartDomain) -> Unit,
////        onFailure: (Throwable) -> Unit,
//    ): CartDomain {
//        remoteCartDataSource.getCartItems(page, size, sort).enqueue(
//            object : Callback<CartResponse> {
//                override fun onResponse(
//                    call: Call<CartResponse>,
//                    response: Response<CartResponse>,
//                ) {
//                    val cartDomain = response.body()?.toCartDomain() ?: throw HttpException(response)
//                    onSuccess(cartDomain)
//                }
//
//                override fun onFailure(
//                    call: Call<CartResponse>,
//                    t: Throwable,
//                ) {
//                    onFailure(t)
//                }
//            },
//        )
////        thread {
////            runCatching {
////                val response = remoteCartDataSource.getCartItems(page, size, sort).execute()
////                response.body()?.toCartDomain() ?: throw HttpException(response)
////            }.onSuccess(onSuccess).onFailure(onFailure)
////        }
//    }
//
//    override fun addCartItem(
//        productId: Int,
//        quantity: Int,
//        onSuccess: () -> Unit,
//        onFailure: (Throwable) -> Unit,
//    ) {
//        remoteCartDataSource.addCartItem(CartItemRequestBody(productId, quantity)).enqueue(
//            object : Callback<Unit> {
//                override fun onResponse(
//                    call: Call<Unit>,
//                    response: Response<Unit>,
//                ) {
//                    if (response.code() != 201) throw HttpException(response)
//                    onSuccess()
//                }
//
//                override fun onFailure(
//                    call: Call<Unit>,
//                    t: Throwable,
//                ) {
//                    onFailure(t)
//                }
//            },
//        )
////        thread {
////            runCatching {
////                val response =
////                    remoteCartDataSource.addCartItem(CartItemRequestBody(productId, quantity))
////                        .execute()
////                if (response.code() != 201) throw HttpException(response)
////            }.onSuccess {
////                onSuccess()
////            }.onFailure(onFailure)
////        }
//    }
//
//    override fun deleteCartItem(
//        cartItemId: Int,
//        onSuccess: () -> Unit,
//        onFailure: (Throwable) -> Unit,
//    ) {
//        remoteCartDataSource.deleteCartItem(cartItemId).enqueue(
//            object : Callback<Unit> {
//                override fun onResponse(
//                    call: Call<Unit>,
//                    response: Response<Unit>,
//                ) {
//                    if (response.code() != 204) throw HttpException(response)
//                    onSuccess()
//                }
//
//                override fun onFailure(
//                    call: Call<Unit>,
//                    t: Throwable,
//                ) {
//                    onFailure(t)
//                }
//            },
//        )
//    }
//
//    override fun updateCartItem(
//        cartItemId: Int,
//        quantity: Int,
//        onSuccess: () -> Unit,
//        onFailure: (Throwable) -> Unit,
//    ) {
//        remoteCartDataSource.updateCartItem(cartItemId, CartQuantity(quantity)).enqueue(
//            object : Callback<Unit> {
//                override fun onResponse(
//                    call: Call<Unit>,
//                    response: Response<Unit>,
//                ) {
//                    if (response.code() != 200) throw HttpException(response)
//                    onSuccess()
//                }
//
//                override fun onFailure(
//                    call: Call<Unit>,
//                    t: Throwable,
//                ) {
//                    onFailure(t)
//                }
//            },
//        )
////        thread {
////            runCatching {
////                val response =
////                    remoteCartDataSource.updateCartItem(cartItemId, CartQuantity(quantity))
////                        .execute()
////                if (response.code() != 200) throw HttpException(response)
////            }.onSuccess {
////                onSuccess()
////            }.onFailure(onFailure)
////        }
//    }
//
//    override fun getCartTotalQuantity(
//        onSuccess: (Int) -> Unit,
//        onFailure: (Throwable) -> Unit,
//    ) {
//        remoteCartDataSource.getCartTotalQuantity().enqueue(
//            object : Callback<CartQuantity> {
//                override fun onResponse(
//                    call: Call<CartQuantity>,
//                    response: Response<CartQuantity>,
//                ) {
//                    val quantity = response.body()?.quantity ?: throw HttpException(response)
//                    onSuccess(quantity)
//                }
//
//                override fun onFailure(
//                    call: Call<CartQuantity>,
//                    t: Throwable,
//                ) {
//                    onFailure(t)
//                }
//            },
//        )
////        thread {
////            runCatching {
////                val response = remoteCartDataSource.getCartTotalQuantity().execute()
////                response.body()?.quantity ?: throw HttpException(response)
////            }.onSuccess { quantity ->
////                onSuccess(quantity)

////            }.onFailure(onFailure)
////        }
//    }
}
