package org.sun.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.sun.pojo.Category;
import org.sun.response.ResponseResult;
import org.sun.services.ICategoryService;

/**
 * 管理中心，分类的API
 */
@RestController
@RequestMapping("/admin/category")
public class CategoryAdminApi {



    @Autowired
    private ICategoryService categoryService;

    /**
     * 添加分类
     * 需要管理员权限
     *
     * @param category 分类
     * @return 1
     */
    @PreAuthorize("@permission.admin()")
    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category){
        return categoryService.addCategory(category);
    }

    /**
     * 删除分类
     * <p>
     * 权限：管理员权限
     * @param categoryId 分类id
     * @return 1
     */
    @PreAuthorize("@permission.admin()")
    @DeleteMapping("/{categoryId}")
    public ResponseResult deleteCategory(@PathVariable("categoryId") String categoryId){
        return categoryService.deleteCategory(categoryId);
    }

    /**
     * 更新分类
     * <p>
     * 权限：管理员权限
     * @param categoryId 分类id
     * @param category  分类
     * @return  1
     */
    @PreAuthorize("@permission.admin()")
    @PutMapping("/{categoryId}")
    public ResponseResult updateCategory(@PathVariable("categoryId") String categoryId, @RequestBody Category category){
        return categoryService.updateCategory(categoryId, category);
    }

    /**
     * 获取分类
     * <p>
     * 使用的case:修改的时候，获取一下，填充弹窗
     * 不获取也是可以的，从列表中获取数据
     * <p>
     * 权限：管理员权限
     * @param categoryId    分类id
     * @return  1
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/{categoryId}")
    public ResponseResult getCategory(@PathVariable("categoryId") String categoryId){
        return categoryService.getCategory(categoryId);
    }

    /**
     * 获取分类列表
     * <p>
     * 权限：管理员权限
     *
     * @return  1
     */
    @PreAuthorize("@permission.admin()")
    @GetMapping("/list")
    public ResponseResult listCategory(){
        return categoryService.listCategories();
    }

}
