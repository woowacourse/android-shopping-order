package com.example.data.datasource.local.entity.recentproduct

import com.example.data.datasource.local.entity.product.toProduct
import com.example.data.datasource.local.entity.product.toProductEntity
import com.example.domain.model.RecentProduct

fun RecentProductEntity.toRecentProduct(): RecentProduct =
    RecentProduct(
        id = id,
        product = product.toProduct(),
        seenDateTime = seenDateTime,
    )

fun RecentProduct.toRecentProductEntity(): RecentProductEntity =
    RecentProductEntity(
        product = product.toProductEntity(),
        seenDateTime = seenDateTime,
    )

fun List<RecentProductEntity>.toRecentProducts(): List<RecentProduct> = map { it.toRecentProduct() }

fun List<RecentProduct>.toRecentProductsEntity(): List<RecentProductEntity> = map { it.toRecentProductEntity() }
