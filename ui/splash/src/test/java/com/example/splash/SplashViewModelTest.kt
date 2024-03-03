package com.example.splash

import androidx.work.WorkInfo
import androidx.work.workDataOf
import app.cash.turbine.test
import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.prop
import com.example.dictionary_sync.DataSyncWorker
import com.example.dictionary_sync.DictionarySyncStateWatcher.State
import com.example.dictionary_sync.DictionarySyncStateWatcherDefault
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test


class SplashViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun testSplashViewModelState() = runTest {
        val statusProviderFake = DataSyncStatusFake()

        val stateWatcher = DictionarySyncStateWatcherDefault(
            statusProvider = statusProviderFake
        )
        val viewModel = SplashViewModel(stateWatcher = stateWatcher)

        viewModel.state.test {
            assertThat(awaitItem()).isInstanceOf(State.Unknown::class)

            statusProviderFake.emit(WorkInfo.State.BLOCKED)
            assertThat(awaitItem()).isInstanceOf(State.Blocked::class)

            statusProviderFake.emit(WorkInfo.State.CANCELLED)
            assertThat(awaitItem()).isInstanceOf(State.Cancelled::class)


            statusProviderFake.emit(WorkInfo.State.ENQUEUED)
            assertThat(awaitItem()).isInstanceOf(State.Loading::class)

            statusProviderFake.emit(WorkInfo.State.RUNNING)
            assertThat(awaitItem()).all {
                isInstanceOf(State.Progress::class)
                transform { it as State.Progress }.prop(State.Progress::percent).isEqualTo(0)
            }

            statusProviderFake.emit(WorkInfo.State.RUNNING, progress = 50)
            assertThat(awaitItem()).all {
                isInstanceOf(State.Progress::class)
                transform { it as State.Progress }.prop(State.Progress::percent).isEqualTo(50)
            }

            statusProviderFake.emit(WorkInfo.State.RUNNING, progress = 99)
            assertThat(awaitItem()).all {
                isInstanceOf(State.Progress::class)
                transform { it as State.Progress }.prop(State.Progress::percent).isEqualTo(99)
            }

            statusProviderFake.emit(WorkInfo.State.FAILED)
            assertThat(awaitItem()).all {
                isInstanceOf(State.Failure::class)
                transform { it as State.Failure }.prop(State.Failure::message).isNull()
            }

            statusProviderFake.emit(WorkInfo.State.FAILED,
                outputData = workDataOf(DataSyncWorker.FAILURE_MESSAGE_DATA to "Some failure message")
            )
            assertThat(awaitItem()).all {
                isInstanceOf(State.Failure::class)
                transform { it as State.Failure }.prop(State.Failure::message).isEqualTo("Some failure message")
            }

            ensureAllEventsConsumed()
        }
    }
}