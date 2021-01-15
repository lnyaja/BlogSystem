package org.sun.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sun.response.ResponseResult;
import org.sun.services.ICategoryService;
import org.sun.services.IFriendLinkService;
import org.sun.services.ILoopService;
import org.sun.services.IWebSizeInfoService;

@RestController
@RequestMapping("/portal/web_size_info")
public class WebSizeInfoPortalApi {

    @Autowired
    private ICategoryService categoryService;

    @Autowired
    private IFriendLinkService friendLinkService;

    @Autowired
    private ILoopService loopService;

    @Autowired
    private IWebSizeInfoService sizeInfoService;

    @GetMapping("/categories")
    public ResponseResult getCategories(){
        return categoryService.listCategories();
    }

    @GetMapping("/title")
    public ResponseResult getWebSizeTitle(){
        return sizeInfoService.getWebSizeTitle();
    }

    @GetMapping("/view_count")
    public ResponseResult getWebSizeViewCount(){
        return sizeInfoService.getSizeViewCount();
    }

    @GetMapping("/seo")
    public ResponseResult getWebSizeSeoInfo(){
        return sizeInfoService.getSeoInfo();
    }



    @GetMapping("/loop")
    public ResponseResult getLoops(){
        return loopService.listLoop();
    }

    @GetMapping("/friend_link")
    public ResponseResult getLinks(){
        return friendLinkService.listFriendLink();
    }
}
