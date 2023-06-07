package com.example.domain.repository

import com.example.domain.model.BaseResponse
import com.example.domain.model.CartProduct

interface CartRepository {
    fun fetchAll(
        callBack: (BaseResponse<List<CartProduct>>) -> Unit,
    )

    fun fetchSize(
        callBack: (cartCount: BaseResponse<Int>) -> Unit,
    )

    fun addCartProduct(
        productId: Long,
        callBack: (cartId: BaseResponse<Long>) -> Unit,
    )

    fun changeCartProductCount(
        cartId: Long,
        count: Int,
        callBack: (cartId: BaseResponse<Long>) -> Unit,
    )

    fun deleteCartProduct(
        cartId: Long,
        callBack: (cartId: BaseResponse<Long>) -> Unit,
    )
}
