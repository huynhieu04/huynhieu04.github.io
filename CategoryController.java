package com.vanlang.webbanhang.controller;

import com.vanlang.webbanhang.model.Category;
import com.vanlang.webbanhang.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class CategoryController {
    @Autowired
    private final CategoryService categoryService;

    @GetMapping("/categories/add")
    public String showAddForm(Model model) {
        model.addAttribute("category", new Category());
        return "/categories/add-category";
    }

    @PostMapping("/categories/add")
    public String addCategory(@Valid Category category, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "/categories/add-category";
        }
        categoryService.addCategory(category);
        return "redirect:/categories";
    }
    //Hiển Thị Danh Sách Danh Mục
    @GetMapping("/categories")
    public String listCategories(Model model,
                                 @RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "5") int size,
                                 @RequestParam(required = false) String keyword) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Category> categoriesPage;
        if (keyword != null && !keyword.isEmpty()) {
            categoriesPage = categoryService.searchCategory(keyword, pageable);
            model.addAttribute("keyword", keyword);
        } else {
            categoriesPage = categoryService.getCategories(pageable);
        }
        model.addAttribute("categories", categoriesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", categoriesPage.getTotalPages());
        return "/categories/categories-list";
    }

    @GetMapping("/categories/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID:" + id));
        model.addAttribute("category", category);
        return "/categories/update-category";
    }

    @PostMapping("/categories/update/{id}")
    public String updateCategory(@PathVariable("id") Long id, @Valid Category category,
                                 BindingResult result, Model model) {
        if (result.hasErrors()) {
            category.setId(id);
            return "/categories/update-category";
        }
        categoryService.updateCategory(category);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/categories";
    }

    @GetMapping("/categories/delete/{id}")
    public String deleteCategory(@PathVariable("id") Long id, Model model) {
        Category category = categoryService.getCategoryById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category Id:" + id));
        categoryService.deleteCategory(id);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "redirect:/categories";
    }
}

