package com.revolut.domain;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ACCOUNTS")
public class Account {

  @Id
  @Column(name = "ID", nullable = false)
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  @NotNull
  @Positive
  @Column(name = "ACCOUNT_NUMBER", nullable = false, unique = true)
  private Long accountNumber;

  @NotBlank
  @Pattern(regexp = "^[a-zA-Z\\\\s_]*$")
  @Column(name = "NAME", nullable = false)
  private String name;

  @NotNull
  @Positive
  @Column(name = "AMOUNT", nullable = false)
  private BigDecimal amount;

  @NotNull
  @Version
  private int version;

}

