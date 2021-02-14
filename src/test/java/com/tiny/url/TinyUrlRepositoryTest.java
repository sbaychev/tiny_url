package com.tiny.url;

import static com.tiny.url.TestUtils.getTheTinyUrlEntity;
import static org.assertj.core.api.Assertions.assertThat;

import com.tiny.url.data.TinyUrl;
import com.tiny.url.repository.ITinyUrlRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class TinyUrlRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ITinyUrlRepository iTinyUrlRepository;

    @Test
    public void whenTinyUrlIsPersisted_thenFindByTinyUrl_And_ReturnTinyUrlCompareToOriginal_Result_True() {

        TinyUrl aTinyUrl = getTheTinyUrlEntity();

        entityManager.persistAndFlush(aTinyUrl);

        TinyUrl found = iTinyUrlRepository.findByTheTinyUrl(aTinyUrl.getTheTinyUrl());
        assertThat(found.getTheTinyUrl()).isEqualTo(aTinyUrl.getTheTinyUrl());
    }

    @Test
    public void whenTinyUrlHasBeenAccessed_thenFindByTinyUrlAndUpdate_And_ReturnTinyUrlCompareToOriginal_Equal() {

        TinyUrl aTinyUrl = getTheTinyUrlEntity();

        entityManager.persistAndFlush(aTinyUrl);
        int result = iTinyUrlRepository.updateTinySetTimesAccessedForTiny("theTinyUrl");
        entityManager.refresh(aTinyUrl);

        TinyUrl found = iTinyUrlRepository.findByTheTinyUrl(aTinyUrl.getTheTinyUrl());

        assertThat(found.getTheTinyUrl()).isEqualTo(aTinyUrl.getTheTinyUrl());
        assertThat(result).isEqualTo(found.getTimesAccessed());
    }

    @Test
    public void whenInvalidUserName_thenReturnNull() {
        TinyUrl fromDb = iTinyUrlRepository.findByTheTinyUrl("doesNotExist");
        assertThat(fromDb).isNull();
    }
}
