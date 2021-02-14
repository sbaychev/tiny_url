package com.tiny.url.data;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@EqualsAndHashCode(callSuper = false, of = {"id"})
public abstract class AbstractEntity<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 2828084142846178952L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    @Getter
    @Setter(AccessLevel.PRIVATE)
    private T id;

//    In case of Optimistic Locking
//    @Getter
//    @Setter(AccessLevel.PRIVATE)
//    @Version
//    @Column(name = "version")
//    private T version;
}
