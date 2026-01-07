package apero.quanta.picai.ui.settings

import apero.quanta.picai.domain.model.ThemeMode

data class SettingState(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val isDynamicColorEnabled: Boolean = true
)

sealed class SettingIntent {
    data class ChangeThemeMode(val mode: ThemeMode) : SettingIntent()
    data class ToggleDynamicColor(val enabled: Boolean) : SettingIntent()
}

sealed class SettingEvent {
    // Placeholder for future events
}
