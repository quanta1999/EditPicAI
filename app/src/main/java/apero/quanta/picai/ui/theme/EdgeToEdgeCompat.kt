package apero.quanta.picai.ui.theme

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge

/**
 * Created by QuanTA on 05/01/2026.
 */

fun ComponentActivity.setEdgeToEdgeConfig() {
    enableEdgeToEdge()
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Force the 3-button navigation bar to be transparent
        // See: https://developer.android.com/develop/ui/views/layout/edge-to-edge#create-transparent
        window.isNavigationBarContrastEnforced = false
    }
}
