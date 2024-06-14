package woowacourse.shopping.presentation.util

class MutableSingleLiveData<T> : SingleLiveData<T>() {
    public override fun postValue(value: T) {
        super.postValue(value)
    }

    public override fun setValue(value: T) {
        super.setValue(value)
    }
}
