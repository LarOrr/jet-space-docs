import org.joda.time.DateTime
import java.lang.IllegalArgumentException
import java.util.function.Predicate
import javax.swing.Box

data class BoxState(var id: Long, var time: DateTime, var color: String, var itemsCount: Int)

fun getBoxStates(itemsCountLog: List<BoxState>, colorLog: List<BoxState>,
                 id: Long, before: DateTime, limit: Int) : List<BoxState> {

    val filterPredicate: (BoxState) -> Boolean = { it.id == id && it.time.isBefore(before) }
    val itemsCounts = itemsCountLog.filter(filterPredicate)
    val colors = colorLog.filter(filterPredicate)

    // ! "Состояние должно включать в себя как цвет, так и количество объектов в коробке."
    // I assume that BoxState must have both Color and Item Count at every timestamp
    // It means that if there is no color or item count yet at some timestamp, I don't add this state to the result

    var itemPos= itemsCounts.size - 1
    var colorPos = colors.size - 1
    val result = mutableListOf<BoxState>()
    if (itemPos == -1 || colorPos == -1) {
        // Return empty
        return result
    }

    while ((itemPos > 0 || colorPos > 0) && result.size < limit) {
        result.add(BoxState(id, maxOf(itemsCounts[itemPos].time, colors[colorPos].time),
            colors[colorPos].color, itemsCounts[itemPos].itemsCount))
        if (colorPos == 0 || colors[colorPos].time < itemsCounts[itemPos].time) {
            itemPos--
        } else if (itemPos == 0 || colors[colorPos].time > itemsCounts[itemPos].time) {
            colorPos--
        } else {
            colorPos--
            itemPos--
        }
    }

    if (result.size < limit) {
        result.add(BoxState(id, maxOf(itemsCounts[0].time, colors[0].time),
            colors[0].color, itemsCounts[0].itemsCount))
    }

    return result
}