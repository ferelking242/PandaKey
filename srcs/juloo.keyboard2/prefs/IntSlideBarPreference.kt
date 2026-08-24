package juloo.keyboard2.prefs

import android.content.Context
import android.content.res.TypedArray
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

/*
 ** IntSlideBarPreference
 ** -
 ** Open a dialog showing a seekbar
 ** -
 ** xml attrs:
 **   android:defaultValue  Default value (int)
 **   min                   min value (int)
 **   max                   max value (int)
 ** -
 ** Summary field allow to show the current value using %s flag
 */
class IntSlideBarPreference(context: Context, attrs: AttributeSet) :
    DialogPreference(context, attrs), SeekBar.OnSeekBarChangeListener
{
  private val _layout: LinearLayout
  private val _textView: TextView
  private val _seekBar: SeekBar

  private val _min: Int

  private val _initialSummary: String

  init
  {
    _initialSummary = getSummary().toString()
    _textView = TextView(context)
    _textView.setPadding(48, 40, 48, 40)
    _seekBar = SeekBar(context)
    _seekBar.setOnSeekBarChangeListener(this)
    _min = attrs.getAttributeIntValue(null, "min", 0)
    val max = attrs.getAttributeIntValue(null, "max", 0)
    _seekBar.setMax(max - _min)
    _layout = LinearLayout(getContext())
    _layout.setOrientation(LinearLayout.VERTICAL)
    _layout.addView(_textView)
    _layout.addView(_seekBar)
  }

  override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean)
  {
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
    val value: Int

    if (restorePersistedValue)
    {
      value = getPersistedInt(_min)
    }
    else
    {
      value = defaultValue as Int
      persistInt(value)
    }
    _seekBar.setProgress(value - _min)
    updateText()
  }

  override fun onGetDefaultValue(a: TypedArray, index: Int): Any
  {
    return a.getInt(index, _min)
  }

  override fun onDialogClosed(positiveResult: Boolean)
  {
    if (positiveResult)
      persistInt(_seekBar.getProgress() + _min)
    else
      _seekBar.setProgress(getPersistedInt(_min) - _min)

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
    val f = String.format(_initialSummary, _seekBar.getProgress() + _min)

    _textView.text = f
    setSummary(f)
  }
}
