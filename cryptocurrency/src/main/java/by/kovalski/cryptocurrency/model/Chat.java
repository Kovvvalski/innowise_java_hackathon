package by.kovalski.cryptocurrency.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class Chat extends Entity {
  private Long id;
  private String userName;

  /**
   * All cryptocurrencies that user want to see
   */
  private Set<Cryptocurrency> userCryptocurrencies;
}
