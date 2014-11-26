package liquibase.ext.sequencetable.sqlgenerator;

import liquibase.database.core.MySQLDatabase;
import liquibase.sql.Sql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.statement.core.CreateSequenceStatement;
import org.junit.Test;

import static org.junit.Assert.*;

public class CreateUnsupportedSequenceGeneratorTest {

    @Test
    public void generateSql() {
        CreateUnsupportedSequenceGenerator generator = new CreateUnsupportedSequenceGenerator();
        Sql[] sql = generator.generateSql(new CreateSequenceStatement(null, null, "my_seq"), new MySQLDatabase(), null);
        assertEquals(2, sql.length);
        assertEquals("CREATE TABLE my_seq (next_value BIGINT NULL)", sql[0].toSql());
        assertEquals("INSERT INTO my_seq (next_value) VALUES (NULL)", sql[1].toSql());
    }

}