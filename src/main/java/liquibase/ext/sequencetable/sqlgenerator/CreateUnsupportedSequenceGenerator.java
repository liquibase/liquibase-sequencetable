package liquibase.ext.sequencetable.sqlgenerator;

import liquibase.database.Database;
import liquibase.ext.sequencetable.change.CreateSequenceTableChange;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.SqlGeneratorFactory;
import liquibase.sqlgenerator.core.CreateSequenceGenerator;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.CreateSequenceStatement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * <h3>General Information</h3>
 * <p>The main purpose of the genericSequence extension is to be able to use
 * Hibernate's SequenceStyleGenerator as a database-independent way of creating IDs for tables.
 * <p>For more details please read the according section in the Hibernate userguide (5.1.5. Enhanced identifier generators)
 * <h3>Special Information</h3>
 * <p>This Change creates depending on the used DBMS {@link Database} and depending whether it
 * supports Sequences or not either a {@link CreateSequenceStatement} or {@link CreateTableStatement}.
 *
 * @author m.oberwasserlechner@mum-software.com
 */

/**
 * Transparently converts dropSequence calls to dropSequenceTable calls
 */
public class CreateUnsupportedSequenceGenerator extends CreateSequenceGenerator {

    @Override
    public boolean supports(CreateSequenceStatement statement, Database database) {
        return !database.supportsSequences();
    }

    /**
     * The default column name if tables are used for the genericSequence.
     */
    private static final String SQ_STYLE_TABLE_NEXTVAL_COL = "next_value";


    @Override
    public Sql[] generateSql(CreateSequenceStatement statement, Database database, SqlGeneratorChain sqlGeneratorChain) {
        CreateSequenceTableChange change = new CreateSequenceTableChange();
        change.setCatalogName(statement.getCatalogName());
        change.setSchemaName(statement.getSchemaName());
        change.setTableName(statement.getSequenceName());
        change.setStartValue(statement.getStartValue());

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
