package woowacourse.shopping.util.collection

class DistinctList<E> : ArrayList<E>() {
    override fun addAll(elements: Collection<E>): Boolean {
        val temp = mutableListOf<E>()
        elements.forEach { elements ->
            if (!contains(elements)) {
                temp.add(elements)
            }
        }
        return super.addAll(temp)
    }

    override fun add(element: E): Boolean {
        if (!contains(element)) {
            return super.add(element)
        }
        return false
    }
}
