import org.joda.time.DateTime
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class BoxStatesKtTest {

    lateinit var itemsCountLog: MutableList<BoxState>
    lateinit var colorLog: MutableList<BoxState>

    fun readData() {
        // TODO Reading data from real csv here?

        //    | id     | dateTime              | itemsCount |
        //    | ------ | --------------------- | ---------- |
        //    | 1      | 2020-04-01T11:50:00   | 0          |
        //    | 2      | 2020-04-01T12:00:50   | 5          |
        //    | 1      | 2020-04-01T19:25:00   | 2          |
        //    | 1      | 2020-04-01T19:25:25   | 20         |
        itemsCountLog = ArrayList()
        itemsCountLog.apply {
            add(BoxState(1, DateTime("2020-04-01T11:50:00"), "", 0))
            add(BoxState(2, DateTime("2020-04-01T12:00:50"), "", 5))
            add(BoxState(1, DateTime("2020-04-01T19:25:00"), "", 2))
            add(BoxState(1, DateTime("2020-04-01T19:25:25"), "", 20))
        }

        //    | id     | dateTime            | colorName |
        //    | ------ | ------------------- | --------- |
        //    | 1      | 2020-04-01T18:00:00 | Blue      |
        //    | 2      | 2020-04-01T18:10:00 | Red       |
        //    | 1      | 2020-04-01T19:00:00 | Black     |
        //    | 2      | 2020-04-01T21:10:00 | Blue      |
        colorLog = ArrayList()
        colorLog.apply {
            add(BoxState(1, DateTime("2020-04-01T18:00:00"), "Blue", -1))
            add(BoxState(2, DateTime("2020-04-01T18:10:00"), "Red", -1))
            add(BoxState(1, DateTime("2020-04-01T19:00:00"), "Black", -1))
            add(BoxState(2, DateTime("2020-04-01T21:10:00"), "Blue", -1))
        }
    }

    @Test
    fun testGetBoxStatesSimple() {
        readData()
        val states = getBoxStates(itemsCountLog, colorLog, 1, DateTime("2020-04-01T19:25:20"), 10,)
        assertEquals(3, states.size)
        assertEquals(BoxState(1, DateTime("2020-04-01T19:25:00"), "Black", 2), states[0])
        assertEquals(BoxState(1, DateTime("2020-04-01T19:00:00"), "Black", 0), states[1])
        assertEquals(BoxState(1, DateTime("2020-04-01T18:00:00"), "Blue", 0), states[2])
    }

    @Test
    fun testLimit() {
        readData()
        var states = getBoxStates(itemsCountLog, colorLog, 1, DateTime("2020-04-01T19:25:20"), 1)
        assertEquals(1, states.size)
        assertEquals(BoxState(1, DateTime("2020-04-01T19:25:00"), "Black", 2), states[0])

        states = getBoxStates(itemsCountLog, colorLog, 1, DateTime("2020-04-01T19:25:20"), 0)
        assertEquals(0, states.size)
    }

    @Test
    fun `One element`() {
        readData()
        val states = getBoxStates(itemsCountLog, colorLog, 2, DateTime("2020-04-01T18:10:01"), 100)
        assertEquals(1, states.size)
        assertEquals(BoxState(2, DateTime("2020-04-01T18:10:00"), "Red", 5), states[0])
    }

    @Test
    fun `Color or item count is not initialized yet`() {
        readData()
        // There is | id=2 | time=2020-04-01T12:00:50   | count=5 |
        // But its color will be only initialized at 2020-04-01T18:10:00
        // So it shouldn't be in the result as all states must have both color and item count
        val states = getBoxStates(itemsCountLog, colorLog, 2, DateTime("2020-04-01T12:00:50"), 100)
        assertEquals(0, states.size)
    }
}