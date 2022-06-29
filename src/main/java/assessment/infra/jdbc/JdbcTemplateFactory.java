package assessment.infra.jdbc;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
//import io.micronaut.context.annotation.Factory;
//import io.micronaut.context.annotation.Value;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.sql.SQLException;


@Factory
public class JdbcTemplateFactory {

  @Value("${db.jdbc-url}")
  protected String jdbcUrl;

  @Singleton
  DataSource getDataSource() {
    HikariConfig config = new HikariConfig();
    config.setDriverClassName("org.postgresql.Driver");
    config.setJdbcUrl(jdbcUrl);
    return new HikariDataSource(config);
  }


  @Singleton
  public JdbcTemplate getJdbcTemplate(DataSource dataSource) throws SQLException {
    return new JdbcTemplate(dataSource);
  }

  @Singleton
  public DataSourceTransactionManager getDataSourceTransactionManager(DataSource dataSource) throws SQLException {
    return new DataSourceTransactionManager(dataSource);
  }

  @Singleton
  public TransactionTemplate getTransactionTemplate(DataSourceTransactionManager transactionManager) throws SQLException {
    return new TransactionTemplate(transactionManager);
  }

}

