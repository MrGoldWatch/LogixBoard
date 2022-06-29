package assessment.freightForwarder;

import assessment.openapi.models.*;
import io.micronaut.http.annotation.PathVariable;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.SQLException;
import java.util.*;

@Singleton
public class Service {

  // @formatter:off
  @Inject private JdbcTemplate jdbcTemplate;
  // @formatter:on

  private final double ozToLb = 0.0625D;
  private final double ozToKg = 0.0283495D;
  private final double lbToKg = 0.453592D;
  private final double lbToOz = 16D;
  private final double kgToLb = 2.20462D;
  private final double kgToOz = 35.274D;

  public void addShipment(Shipment shipment) {
    String reference_id = shipment.getReferenceId();
    String[] organizations = shipment.getOrganizations().toArray(new String[0]);
    String arrival_time;
    if (shipment.getEstimatedTimeArrival() != null ) {
      arrival_time = shipment.getEstimatedTimeArrival();
    } else {
      arrival_time = "";
    }
    ShipmentTransportPacks tp = shipment.getTransportPacks();
    String sql = "INSERT INTO shipment " +
        "(reference_id, organizations, arrival_time)" +
        "VALUES (?,?,?) ON CONFLICT (reference_id) DO NOTHING";
    jdbcTemplate.update(sql, reference_id, organizations, arrival_time);
    addTransportPacks(tp, reference_id);
  }

  public void addOrganization(Organization org) {
    UUID id = org.getId();
    String code = org.getCode();
    String sql = "INSERT INTO organization " +
        "(id, code)" +
        "VALUES (?,?) ON CONFLICT (id) DO UPDATE SET code=? ";
    jdbcTemplate.update(sql,
        id, code,
        //handle updates below
        code);
  }

  public void addTransportPacks( ShipmentTransportPacks tp, String reference_id) {
    List<TotalWeight> totalWeights = tp.getNodes();
    List<Object[]> batchArgsList = new ArrayList<>();
    String sql = "INSERT INTO transportpacks " +
        "(reference_id, weight, unit)" +
        "VALUES (?,?,?) ON CONFLICT (reference_id, weight, unit) DO NOTHING";
    if (totalWeights != null) {
      for (TotalWeight tw : totalWeights) {
        int weight = tw.getTotalWeight().getWeight();
        String unit = tw.getTotalWeight().getUnit().getValue();

        // batch sql calls
        Object[] objectArray = { reference_id, weight, unit};
        batchArgsList.add(objectArray);
      }
    }
    jdbcTemplate.batchUpdate(sql, batchArgsList);
  }

  public Shipment getShipment(String shipmentId) throws SQLException {
    Map<String, Object> map = jdbcTemplate.queryForMap("SELECT * FROM shipment WHERE reference_id=?", shipmentId);
    Shipment s = new Shipment();
    s.setReferenceId(shipmentId);
    Array pgArray = (Array) map.get("organizations");
    String[] javaArray = (String[]) pgArray.getArray();
    s.setOrganizations(Arrays.asList(javaArray));
    s.setEstimatedTimeArrival(map.get("arrival_time").toString());
//
    List<Map<String, Object>> map2 = jdbcTemplate.queryForList("SELECT * FROM transportpacks WHERE reference_id=?", shipmentId);
//    s.setTransportPacks();
    ShipmentTransportPacks stp = new ShipmentTransportPacks();
//
    for (Map<String, Object> m : map2) {
      TotalWeightTotalWeight twtw = new TotalWeightTotalWeight();
      TotalWeight tw = new TotalWeight();
//      twtw.s
      Long l = (long) m.get("weight");
      twtw.setWeight(l.intValue());
      twtw.setUnit(TotalWeightTotalWeight.UnitEnum.valueOf(m.get("unit").toString()));
      tw.setTotalWeight(twtw);
      stp.addNodesItem(tw);
    }
//
    s.setTransportPacks(stp);
    return s;
//
//    return null;
  }

  public Organization getOrganization(String organizationId) {
    Map<String, Object> map = jdbcTemplate.queryForMap("SELECT * FROM organization WHERE id=?", UUID. fromString(organizationId));
    Organization o = new Organization();
    o.setId((UUID) map.get("id"));
    o.setCode((map.get("code")).toString());

    return o;
  }

  public WeightAggregate getAggregation(Aggregate aggregate) {
    String unit = aggregate.getUnit().getValue();
    WeightAggregate tw = new WeightAggregate();
    double totalWeight = 0;
    List<Map<String, Object>> map2 = jdbcTemplate.queryForList("SELECT * FROM transportpacks");
    for (Map<String, Object> m : map2) {
      Long l = (Long) m.get("weight");
      if (l != 0) {
//        double d =
        totalWeight += convert(l, m.get("unit").toString(), unit);
      }
    }

    tw.setUnit(WeightAggregate.UnitEnum.valueOf(unit));
    tw.setWeight(new BigDecimal(totalWeight));
    return tw;
  }

//  kg | lb | oz
  public double convert(long value, String from, String to) {
    if (from.equals(to)) return value;
    double total = 0D;
    switch (from) {
      case "KILOGRAMS":
        if (to.equals("OUNCES")) {
          total += value * kgToOz;
        } else if (to.equals("POUNDS")) {
          total += value * kgToLb;
        }
        break;
      case "OUNCES":
        if (to.equals("KILOGRAMS")) {
          total += value * ozToKg;
        } else if (to.equals("POUNDS")) {
          total += value * ozToLb;
        }
        break;
      case "POUNDS":
        if (to.equals("KILOGRAMS")) {
          total += value * lbToKg;
        } else if (to.equals("OUNCES")) {
          total += value * lbToOz;
        }
        break;
    }
    return total;
  }
}
