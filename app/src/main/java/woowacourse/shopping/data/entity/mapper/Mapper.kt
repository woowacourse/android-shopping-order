package woowacourse.shopping.data.entity.mapper

interface Mapper<T, R> {
    fun T.toEntity(): R

    fun R.toDomain(): T
}