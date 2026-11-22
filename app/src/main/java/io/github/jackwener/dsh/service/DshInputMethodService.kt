package io.github.jackwener.dsh.service

import android.content.ComponentName
import android.content.Context
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.provider.Settings
import android.view.Display
import android.view.Gravity
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import io.github.jackwener.dsh.R

/**
 * DSH 输入法: a keyboard with no keys (like ADBKeyboard / droidrun Portal) that lets the agent
 * commit any text — including CJK and emoji — into fields where AccessibilityNodeInfo
 * ACTION_SET_TEXT does not work (WebView, Flutter, games, custom editors). It runs in the app's main
 * process, so CapabilityHub calls [commit] directly; nothing is exported besides the system binding.
 * The agent switches to it only through Shizuku (`ime set`) and switches back afterwards; a user who
 * selects it manually gets a one-line bar with a button back to their keyboard.
 *
 * Android can instantiate one IME per display (the agent's virtual display and the user's screen each
 * get their own). Each instance is registered by its display, and [commit] refuses to write through an
 * editor that is bound to a different display — otherwise the agent's text could land in the user's
 * focused field when two keyboards are up at once.
 */
class DshInputMethodService : InputMethodService() {
    override fun onCreate() { super.onCreate(); register() }
    override fun onDestroy() { unregister(); super.onDestroy() }

    override fun onCreateInputView(): View = TextView(this).apply {
        text = getString(R.string.ime_bar)
        gravity = Gravity.CENTER
        val pad = (12 * resources.displayMetrics.density).toInt()
        setPadding(pad, pad, pad, pad)
        setOnClickListener {
            if (!switchToPreviousInputMethod()) getSystemService(InputMethodManager::class.java).showInputMethodPicker()
        }
    }

    override fun onStartInput(attribute: EditorInfo?, restarting: Boolean) {
        super.onStartInput(attribute, restarting)
        editor = attribute
        // The display is known only once the editor binds; re-register under the right id.
        register()
    }

    @Volatile private var editor: EditorInfo? = null

    /** The display this IME instance is bound to (0 = the user's screen). */
    val displayId: Int
        get() = if (Build.VERSION.SDK_INT >= 30) display?.displayId ?: Display.DEFAULT_DISPLAY else Display.DEFAULT_DISPLAY

    /** True when an editor is connected and the text was committed. */
    fun commit(text: String, replace: Boolean, targetDisplayId: Int = Display.DEFAULT_DISPLAY): Boolean {
        // Never write through an editor on a different display (two keyboards running at once).
        if (targetDisplayId != Display.DEFAULT_DISPLAY && displayId != targetDisplayId) return false
        val ic = currentInputConnection ?: return false
        if (editor == null) return false
        ic.beginBatchEdit()
        if (replace) { ic.performContextMenuAction(android.R.id.selectAll); ic.commitText("", 1) }
        val ok = ic.commitText(text, 1)
        ic.endBatchEdit()
        return ok
    }

    /** Performs the field's editor action (Enter / Search / Go / Send). */
    fun editorAction(search: Boolean): Boolean {
        val ic = currentInputConnection ?: return false
        val declared = (editor?.imeOptions ?: 0) and EditorInfo.IME_MASK_ACTION
        if (search) return ic.performEditorAction(EditorInfo.IME_ACTION_SEARCH)
        if (declared == EditorInfo.IME_ACTION_NONE || declared == EditorInfo.IME_ACTION_UNSPECIFIED || ((editor?.imeOptions ?: 0) and EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0)
            return ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_DOWN, android.view.KeyEvent.KEYCODE_ENTER)) &&
                ic.sendKeyEvent(android.view.KeyEvent(android.view.KeyEvent.ACTION_UP, android.view.KeyEvent.KEYCODE_ENTER))
        return ic.performEditorAction(declared)
    }

    private fun register() { instance = this; byDisplay[displayId] = this }
    private fun unregister() { if (instance === this) instance = null; byDisplay.remove(displayId, this) }

    companion object {
        /** The most recently bound instance (kept for the main-screen callers). */
        @Volatile var instance: DshInputMethodService? = null
            private set
        private val byDisplay = java.util.concurrent.ConcurrentHashMap<Int, DshInputMethodService>()

        /** The IME instance bound to [displayId], or null when none is connected there. */
        fun forDisplay(displayId: Int): DshInputMethodService? =
            byDisplay[displayId] ?: if (displayId == Display.DEFAULT_DISPLAY) instance else null

        fun id(context: Context) = ComponentName(context, DshInputMethodService::class.java).flattenToShortString()

        fun isCurrent(context: Context) = currentId(context)?.let(ComponentName::unflattenFromString) ==
            ComponentName(context, DshInputMethodService::class.java)

        fun currentId(context: Context): String? = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD)
    }
}
