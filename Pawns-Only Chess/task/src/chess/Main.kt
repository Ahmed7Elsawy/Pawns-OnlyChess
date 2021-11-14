package chess

data class Point(var letter: Char, var number: Int, val color: COLOR)
data class Movement(var letterFrom: Char, var numberFrom: Int, var letterTo: Char, var numberTo: Int)
enum class COLOR(val letter: Char) {
    WHITE('W'),
    BLACK('B')
}

fun main() {

    val chessState = mutableListOf(
        Point('a', 7, COLOR.BLACK),
        Point('b', 7, COLOR.BLACK),
        Point('c', 7, COLOR.BLACK),
        Point('d', 7, COLOR.BLACK),
        Point('e', 7, COLOR.BLACK),
        Point('f', 7, COLOR.BLACK),
        Point('g', 7, COLOR.BLACK),
        Point('h', 7, COLOR.BLACK),
        Point('a', 2, COLOR.WHITE),
        Point('b', 2, COLOR.WHITE),
        Point('c', 2, COLOR.WHITE),
        Point('d', 2, COLOR.WHITE),
        Point('e', 2, COLOR.WHITE),
        Point('f', 2, COLOR.WHITE),
        Point('g', 2, COLOR.WHITE),
        Point('h', 2, COLOR.WHITE)
    )

    var playerTurn = 0
    println("Pawns-Only Chess")
    println("First Player's name:")
    val firstPlayer = readLine()!!
    println("Second Player's name:")
    val secondPlayer = readLine()!!
    val playerNames = arrayOf(firstPlayer, secondPlayer)
    printChess(chessState)

    var input = ""
    var lastMovement = Movement('z', 0, 'z', 0)
    while (input != "exit") {
        val playerName = playerNames[playerTurn]
        printPlayerTurn(playerName)
        input = readLine()!!

        if (input == "exit")
            break

        val newMovement = convertToMovement(input)
        if (isRightMovement(newMovement, playerTurn, chessState, lastMovement)) {
            val oldPoint =
                chessState.first { it.letter == newMovement.letterFrom && it.number == newMovement.numberFrom }
            oldPoint.letter = newMovement.letterTo
            oldPoint.number = newMovement.numberTo
            printChess(chessState)
            playerTurn = playerTurn.updateTurn()
        }

        lastMovement = newMovement
        if (isWhiteWin(lastMovement, chessState)) {
            println("White Wins!")
            break
        }

        if (isBlackWin(lastMovement, chessState)) {
            println("Black Wins!")
            break
        }
        
        if (isStalemate(playerTurn, chessState)) {
            println("Stalemate!")
            break
        }

    }
    println("Bye!")
}

fun isStalemate(playerTurn: Int, chessState: MutableList<Point>): Boolean {
    return if (playerTurn == 0) {
        chessState.filter { it.color == COLOR.WHITE }
            .all { whitePawn ->
                chessState.any { it.number == whitePawn.number + 1 && it.letter == whitePawn.letter }
                        && chessState.none { it.number == whitePawn.number + 1 && it.letter - 1 == whitePawn.letter }
                        && chessState.none { it.number == whitePawn.number + 1 && it.letter + 1 == whitePawn.letter }
            }
    } else {
        chessState.filter { it.color == COLOR.BLACK }
            .all { blackPawn ->
                chessState.any { it.number == blackPawn.number - 1 && it.letter == blackPawn.letter }
                        && chessState.none { it.number == blackPawn.number + 1 && it.letter - 1 == blackPawn.letter }
                        && chessState.none { it.number == blackPawn.number + 1 && it.letter + 1 == blackPawn.letter }
            }
    }
}

fun isBlackWin(movement: Movement, chessState: MutableList<Point>): Boolean {
    if (movement.numberTo == 1)
        return true
    if (chessState.all { it.color == COLOR.BLACK })
        return true
    return false
}

fun isWhiteWin(movement: Movement, chessState: MutableList<Point>): Boolean {
    if (movement.numberTo == 8)
        return true
    if (chessState.all { it.color == COLOR.WHITE })
        return true

    return false
}

fun printChess(startState: MutableList<Point>) {
    for (row in 8 downTo 1) {
        printBarrierLine()
        val s = startState.filter { it.number == row }.toMutableList()
        printBoxLine(row, s)
    }
    printBarrierLine()
    printLastLine()
}

private fun isRightMovement(
    movement: Movement,
    playerTurn: Int,
    chessState: MutableList<Point>,
    lastMovement: Movement
): Boolean {
    val regex = Regex("[a-h][1-8][a-h][1-8]")

    val color = if (playerTurn == 0) COLOR.WHITE else COLOR.BLACK
    val isExist = isPawnThere(chessState, color, movement)

    if (!isExist) {
        println("No ${color.name.toLowerCase()} pawn at ${movement.letterFrom}${movement.numberFrom}")
        return false
    }

    if (movement.letterFrom != movement.letterTo) {
        val capture = isRightCapturePlayer1(chessState, movement, lastMovement) || isRightCapturePlayer2(
            chessState,
            movement,
            lastMovement
        )
        if (capture) {
            return true
        }
    }

    if (movement.letterFrom != movement.letterTo
        || isBusyPlace(movement, chessState)
        || playerTurn == 0 && !isRightMovePlayer1(movement)
        || playerTurn == 1 && !isRightMovePlayer2(movement)
    ) {
        println("Invalid Input")
        return false
    }
    return true
}

