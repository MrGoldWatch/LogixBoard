package assessment.infra.healtchcheck;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;

import static assessment.infra.healtchcheck.HealthCheck.URI;

@Controller(URI)
public class HealthCheck {

  public static final String URI="/hc";

  // @formatter:off
  @Inject  private JdbcTemplate jdbcTemplate;
  // @formatter:on

  @Get
  public String checkHealth() {

    jdbcTemplate.queryForObject("select 1", Object.class);
    return "all good";

  }


}
