package juloo.keyboard2

import android.content.res.Resources
import android.os.Build.VERSION
import android.text.InputType
import android.text.TextUtils
import android.view.inputmethod.EditorInfo

class EditorConfig
{
  /** Key that replaces the "ACTION" key. Might be [null] to remove that key. */
  @JvmField var action_key_replacement: KeyValue? = null
  /** Key that replaces the "ENTER" key. Might be [null] to not replace the
      enter key. */
  @JvmField var enter_key_replacement: KeyValue? = null
  @JvmField var actionId: Int = 0
  /** Whether selection mode turns on automatically when text is selected. */
  @JvmField var selection_mode_enabled: Boolean = true
  /** Whether the numeric layout should be shown by default. */
  @JvmField var numeric_layout: Boolean = false
  /** Workaround some apps which answers to [getExtractedText] but do not react
      to [setSelection] while returning [true]. */
  @JvmField var should_move_cursor_force_fallback: Boolean = false

  /** Autocapitalisation. */
  @JvmField var caps_mode: Int = 0 // Argument for [getCursorCapsMode()]
  // Whether caps state is on initially.
  @JvmField var caps_initially_enabled: Boolean = false
  // Whether caps state should be updated right away.
  @JvmField var caps_initially_updated: Boolean = false

  /** CurrentlyTypedWord. */
  @JvmField var initial_text_before_cursor: CharSequence? = null // Might be [null].
  @JvmField var initial_text_after_cursor: CharSequence? = null // Might be [null].
  @JvmField var initial_sel_start: Int = 0
  @JvmField var initial_sel_end: Int = 0

  /** Suggestions. */
  // Doesn't override [_config.suggestions_enabled].
  @JvmField var should_show_candidates_view: Boolean = false

  fun refresh(info: EditorInfo, res: Resources)
  {
    val inputType = info.inputType and InputType.TYPE_MASK_CLASS
    val options = info.imeOptions
    /* Selection mode.
       Editors with [TYPE_NULL] are for example Termux and Emacs. */
    selection_mode_enabled = inputType != InputType.TYPE_NULL
    enter_key_replacement = null
    /* Action key. Looks at [info.actionLabel] first. */
    if (info.actionLabel != null)
    {
      actionId = info.actionId
      action_key_replacement =
        KeyValue.makeActionKey(info.actionLabel.toString())
    }
    else
    {
      actionId = options and EditorInfo.IME_MASK_ACTION
      val label = actionLabel_of_imeAction(actionId, res)
      action_key_replacement = null
      if (label != null)
      {
        action_key_replacement = KeyValue.makeActionKey(label)
        // Swap the enter and action keys
        if ((options and EditorInfo.IME_FLAG_NO_ENTER_ACTION) == 0)
        {
          enter_key_replacement = action_key_replacement
          action_key_replacement = KeyValue.ENTER
        }
      }
    }
    /* Numeric layout */
    numeric_layout = when (inputType)
    {
      InputType.TYPE_CLASS_NUMBER,
      InputType.TYPE_CLASS_PHONE,
      InputType.TYPE_CLASS_DATETIME -> true
      else -> false
    }
    /* setSelection fallback */
    should_move_cursor_force_fallback = _should_move_cursor_force_fallback(info)
    /* Autocapitalisation */
    caps_mode = info.inputType and TextUtils.CAP_MODE_SENTENCES
    caps_initially_enabled = (info.initialCapsMode != 0)
    caps_initially_updated = caps_should_update_state(info)
    /* CurrentlyTypedWord */
    if (VERSION.SDK_INT >= 30)
    {
      initial_text_before_cursor = info.getInitialTextBeforeCursor(20, 0)
      initial_text_after_cursor = info.getInitialTextAfterCursor(20, 0)
    }
    initial_sel_start = info.initialSelStart
    initial_sel_end = info.initialSelEnd
    /* Suggestions */
    should_show_candidates_view = CandidatesView.should_show(info)
  }

  private fun actionLabel_of_imeAction(action: Int, res: Resources): String?
  {
    val id: Int
    when (action)
    {
      EditorInfo.IME_ACTION_NEXT -> id = R.string.key_action_next
      EditorInfo.IME_ACTION_DONE -> id = R.string.key_action_done
      EditorInfo.IME_ACTION_GO -> id = R.string.key_action_go
      EditorInfo.IME_ACTION_PREVIOUS -> id = R.string.key_action_prev
      EditorInfo.IME_ACTION_SEARCH -> id = R.string.key_action_search
      EditorInfo.IME_ACTION_SEND -> id = R.string.key_action_send
      else -> return null
    }
    return res.getString(id)
  }

  private fun _should_move_cursor_force_fallback(info: EditorInfo): Boolean
  {
    // This catch Acode: which sets several variations at once.
    if ((info.inputType and InputType.TYPE_MASK_VARIATION and
          InputType.TYPE_TEXT_VARIATION_PASSWORD) != 0)
      return true
    // Godot editor: Doesn't handle setSelection() but returns true.
    return info.packageName?.startsWith("org.godotengine.editor") == true
  }

  /** Whether the caps state should be updated when input starts. [inputType]
      is the field from the editor info object. */
  private fun caps_should_update_state(info: EditorInfo): Boolean
  {
    val class_ = info.inputType and InputType.TYPE_MASK_CLASS
    val variation = info.inputType and InputType.TYPE_MASK_VARIATION
    if (class_ != InputType.TYPE_CLASS_TEXT)
      return false
    when (variation)
    {
      InputType.TYPE_TEXT_VARIATION_LONG_MESSAGE,
      InputType.TYPE_TEXT_VARIATION_NORMAL,
      InputType.TYPE_TEXT_VARIATION_PERSON_NAME,
      InputType.TYPE_TEXT_VARIATION_SHORT_MESSAGE,
      InputType.TYPE_TEXT_VARIATION_EMAIL_SUBJECT,
      InputType.TYPE_TEXT_VARIATION_WEB_EDIT_TEXT -> return true
      else -> return false
    }
  }
}
