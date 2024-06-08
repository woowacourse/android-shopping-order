package com.example.data.datasource.local.room.entity.cart

import com.example.domain.model.CartItem

fun CartItemEntity.toCartItem() = CartItem(id, productId, quantity)

fun List<CartItemEntity>.toCartItems() = map { it.toCartItem() }

fun CartItem.toCartItemEntity() = CartItemEntity(id, productId, quantity)

fun List<CartItem>.toCartItemEntities() = map { it.toCartItemEntity() }
