package com.github.tesis.runnerms.constants

object ExternalRoutes {
    object QueryBuilder {
        const val NAME = "query-builder-ms"

        const val TRANSLATE_SQL = "/$NAME/v1/query"
    }

    object Query {
        const val NAME = "query-ms"

        const val GET_QUERIES = "/$NAME/v1/query/list"
    }
}
