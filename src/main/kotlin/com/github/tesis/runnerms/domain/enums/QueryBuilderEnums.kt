package com.github.tesis.runnerms.domain.enums

enum class TypeGateLogic {
    AND, OR
}

enum class TypeParam {
    COLUMN, PARAM
}

enum class TypeColumn {
    COLUMN, GENERATED
}

enum class TypeParamInput {
    TEXT, DATE, NUMBER, FUNCTION, NONE
}

enum class TypeConditions {
    EQ, NOT_EQ, ILIKE, NOT_ILIKE, GREATER, GREATER_EQ, LESS, LESS_EQ, GROUP, IS, IS_NOT
}

enum class TypeInputFunction {
    NOW
}

enum class FunctionGeneratedColumn {
    CONCAT, DATE_PART, SUM, AVG, MAX, MIN, COUNT
}

enum class TypeJoin {
    LEFT, INNER, RIGHT
}

enum class TypeOrder {
    DESC, ASC
}
