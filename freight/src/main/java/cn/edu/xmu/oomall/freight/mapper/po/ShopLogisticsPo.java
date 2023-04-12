//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.freight.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "freight_shop_logistics")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopLogisticsPo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long shopId;

  private Long logisticsId;

  private String secret;

  private Long creatorId;

  private String creatorName;

  private Long modifierId;

  private String modifierName;

  private LocalDateTime gmtCreate;

  private LocalDateTime gmtModified;

  private Long invalid;

  private Long priority;

}
