package org.sun.controller.admin;

import org.springframework.web.bind.annotation.*;
import org.sun.pojo.Category;
import org.sun.response.ResponseResult;

/**
 * 管理中心，分类的API
 */
@RestController
@RequestMapping("/admin/category")
public class CategoryAdminApi {

    /**
     * 添加分类
     * @param category 分类
     * @return 1
     */
    @PostMapping
    public ResponseResult addCategory(@RequestBody Category category){
        return null;
    }

    /**
     * 删除分类
     * @param categoryId 分类id
     * @return 1
     */
    @DeleteMapping("/{categoryId}")
    public ResponseResult deleteCategory(@PathVariable("categoryId") String categoryId){
        return null;
    }

    /**
     * 更新分类
     * @param categoryId 分类id
     * @param category  分类
     * @return  1
     */
    @PutMapping("/{categoryId}")
    public ResponseResult updateCategory(@PathVariable("categoryId") String categoryId, @RequestBody Category category){
        return null;
    }

    /**
     * 获取分类
     * @param categoryId    分类id
     * @return  1
     */
    @GetMapping("/{categoryId}")
    public ResponseResult getCategory(@PathVariable("categoryId") String categoryId){
        return null;
    }

    /**
     * 获取分类列表
     * @param page  页
     * @param size  大小
     * @return  1
     */
    @GetMapping("/list")
    public ResponseResult listCategory(@RequestParam("page") int page, @RequestParam("size") int size){
        return null;
    }

}
