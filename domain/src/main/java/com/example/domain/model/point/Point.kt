package com.example.domain.model.point

@JvmInline
value class Point(val value: Int) {

    init {
        require(value >= POINT_LOWER_LIMIT) { ERROR_POINT_RANGE }
    }

    companion object {
        private const val POINT_LOWER_LIMIT = 0
        private const val ERROR_POINT_RANGE = "[ERROR] 포인트는 0P 이상이어야 합니다"
    }
}
