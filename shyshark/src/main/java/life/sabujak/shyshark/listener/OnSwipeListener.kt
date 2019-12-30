package life.sabujak.shyshark.listener

interface OnSwipeListener {
    fun onSwiped(position: Int, direction: Int)
    fun onChangeHorizontalDrag(direction: Int, percent: Float)
    fun onChangeVerticalDrag(direction: Int, percent: Float)
}