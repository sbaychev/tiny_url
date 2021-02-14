package com.tiny.url.data;

import java.time.Instant;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Entity
@AllArgsConstructor
@Builder
@Setter
@Data
@EqualsAndHashCode(callSuper = true)
@Table(name = "tiny")
public class TinyUrl extends AbstractEntity<Long> {

    //size
    @Column
    private String originalUrl;

    @Column
    private String theTinyUrl;

    @Column
    private Integer timesAccessed;

    @Column
    private Instant insertionTime;

    @Column
    private Instant expirationTime;

    public TinyUrl() {
        this.originalUrl = "";
        this.theTinyUrl = "";
        this.timesAccessed = 0;
        this.insertionTime = null;
        this.expirationTime = null;
    }
}
