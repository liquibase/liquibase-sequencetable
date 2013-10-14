package liquibase.ext.id.genericsequence.drop;

import java.util.ArrayList;
import java.util.List;

import liquibase.change.AbstractChange;
import liquibase.database.Database;
import liquibase.statement.SqlStatement;
import liquibase.statement.core.DropSequenceStatement;
import liquibase.statement.core.DropTableStatement;


/**
 * Drops a genericSequences sequence or table depending on which DBMS is used.
 * @author m.oberwasserlechner@mum-software.com
 */
public class DropGenericSequenceChange extends AbstractChange {
  
  /**
   * The schema name.
   */
  private String schemaName;
  /**
   * The sequence or table name (required)
   */
  private String sequenceName;
  /**
   * If true it's tried to always remove a tabled with the given [{@link #schemaName} and] {@link #sequenceName}.
   */
  private boolean forceTableUse = false;
  
  public DropGenericSequenceChange() {
    super("dropGenericSequence", "Drop GenericSequence", 15);
  }

  /**
   * @see liquibase.change.Change#getConfirmationMessage()
   */
  public String getConfirmationMessage() {
    return "GenericSequence successfully dropped!";
  }

  /**
   * @see liquibase.change.Change#generateStatements(liquibase.database.Database)
   */
  public SqlStatement[] generateStatements(Database database) {
    List<SqlStatement> list = new ArrayList<SqlStatement>();

    if (!this.forceTableUse && database.supportsSequences()) {
      list.add(new DropSequenceStatement(this.schemaName, this.sequenceName));
    } else {
      list.add(new DropTableStatement(this.schemaName, this.schemaName, false));
    }
    return (SqlStatement[]) list.toArray();
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

  public boolean isForceTableUse() {
    return forceTableUse;
  }

  public void setForceTableUse(boolean forceTableUse) {
    this.forceTableUse = forceTableUse;
  }

}