private fun isPawnThere(
    chessState: MutableList<Point>,
    color: COLOR,
    movement: Movement
): Boolean {
    val isExist = chessState.any { point ->
        point.color == color && point.letter == movement.letterFrom
                && point.number == movement.numberFrom
    }
    return isExist
}

fun isBusyPlace(movement: Movement, chessState: MutableList<Point>): Boolean {
    return chessState.any {
        it.letter == movement.letterTo && it.number == movement.numberTo
    }
}

fun isRightMovePlayer2(movement: Movement): Boolean {
    if (movement.numberFrom == 7 && (movement.numberFrom - 1 == movement.numberTo || movement.numberFrom - 2 == movement.numberTo)) {
        return true
    } else if (movement.numberFrom - 1 == movement.numberTo) {
        return true
    }
    return false
}

fun isRightMovePlayer1(movement: Movement): Boolean {
    if (movement.numberFrom == 2 && (movement.numberFrom + 1 == movement.numberTo || movement.numberFrom + 2 == movement.numberTo)) {
        return true
    } else if (movement.numberFrom + 1 == movement.numberTo) {
        return true
    }
    return false
}

fun isRightCapturePlayer1(
    chessState: MutableList<Point>,
    movement: Movement,
    lastMovement: Movement
): Boolean {
    if (movement.letterFrom == (movement.letterTo + 1) || movement.letterFrom == (movement.letterTo - 1)) {
        if (movement.numberFrom == (movement.numberTo - 1)
            && lastMovement.letterFrom == movement.letterTo
            && (lastMovement.numberFrom == 7 && lastMovement.numberTo == 5)
        ) {
            removePoint(lastMovement.letterTo, lastMovement.numberTo, chessState)
            return true
        } else if (movement.numberFrom == movement.numberTo - 1) {
            val captured = chessState.any { point ->
                point.color == COLOR.BLACK && point.letter == movement.letterTo
                        && point.number == movement.numberTo
            }
            removePoint(movement.letterTo, movement.numberTo, chessState)
            return captured
        }
    }
    return false
}

fun isRightCapturePlayer2(
    chessState: MutableList<Point>, movement: Movement, lastMovement: Movement
): Boolean {
    if (movement.numberFrom == (movement.numberTo + 1)
        && lastMovement.letterFrom == movement.letterTo
        && lastMovement.numberFrom == 2 && lastMovement.numberTo == 4
    ) {
        removePoint(lastMovement.letterTo, lastMovement.numberTo, chessState)
        return true
    } else if (movement.letterFrom == movement.letterTo + 1 || movement.letterFrom == movement.letterTo - 1) {
        if (movement.numberFrom == movement.numberTo + 1) {
            val captured = chessState.any { point ->
                point.color == COLOR.WHITE && point.letter == movement.letterTo
                        && point.number == movement.numberTo
            }
            removePoint(movement.letterTo, movement.numberTo, chessState)
            return captured
        }
    }
    return false
}


private fun Int.updateTurn(): Int {
    return (this + 1) % 2
}

private fun printPlayerTurn(playerName: String) {
    println("$playerName's turn:")
}

private fun printBarrierLine() {
    println("  +---+---+---+---+---+---+---+---+")
}

private fun printBoxLine(lineNumber: Int, points: List<Point>) {
    val barrierLine = mutableListOf(
        '8',
        ' ',
        '|',
        ' ',
        ' ',
        ' ',
        '|',
        ' ',
        ' ',
        ' ',
        '|',
        ' ',
        ' ',
        ' ',
        '|',
        ' ',
        ' ',
        ' ',
        '|',
        ' ',
        ' ',
        ' ',
        '|',
        ' ',
        ' ',
        ' ',
        '|',
        ' ',
        ' ',
        ' ',
        '|',
        ' ',
        ' ',
        ' ',
        '|'
    )
    barrierLine[0] = '0' + lineNumber
    for (point in points)
        barrierLine[getLetterPositionInArray(point.letter)] = point.color.letter
    println(barrierLine.joinToString(""))
}

private fun printLastLine() {
    println("    a   b   c   d   e   f   g   h")
}

private fun getLetterPositionInArray(letter: Char): Int {
    return (letter + 1 - 'a') * 4
}

private fun convertToMovement(movementString: String): Movement {
    return Movement(
        movementString[0]
        , Character.getNumericValue(movementString[1])
        , movementString[2]
        , Character.getNumericValue(movementString[3])
    )
}

private fun removePoint(letter: Char, number: Int, chessState: MutableList<Point>) {
    chessState.removeIf { it.letter == letter && it.number == number }
}