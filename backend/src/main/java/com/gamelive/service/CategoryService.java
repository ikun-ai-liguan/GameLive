package com.gamelive.service;

import com.gamelive.entity.Category;

import java.util.List;

public interface CategoryService {

    List<Category> listAll();

    void create(Category category);

    void update(Category category);

    void delete(Long id);
}
