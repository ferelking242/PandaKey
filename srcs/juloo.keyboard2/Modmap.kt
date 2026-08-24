package juloo.keyboard2

import java.util.TreeMap

/** Stores key combinations that are applied by [KeyModifier]. */
class Modmap
{
  enum class M { Shift, Fn, Ctrl }

  private val _map: Array<MutableMap<KeyValue, KeyValue>?>

  init
  {
    @Suppress("UNCHECKED_CAST")
    _map = arrayOfNulls<MutableMap<KeyValue, KeyValue>>(M.values().size) as Array<MutableMap<KeyValue, KeyValue>?>
  }

  fun add(m: M, a: KeyValue, b: KeyValue)
  {
    val i = m.ordinal
    if (_map[i] == null)
      _map[i] = TreeMap()
    _map[i]!![a] = b
  }

  fun get(m: M, a: KeyValue): KeyValue?
  {
    val mm = _map[m.ordinal]
    return mm?.get(a)
  }
}
