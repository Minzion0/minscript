package com.minscript.config.entity;

import jakarta.persistence.*;
import lombok.*;

@Data
@Table(name = "news")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class NewsEntity extends BaseEntity{


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(updatable = false,columnDefinition = "BIGINT UNSIGNED")
    private Long inews;

    @Column
    private String title;

    @Column
    private String ctnt;

    @Column
    private String url;

}
