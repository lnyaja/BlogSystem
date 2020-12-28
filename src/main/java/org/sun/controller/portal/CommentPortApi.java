package org.sun.controller.portal;

import org.springframework.web.bind.annotation.*;
import org.sun.pojo.Comment;
import org.sun.response.ResponseResult;

@RestController
@RequestMapping("/portal/comment")
public class CommentPortApi {
    @PostMapping
    public ResponseResult postComment(@RequestBody Comment comment){
        return null;
    }

    @DeleteMapping("/{commentId}")
    public ResponseResult deleteComment(@PathVariable("commentId") String commentId){
        return null;
    }

    @GetMapping("/list/{articleId}")
    public ResponseResult listComments(@PathVariable("articleId") String articleId){
        return null;
    }
}
