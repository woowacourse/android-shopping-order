package woowacourse.shopping.data.dto.mapper

interface Mapper<T, R> {
    fun T.toEntity(): R

    fun R.toDomain(): T
}