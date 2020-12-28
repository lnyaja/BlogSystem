package org.sun.controller.portal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.sun.response.ResponseResult;

@RestController
@RequestMapping("/portal/web_size_info")
public class WebSizeInfoPortalApi {

    @GetMapping("/categories")
    public ResponseResult getCategories(){
        return null;
    }

    @GetMapping("/title")
    public ResponseResult getWebSizeTitle(){
        return null;
    }

    @GetMapping("/view_count")
    public ResponseResult getWebSizeViewCount(){
        return null;
    }

    @GetMapping("/seo")
    public ResponseResult getWebSizeSeoInfo(){
        return null;
    }
    @GetMapping("/loop")
    public ResponseResult getLoops(){
        return null;
    }

    @GetMapping("/friend_link")
    public ResponseResult getLinks(){
        return null;
    }
}
