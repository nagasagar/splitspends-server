package com.dasa.splitspends.repository;

import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import com.dasa.splitspends.SplitspendsApplication;
import com.dasa.splitspends.config.DBConfiguration;

@RunWith(SpringRunner.class)
@DataJpaTest
@ContextConfiguration(classes= {SplitspendsApplication.class, DBConfiguration.class})
@ActiveProfiles("jpa")
public abstract class BaseJPARepositoryTest {
}