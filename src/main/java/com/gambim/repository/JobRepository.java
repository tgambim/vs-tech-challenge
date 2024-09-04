package com.gambim.repository;

import com.gambim.domain.Job;
import com.gambim.framework.annotation.Component;
import com.gambim.framework.data.InMemoryRepository;

@Component
public class JobRepository extends InMemoryRepository<Job> {}
