package juloo.keyboard2

/** Stores key combinations that are applied by [KeyModifier]. */
class Modmap
{
  enum class M { Shift, Fn, Ctrl }

  private val _map: Array<TreeMap<KeyValue, KeyValue>?> =
    arrayOfNulls(M.values().size)

  fun add(m: M, a: KeyValue, b: KeyValue)
  {
    val i = m.ordinal
    if (_map[i] == null)
      _map[i] = TreeMap()
    _map[i]!![a] = b
  }

  operator fun get(m: M, a: KeyValue): KeyValue?
  {
    val mm = _map[m.ordinal]
    return mm?.get(a)
  }
}
