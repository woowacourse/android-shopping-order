package com.example.data.repository

import com.example.domain.datasource.CartDataSource
import com.example.domain.datasource.DataResponse
import com.example.domain.model.CartItem
import com.example.domain.model.Quantity
import com.example.domain.repository.CartRepository

class DefaultCartRepository(
    private val dataSource: CartDataSource,
) : CartRepository {
    override suspend fun findAll(): DataResponse<List<CartItem>> = dataSource.findAll()

    override suspend fun postCartItem(
        productId: Int,
        quantity: Quantity,
    ): DataResponse<Unit> = dataSource.postCartItem(productId, quantity)

    override suspend fun increaseQuantity(productId: Int) = dataSource.increaseQuantity(productId)

    override suspend fun decreaseQuantity(productId: Int) = dataSource.decreaseQuantity(productId)

    override suspend fun changeQuantity(
        productId: Int,
        quantity: Quantity,
    ) = dataSource.changeQuantity(productId, quantity)

    override suspend fun deleteCartItem(cartItemId: Int) = dataSource.deleteCartItem(cartItemId)

    override suspend fun find(productId: Int): DataResponse<CartItem> = dataSource.findByProductId(productId)

    override suspend fun findRange(
        page: Int,
        pageSize: Int,
    ): DataResponse<List<CartItem>> = dataSource.findRange(page, pageSize)

    override suspend fun totalCartItemCount(): DataResponse<Int> = dataSource.totalCartItemCount()
}
