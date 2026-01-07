package apero.quanta.picai.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import apero.quanta.picai.domain.model.ThemeMode
import apero.quanta.picai.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

    private val _viewState = MutableStateFlow(SettingState())
    val viewState: StateFlow<SettingState> = _viewState.asStateFlow()

    init {
        viewModelScope.launch {
            settingsRepository.getThemeMode().collect { mode ->
                _viewState.update { it.copy(themeMode = mode) }
            }
        }
        viewModelScope.launch {
            settingsRepository.isDynamicColorEnabled().collect { enabled ->
                _viewState.update { it.copy(isDynamicColorEnabled = enabled) }
            }
        }
    }

    fun processIntent(intent: SettingIntent) {
        when (intent) {
            is SettingIntent.ChangeThemeMode -> {
                viewModelScope.launch {
                    settingsRepository.setThemeMode(intent.mode)
                }
            }
            is SettingIntent.ToggleDynamicColor -> {
                viewModelScope.launch {
                    settingsRepository.setDynamicColorEnabled(intent.enabled)
                }
            }
        }
    }
}
