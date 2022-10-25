package com.github.tesis.runnerms.domain.requests

import com.github.tesis.runnerms.domain.enums.FunctionGeneratedColumn
import com.github.tesis.runnerms.domain.enums.TypeColumn
import com.github.tesis.runnerms.domain.enums.TypeConditions
import com.github.tesis.runnerms.domain.enums.TypeGateLogic
import com.github.tesis.runnerms.domain.enums.TypeInputFunction
import com.github.tesis.runnerms.domain.enums.TypeJoin
import com.github.tesis.runnerms.domain.enums.TypeOrder
import com.github.tesis.runnerms.domain.enums.TypeParam
import com.github.tesis.runnerms.domain.enums.TypeParamInput
import java.util.UUID

data class QueryBuilderRequest(
    val queryName: String,
    val integrationCode: UUID,
    val queryJson: QueryJson,
    var queriesFromIntegrationCode: Map<UUID, String>? = null
) {
    data class QueryJson(
        val columns: List<Column>,
        val filters: List<Filter>,
        val tables: Tables,
        val groupBy: List<Column.TableColumn>,
        val order: List<Order>
    ) {
        data class Column(
            val type: TypeColumn,
            val tableColumn: TableColumn?,
            val generatedColumn: GeneratedColumn?
        ) {
            data class TableColumn(
                val schemaName: String?,
                val tableName: String?,
                val columnName: String?,
                val alias: String?,
                val columnAlias: String?,
                val queryColumn: Boolean
            )
            data class GeneratedColumn(
                val name: String,
                val functionName: FunctionGeneratedColumn,
                val groupRequired: Boolean?,
                val params: List<Filter.Param>
            )
        }

        data class Filter(
            val type: TypeConditions,
            val params: List<Param>?,
            val gateLogicPrevious: TypeGateLogic?,
            val groupConditions: List<GroupCondition>?
        ) {
            data class Param(
                val type: TypeParam,
                val tableColumn: Column.TableColumn?,
                val param: DetailParam?
            ) {
                data class DetailParam(
                    val typeInput: TypeParamInput,
                    val inputFunctions: TypeInputFunction?,
                    val value: String?
                )
            }

            data class GroupCondition(
                val type: TypeConditions,
                val params: List<Param>,
                val gateLogicPrevious: TypeGateLogic?
            )
        }

        data class Tables(
            val schemaName: String? = null,
            val tableName: String? = null,
            val alias: String? = null,
            val queryCode: UUID? = null,
            val join: Join? = null
        ) {
            data class Join(
                val type: TypeJoin,
                val table: Tables,
                val joinConditional: JoinConditional,
                val join: Join? = null
            ) {
                data class JoinConditional(
                    val columnLeft: String,
                    val columnRight: String
                )
            }
        }

        data class Order(
            val type: TypeOrder,
            val schemaName: String?,
            val tableName: String?,
            val columnName: String?,
            val alias: String?,
            val columnAlias: String?,
            val queryColumn: Boolean
        ) {
            fun toTableColumn() = Column.TableColumn(
                schemaName = schemaName,
                tableName = tableName,
                columnName = columnName,
                alias = alias,
                columnAlias = columnAlias,
                queryColumn = queryColumn
            )
        }
    }
}
