package com.example.dictionarysync

import androidx.work.WorkInfo
import androidx.work.workDataOf
import app.cash.turbine.test
import assertk.all
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import assertk.assertions.prop
import com.example.dictionarysync.DictionarySyncStateWatcher.State
import com.example.testing.DataSyncStatusFake
import com.example.testing.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class StateWatcherTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun testWatcherState() = runTest {
    val statusProviderFake = DataSyncStatusFake()

    val stateWatcher = DictionarySyncStateWatcherDefault(
      statusProvider = statusProviderFake,
    )

    stateWatcher.watch().test {
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

      statusProviderFake.emit(
        WorkInfo.State.FAILED,
        outputData = workDataOf(DataSyncWorker.FAILURE_MESSAGE_DATA to "Some failure message"),
      )
      assertThat(awaitItem()).all {
        isInstanceOf(State.Failure::class)
        transform { it as State.Failure }.prop(State.Failure::message).isEqualTo("Some failure message")
      }

      ensureAllEventsConsumed()
    }
  }
}
