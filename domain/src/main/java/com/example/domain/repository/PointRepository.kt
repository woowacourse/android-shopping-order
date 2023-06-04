package com.example.domain.repository

import com.example.domain.model.PointInfo

interface PointRepository {
    fun getPoint(
        onSuccess: (PointInfo) -> Unit,
        onFailure: () -> Unit
    )
}
