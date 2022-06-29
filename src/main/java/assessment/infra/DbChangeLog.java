package assessment.infra;

import io.micronaut.context.event.ApplicationEventListener;
import io.micronaut.runtime.server.event.ServerStartupEvent;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.DatabaseException;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.Connection;
import java.sql.SQLException;

@Singleton
public class DbChangeLog implements ApplicationEventListener<ServerStartupEvent> {

  @Inject
  JdbcTemplate jdbcTemplate;

  @Override
  public void onApplicationEvent(ServerStartupEvent event) {
    Connection connection = null;
    try {
      connection = jdbcTemplate.getDataSource().getConnection();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    try {
      Database database =
          DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
      Liquibase liquibase = new Liquibase("classpath:/liquibaseChangeLog.xml",
          new ClassLoaderResourceAccessor(ClassLoader.getSystemClassLoader()), database);
      liquibase.update(new Contexts());
    } catch (DatabaseException e) {
      if (connection != null) {
        try {
          connection.rollback();
          connection.close();
        } catch (SQLException exception) {
          exception.printStackTrace();
        }
      }
    } catch (LiquibaseException e) {
      throw new RuntimeException(e);
    }

  }
}
