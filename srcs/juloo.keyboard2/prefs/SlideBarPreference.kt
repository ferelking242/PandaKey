package juloo.keyboard2.prefs

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

/*
 ** SideBarPreference
 ** -
 ** Open a dialog showing a seekbar
 ** -
 ** xml attrs:
 **   android:defaultValue  Default value (float)
 **   min                   min value (float)
 **   max                   max value (float)
 ** -
 ** Summary field allow to show the current value using %f or %s flag
 */
class SlideBarPreference(context: Context, attrs: AttributeSet) :
    DialogPreference(context, attrs), SeekBar.OnSeekBarChangeListener
{
  private val _layout: LinearLayout
  private val _textView: TextView
  private val _seekBar: SeekBar

  private val _min: Float
  private val _max: Float
  private var _value: Float

  private val _initialSummary: String

  init
  {
    _initialSummary = getSummary().toString()
    _textView = TextView(context)
    _textView.setPadding(48, 40, 48, 40)
    _seekBar = SeekBar(context)
    _seekBar.setOnSeekBarChangeListener(this)
    _seekBar.setMax(STEPS)
    _min = float_of_string(attrs.getAttributeValue(null, "min"))
    _value = _min
    _max = Math.max(1f, float_of_string(attrs.getAttributeValue(null, "max")))
    _layout = LinearLayout(getContext())
    _layout.setOrientation(LinearLayout.VERTICAL)
    _layout.addView(_textView)
    _layout.addView(_seekBar)
  }

  override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)
  {
    _value = Math.round(progress * (_max - _min)) / STEPS.toFloat() + _min
    updateText()
  }

  override fun onStartTrackingTouch(seekBar: SeekBar)
  {
  }

  override fun onStopTrackingTouch(seekBar: SeekBar)
  {
  }

  override fun onSetInitialValue(restorePersistedValue: Boolean, defaultValue: Any?)
  {
    if (restorePersistedValue)
    {
      _value = getPersistedFloat(_min)
    }
    else
    {
      _value = defaultValue as Float
      persistFloat(_value)
    }
    _seekBar.setProgress(((_value - _min) * STEPS / (_max - _min)).toInt())
    updateText()
  }

  override fun onGetDefaultValue(a: TypedArray, index: Int): Any
  {
    return a.getFloat(index, _min)
  }

  override fun onDialogClosed(positiveResult: Boolean)
  {
    if (positiveResult)
      persistFloat(_value)
    else
      _seekBar.setProgress(((getPersistedFloat(_min) - _min) * STEPS / (_max - _min)).toInt())

    updateText()
  }

  override fun onCreateDialogView(): View
  {
    val parent = _layout.parent as ViewGroup

    if (parent != null)
      parent.removeView(_layout)
    return _layout
  }

  private fun updateText()
  {
    val f = String.format(_initialSummary, _value)

    _textView.text = f
    setSummary(f)
  }

  companion object
  {
    private const val STEPS = 100

    private fun float_of_string(str: String?): Float
    {
      if (str == null)
        return 0f
      return str.toFloat()
    }
  }
}
