package assessment.freightForwarder;

import assessment.openapi.models.Aggregate;
import assessment.openapi.models.Organization;
import assessment.openapi.models.Shipment;
import assessment.openapi.models.WeightAggregate;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;
import io.micronaut.http.annotation.Post;
//import io.micronaut.http.annotation.RequestAttribute;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import java.sql.SQLException;
import java.util.List;

@Controller
public class Config {

  @Inject
  private Service service;


  @Post("/shipment")
  public HttpResponse addShipment(Shipment shipment) throws SQLException {
      service.addShipment(shipment);
    return HttpResponse.ok();
  }

  @Post("/organization")
  public HttpResponse addOrganization(Organization organization) throws SQLException {
      service.addOrganization(organization);
    return HttpResponse.ok();
  }

  @Get("/shipments/{shipmentId}")
  public Shipment getShipment(@NotNull @PathVariable String shipmentId) throws SQLException {
        return service.getShipment(shipmentId);
  }

  @Get("/organizations/{organizationId}")
  public Organization getOrganization(@NotNull @PathVariable String organizationId) throws SQLException {
    return service.getOrganization(organizationId);
  }

  @Post("/aggregation")
  public WeightAggregate getAggregation(Aggregate aggregate) throws SQLException {
    return service.getAggregation(aggregate);
  }
}
