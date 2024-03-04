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
import com.example.dictionarysync.DataSyncWorker
import com.example.dictionarysync.DictionarySyncStateWatcherDefault
import com.example.splash.SplashViewModel.UiState
import com.example.testing.DataSyncStatusFake
import com.example.testing.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class SplashViewModelTest {

  @get:Rule
  val mainDispatcherRule = MainDispatcherRule()

  @Test
  fun `test splash state updates according to data sync watcher`() = runTest {
    val statusProviderFake = DataSyncStatusFake()

    val stateWatcher = DictionarySyncStateWatcherDefault(
      statusProvider = statusProviderFake,
    )
    val viewModel = SplashViewModel(stateWatcher = stateWatcher)

    viewModel.state.test {
      assertThat(awaitItem()).isInstanceOf(UiState.Unknown::class)

      statusProviderFake.emit(WorkInfo.State.BLOCKED)
      assertThat(awaitItem()).isInstanceOf(UiState.Blocked::class)

      statusProviderFake.emit(WorkInfo.State.CANCELLED)
      assertThat(awaitItem()).isInstanceOf(UiState.Cancelled::class)

      statusProviderFake.emit(WorkInfo.State.ENQUEUED)
      assertThat(awaitItem()).isInstanceOf(UiState.Loading::class)

      statusProviderFake.emit(WorkInfo.State.RUNNING)
      assertThat(awaitItem()).all {
        isInstanceOf(UiState.Progress::class)
        transform { it as UiState.Progress }.prop(UiState.Progress::percent).isEqualTo(0)
      }

      statusProviderFake.emit(WorkInfo.State.RUNNING, progress = 50)
      assertThat(awaitItem()).all {
        isInstanceOf(UiState.Progress::class)
        transform { it as UiState.Progress }.prop(UiState.Progress::percent).isEqualTo(50)
      }

      statusProviderFake.emit(WorkInfo.State.RUNNING, progress = 99)
      assertThat(awaitItem()).all {
        isInstanceOf(UiState.Progress::class)
        transform { it as UiState.Progress }.prop(UiState.Progress::percent).isEqualTo(99)
      }

      statusProviderFake.emit(WorkInfo.State.FAILED)
      assertThat(awaitItem()).all {
        isInstanceOf(UiState.Failure::class)
        transform { it as UiState.Failure }.prop(UiState.Failure::message).isNull()
      }

      statusProviderFake.emit(
        WorkInfo.State.FAILED,
        outputData = workDataOf(DataSyncWorker.FAILURE_MESSAGE_DATA to "Some failure message"),
      )
      assertThat(awaitItem()).all {
        isInstanceOf(UiState.Failure::class)
        transform { it as UiState.Failure }.prop(UiState.Failure::message).isEqualTo("Some failure message")
      }

      ensureAllEventsConsumed()
    }
  }
}
