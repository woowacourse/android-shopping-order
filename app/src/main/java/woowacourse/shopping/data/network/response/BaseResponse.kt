package woowacourse.shopping.data.network.response

interface BaseResponse<D> {
    fun toDomain(): D
}
