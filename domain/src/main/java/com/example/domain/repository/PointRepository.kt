package com.example.domain.repository

import com.example.domain.model.point.PointInfo

interface PointRepository {
    fun getPoint(
        onSuccess: (PointInfo) -> Unit,
        onFailure: () -> Unit
    )
}
