package liquibase.ext.id.genericsequence.create;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import liquibase.change.AbstractChange;
import liquibase.database.Database;
import liquibase.database.core.FirebirdDatabase;
import liquibase.database.core.H2Database;
import liquibase.database.core.HsqlDatabase;
import liquibase.database.structure.type.BigIntType;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.CreateSequenceStatement;
import liquibase.statement.core.CreateTableStatement;
import liquibase.statement.core.InsertStatement;


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
 *
 */
public class CreateGenericSequenceChange extends AbstractChange {
  
  /**
   * The default column name if tables are used for the genericSequence.
   */
  private static final String SQ_STYLE_TABLE_NEXTVAL_COL = "next_val";
  
  /**
   * The schema name.
   */
  private String schemaName;
  /**
   * The sequence or table name. (required)
   */
  private String sequenceName;
  /**
   * The start value. Used for both styles. (required)
   */
  private BigInteger startValue;
  /**
   * The min value for a sequence can provide. Used by sequences only.
   */
  private BigInteger minValue;
  /**
   * The max value for a sequence can provide. Used by sequences only.
   */
  private BigInteger maxValue;
  /**
   * The increment size of a sequence. (required). Although its only used by sequences.
   */
  private BigInteger incrementBy = BigInteger.valueOf(1L);
  /**
   * Forces the use of tables even if the DBMS would support sequences.
   */
  private boolean forceTableUse = false;
  /**
   * Column name, if tables are used as genericSequences.
   */
  private String tableValueColumnName = SQ_STYLE_TABLE_NEXTVAL_COL;
  /**
   * {@link #tableValueColumnName} column's size, if tables are used as genericSequences. Defaults to 10.
   */
  private Integer tableValueColumnSize = 10;
  

  public CreateGenericSequenceChange() {
    super("createGenericSequence", "Create GenericSequence", 15);
  }

  /**
   * @see liquibase.change.Change#getConfirmationMessage()
   */
  public String getConfirmationMessage() {
    return null;
  }

  /**
   * @see liquibase.change.Change#generateStatements(liquibase.database.Database)
   */
  public SqlStatement[] generateStatements(Database database) {
    List<SqlStatement> list = new ArrayList<SqlStatement>();

    if (!this.forceTableUse && database.supportsSequences()) {
      CreateSequenceStatement sqStmt = new CreateSequenceStatement(this.schemaName, this.sequenceName);
      if (!(database instanceof FirebirdDatabase)) {
        sqStmt.setStartValue(this.startValue);
        sqStmt.setIncrementBy(this.incrementBy);
      }
      
      if (!(database instanceof FirebirdDatabase || database instanceof H2Database || database instanceof HsqlDatabase)) {
        sqStmt.setMinValue(this.minValue);
        sqStmt.setMaxValue(this.maxValue);
      }
      
      list.add(sqStmt);
    } else {
      // create a table
      CreateTableStatement tblStmt = new CreateTableStatement(this.schemaName, this.sequenceName);
      
      // add a numeric column for the value
      String typeSize = null;
      if (this.tableValueColumnSize != null) {
        typeSize = String.valueOf(this.tableValueColumnSize);
      } else {
        typeSize = "10";
      }
      BigIntType type = new BigIntType();
      type.setFirstParameter(typeSize);
      
      String colName = this.tableValueColumnName;
      if (colName == null) {
        colName = SQ_STYLE_TABLE_NEXTVAL_COL;
      }
      tblStmt.addColumn(colName, new BigIntType());

      list.add(tblStmt);
      
      InsertStatement insertStmt = new InsertStatement(this.schemaName, this.sequenceName);
      insertStmt.addColumnValue(colName, startValue);
      
      list.add(insertStmt);
    }
    return list.toArray(new SqlStatement[list.size()]);
  }

  public String getSchemaName() {
    return schemaName;
  }

  public void setSchemaName(String schemaName) {
    this.schemaName = schemaName;
  }

  public String getSequenceName() {
    return sequenceName;
  }

  public void setSequenceName(String sequenceName) {
    this.sequenceName = sequenceName;
  }

  public BigInteger getStartValue() {
    return startValue;
  }

  public void setStartValue(BigInteger startValue) {
    this.startValue = startValue;
  }

  public BigInteger getMinValue() {
    return minValue;
  }

  public void setMinValue(BigInteger minValue) {
    this.minValue = minValue;
  }

  public BigInteger getMaxValue() {
    return maxValue;
  }

  public void setMaxValue(BigInteger maxValue) {
    this.maxValue = maxValue;
  }

  public BigInteger getIncrementBy() {
    return incrementBy;
  }
  
  public void setIncrementBy(BigInteger incrementBy) {
    this.incrementBy = incrementBy;
  }

  public boolean isForceTableUse() {
    return forceTableUse;
  }

  public void setForceTableUse(boolean forceTableUse) {
    this.forceTableUse = forceTableUse;
  }

  public String getTableValueColumnName() {
    return tableValueColumnName;
  }
  
  public void setTableValueColumnName(String tableValueColumnName) {
    this.tableValueColumnName = tableValueColumnName;
  }
  
  public Integer getTableValueColumnSize() {
    return tableValueColumnSize;
  }
  
  public void setTableValueColumnSize(Integer tableValueColumnSize) {
    this.tableValueColumnSize = tableValueColumnSize;
  }

}
