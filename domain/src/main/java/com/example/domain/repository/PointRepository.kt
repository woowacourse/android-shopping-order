package com.example.domain.repository

import com.example.domain.model.FailureInfo
import com.example.domain.model.Point

interface PointRepository {
    fun getPoint(
        onSuccess: (Point) -> Unit,
        onFailure: (FailureInfo) -> Unit
    )
}
