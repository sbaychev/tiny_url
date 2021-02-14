package com.tiny.url.repository;

import com.tiny.url.data.TinyUrl;
import java.util.Optional;
import javax.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional
public interface ITinyUrlRepository extends JpaRepository<TinyUrl, Long> {

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<TinyUrl> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_READ)
    TinyUrl findByTheTinyUrl(String theTinyUrl);

    /**
     * Saves the given {@link TinyUrl}.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    <S extends TinyUrl> S save(S tinyUrl);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    <S extends TinyUrl> S saveAndFlush(S tinyUrl);

    @Lock(LockModeType.PESSIMISTIC_READ)
    Optional<TinyUrl> findByOriginalUrl(String originalUrl);

    @Modifying
    @Query(value = "UPDATE tiny u SET u.times_accessed = u.times_accessed + 1 WHERE u.the_tiny_url = ?", nativeQuery = true)
    int updateTinySetTimesAccessedForTiny(String theTinyUrl);
}