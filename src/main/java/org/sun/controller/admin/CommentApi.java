package org.sun.controller.admin;

import org.springframework.web.bind.annotation.*;
import org.sun.response.ResponseResult;

@RestController
@RequestMapping("/admin/comment")
public class CommentApi {

    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId") String commentId){
        return null;
    }

    @GetMapping("/list")
    public ResponseResult listComments(@RequestParam("page") int page, @RequestParam("size") int size){
        return null;
    }

    @PutMapping("/top/{commentId}")
    public ResponseResult topComment(@PathVariable("commentId") String commentId){
        return null;
    }
}
