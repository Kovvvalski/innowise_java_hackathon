package by.kovalski.cryptocurrency.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Cryptocurrency extends Entity{
  /**
   * id
   */
  private Long id;
  /**
   * name
   */
  @JsonProperty("symbol")
  private String name;
  /**
   * current cryptocurrency price
   */
  @JsonProperty("price")
  private BigDecimal price;
  /**
   * difference of price of cryptocurrency in last N seconds, represented in %
   */
  private double priceDifference;
}
