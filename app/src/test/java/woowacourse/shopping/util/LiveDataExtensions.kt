package woowacourse.shopping.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/* Copyright 2019 Google LLC.	
   SPDX-License-Identifier: Apache-2.0 */
fun <T> LiveData<T>.getOrAwaitValue(
    time: Long = 2,
    timeUnit: TimeUnit = TimeUnit.SECONDS,
): T {
    var data: T? = null
    val latch = CountDownLatch(1)
    val observer =
        object : Observer<T> {
            override fun onChanged(value: T) {
                data = value
                latch.countDown()
                this@getOrAwaitValue.removeObserver(this)
            }
        }

    this.observeForever(observer)

    // Don't wait indefinitely if the LiveData is not set.
    if (!latch.await(time, timeUnit)) {
        throw TimeoutException("LiveData value was never set.")
    }

    @Suppress("UNCHECKED_CAST")
    return data as T
}

@Suppress("UNCHECKED_CAST")
inline fun <ITEM, reified VIEWMODEL> setUpTestLiveData(
    item: ITEM,
    fieldName: String,
    viewModel: VIEWMODEL,
) {
    val field = VIEWMODEL::class.java.getDeclaredField(fieldName)
    field.isAccessible = true
    val liveData = field.get(viewModel) as MutableLiveData<ITEM>
    liveData.postValue(item)
}
