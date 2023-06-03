package cn.iocoder.yudao.module.jl.entity.project;

import cn.iocoder.yudao.module.jl.entity.BaseEntity;
import lombok.*;
import java.util.*;
import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;

/**
 * 项目的实验名目 Entity
 *
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(name = "ProjectCategory")
@Table(name = "jl_project_category")
public class ProjectCategory extends BaseEntity {

    /**
     * 岗位ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false )
    private Long id;

    /**
     * 报价 id
     */
    @Column(name = "quote_id")
    private Long quoteId;

    /**
     * 安排单 id
     */
    @Column(name = "schedule_id")
    private Long scheduleId;

    /**
     * 类型，报价/安排单
     */
    @Column(name = "type", nullable = false )
    private String type;

    /**
     * 名目的实验类型，动物/细胞/分子等
     */
    @Column(name = "category_type")
    private String categoryType;

    /**
     * 实验名目库的名目 id
     */
    @Column(name = "category_id", nullable = false )
    private Long categoryId;

    /**
     * 实验人员
     */
    @Column(name = "operator_id")
    private Long operatorId;

    /**
     * 客户需求
     */
    @Column(name = "demand")
    private String demand;

    /**
     * 干扰项
     */
    @Column(name = "interference")
    private String interference;

    /**
     * 依赖项(json数组多个)
     */
    @Column(name = "depend_ids")
    private String dependIds;

    /**
     * 实验名目名字
     */
    @Column(name = "name", nullable = false )
    private String name;

    /**
     * 备注
     */
    @Column(name = "mark")
    private String mark;

    @ManyToOne
    @JoinColumn(name="quote_id", insertable = false, updatable = false)
    private ProjectQuote quote;

    @ManyToOne
    @JoinColumn(name="schedule_id", insertable = false, updatable = false)
    private ProjectSchedule schedule;

    /**
     * 实验物资
     */
    @OneToMany(mappedBy="category")
    private List<ProjectSupply> supplyList;

    /**
     * 实验物资
     */
    @OneToMany(mappedBy="category1")
    private List<ProjectChargeitem> chargeitemList;

}
