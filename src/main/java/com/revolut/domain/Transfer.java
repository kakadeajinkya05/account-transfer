package com.revolut.domain;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transfer {

  private Long fromAccount;
  private Long toAccount;
  private BigDecimal amount;

}
