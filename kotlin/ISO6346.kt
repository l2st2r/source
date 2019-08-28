fun validateISO6346(numberString: String, strictCategoryIdentifier: Boolean = true): Boolean {
            if (numberString.length != 11) return false

            var numberSum: Int = 0

            for (i in numberString.indices) {
                val curChar = numberString[i].toUpperCase()
                // first 3 digit must be Latin alphabat
                if (i < 3 && curChar !in 'A'..'Z') return false
                // 4th digit must be one of U/J/Z under ISO Code
                // TODO: must be alphatbet when strict= false
                if (i == 3 && strictCategoryIdentifier && curChar !in arrayOf('U','J','Z')) return false
                // 5+ char must be numeric digit
                if (i > 3 && !curChar.isDigit()) return false

                val intValue: Int
                val asciiValue = curChar.toInt()

                when (asciiValue) {
                    in '0'.toInt() .. '9'.toInt() -> {
                        intValue = asciiValue - '0'.toInt()
                    }
                    in 'A'.toInt() .. 'Z'.toInt() -> {
                        // A = 10, B = 11, C = 12
                        val unSerializedValue: Int = asciiValue - 'A'.toInt() + 10
                        val omittedTimesFor11: Int = unSerializedValue / 11
                        val remainedValue: Int = unSerializedValue.rem(11)
                        // V = unserialized 32, value=35.
                        // 32 + div:2 + (mod:10+div:2)/11 = 34+1 = 35
                        intValue = unSerializedValue + omittedTimesFor11 + (remainedValue + omittedTimesFor11) / 11
                    }
                    else -> return false
                }
                // println("Character $curChar corresponds for digit $intValue")

                // check last digit
                if (i == numberString.length - 1) {
                    var remainder = numberSum.rem(11)
                    // println("remainder is $remainder")
                    if (remainder == 10) remainder = 0

                    return intValue == remainder
                }

                var multipleValue: Int = 1
                for (j in 0 until i) {
                    multipleValue *= 2
                }
                // println("$intValue * $multipleValue = ${intValue * multipleValue}")
                numberSum += intValue * multipleValue
                // numberSum += intValue * 2f.pow(i).toInt()

            }

            return false
        }
