package liquibase.ext.sequencetable.change;

import liquibase.change.AbstractChange;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.datatype.core.BigIntType;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.CreateTableStatement;
import liquibase.statement.core.InsertStatement;

import java.math.BigInteger;

@DatabaseChange(name="createSequenceTable",
        description = "Creates a table to use to generate sequence values if the database does not support sequences",
        priority = ChangeMetaData.PRIORITY_DEFAULT)
public class CreateSequenceTableChange extends AbstractChange {

    private String catalogName;
    private String schemaName;
    private String tableName;
    private BigInteger startValue = BigInteger.ONE;
    private String nextValueColumnName = "next_value";

    private boolean alwaysUse = false;

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public BigInteger getStartValue() {
        return startValue;
    }

    public void setStartValue(BigInteger startValue) {
        this.startValue = startValue;
    }

    public String getNextValueColumnName() {
        return nextValueColumnName;
    }

    public void setNextValueColumnName(String nextValueColumnName) {
        this.nextValueColumnName = nextValueColumnName;
    }

    public boolean isAlwaysUse() {
        return alwaysUse;
    }

    public void setAlwaysUse(boolean alwaysUse) {
        this.alwaysUse = alwaysUse;
    }

    @Override
    public String getConfirmationMessage() {
        return "Sequence table "+tableName+" created";
    }

    @Override
    public SqlStatement[] generateStatements(Database database) {


        if (alwaysUse || !database.supportsSequences()) {
            CreateTableStatement tblStmt = new CreateTableStatement(this.catalogName, this.schemaName, this.tableName);

            String colName = this.nextValueColumnName;
            tblStmt.addColumn(colName, new BigIntType());

            InsertStatement insertStmt = new InsertStatement(this.catalogName, this.schemaName, this.tableName);
            insertStmt.addColumnValue(colName, startValue);

            return new SqlStatement[] {
                    tblStmt,
                    insertStmt
            };
        } else {
            return new SqlStatement[0];
        }
    }
}
