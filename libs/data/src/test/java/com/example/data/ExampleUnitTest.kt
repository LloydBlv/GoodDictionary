@file:OptIn(ExperimentalCoroutinesApi::class)

package com.example.data

import androidx.room.Room
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    @Before
    fun test() {
    }
    @Test
    fun addition_isCorrect() = runTest {
        val dictionaryLoader = DictionaryLoaderDefault(dispatcher = UnconfinedTestDispatcher())
//        dictionaryLoader.insertWords("", )
    }
}