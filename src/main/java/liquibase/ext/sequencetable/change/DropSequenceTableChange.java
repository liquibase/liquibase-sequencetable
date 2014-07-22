package liquibase.ext.sequencetable.change;

import liquibase.change.AbstractChange;
import liquibase.change.ChangeMetaData;
import liquibase.change.DatabaseChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.DropTableStatement;

import java.math.BigInteger;

@DatabaseChange(name="dropSequenceTable",
description = "Drop the table used to generate sequence values if the database does not support sequences",
priority = ChangeMetaData.PRIORITY_DEFAULT)
public class DropSequenceTableChange extends AbstractChange {

	private String catalogName;
	private String schemaName;
	private String tableName;
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

	public boolean isAlwaysUse() {
		return alwaysUse;
	}

	public void setAlwaysUse(boolean alwaysUse) {
		this.alwaysUse = alwaysUse;
	}

	@Override
	public String getConfirmationMessage() {
		return "Sequence table "+tableName+" dropped";
	}

	@Override
	public SqlStatement[] generateStatements(Database database) {


		if (alwaysUse || !database.supportsSequences()) {
			DropTableStatement tblStmt = new DropTableStatement(this.catalogName, this.schemaName, this.tableName, false);

			return new SqlStatement[] {
					tblStmt,
			};
		} else {
			return new SqlStatement[0];
		}
	}
}
