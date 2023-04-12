//School of Informatics Xiamen University, GPL-3.0 license

package cn.edu.xmu.oomall.freight.mapper.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "freight_logistics")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogisticsPo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;

  private String appId;

  private Long creatorId;

  private String creatorName;

  private Long modifierId;

  private String modifierName;

  private LocalDateTime gmtCreate;

  private LocalDateTime gmtModified;

  private String snPattern;

  private String secret;

  private String logisticsClass;

}
