package woowacourse.shopping.data.remote.paging

fun <T : Any> LoadResult.Page<T>.mergeWith(other: LoadResult.Page<T>): LoadResult.Page<T> {
    val mergedData = this.data + other.data
    return LoadResult.Page(
        offset = other.offset,
        last = other.last,
        data = mergedData
    )
}