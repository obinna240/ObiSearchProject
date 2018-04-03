package com.sa.search.db.mongo.dao;

import org.springframework.data.repository.PagingAndSortingRepository;

import com.sa.search.db.mongo.model.Template;

public interface TemplateDAO extends PagingAndSortingRepository<Template, String>, TemplateDAOCustom {

}
