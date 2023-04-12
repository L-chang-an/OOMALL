//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.freight.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "freight_undeliverable")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UndeliverablePo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long regionId;

  private LocalDateTime beginTime;

  private LocalDateTime endTime;

  private Long creatorId;

  private String creatorName;

  private Long modifierId;

  private String modifierName;

  private LocalDateTime gmtCreate;

  private LocalDateTime gmtModified;

  private Long shopLogisticsId;


}
