package liquibase.ext.sequencetable.sqlgenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import liquibase.change.AbstractChange;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.ext.sequencetable.change.CreateSequenceTableChange;
import liquibase.ext.sequencetable.change.DropSequenceTableChange;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.sqlgenerator.core.CreateSequenceGenerator;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.CreateSequenceStatement;
import liquibase.statement.core.DropSequenceStatement;
import liquibase.statement.core.DropTableStatement;


/**
 * Transparently converts dropSequence calls to dropSequenceTable calls
 */
public class DropUnsupportedSequenceGenerator extends CreateSequenceGenerator {

    @Override
    public boolean supports(CreateSequenceStatement statement, Database database) {
        return !database.supportsSequences();
    }

    @Override
    public Sql[] generateSql(CreateSequenceStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        DropSequenceTableChange change = new DropSequenceTableChange();
        change.setCatalogName(statement.getCatalogName());
        change.setSchemaName(statement.getSchemaName());
        change.setTableName(statement.getSequenceName());

        List<Sql> sql = new ArrayList<Sql>();
        SqlStatement[] statements = change.generateStatements(database);
        if (statements != null) {
            for (SqlStatement sqlStatement : statements) {
                sql.addAll(Arrays.asList(SqlGeneratorFactory.getInstance().generateSql(sqlStatement, database)));
            }
        }

        return sql.toArray(new Sql[sql.size()]);
    }

}
