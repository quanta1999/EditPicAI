package apero.quanta.picai

import androidx.lifecycle.ViewModel
import apero.quanta.picai.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject

/**
 * Created by QuanTA on 08/01/2026.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository
) : ViewModel() {

}